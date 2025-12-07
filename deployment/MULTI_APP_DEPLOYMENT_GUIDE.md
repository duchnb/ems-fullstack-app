# Multi-Application Deployment Guide
## Deploying Multiple Apps on Same EC2 Server

**Context:** This guide shows how to deploy additional applications (like EMS) on the same EC2 server that's already running JobPortal.

**Existing Infrastructure:**
- EC2 t3.micro (35.178.231.85)
- MariaDB 10.5
- Nginx with SSL
- Domain: gitsoft.uk (Route 53)

---

## 🏗️ Architecture Overview

```
EC2 Server (35.178.231.85)
├── Nginx (Port 80/443)
│   ├── jobportal.gitsoft.uk → localhost:8080
│   └── ems.gitsoft.uk → localhost:8081 + /var/www/ems
├── Spring Boot Apps
│   ├── JobPortal (Port 8080) - Thymeleaf
│   └── EMS Backend (Port 8081) - REST API
├── React Frontend
│   └── EMS (served by Nginx from /var/www/ems)
└── MariaDB (Port 3306)
    ├── jobportal database
    └── ems database
```

---

## 📋 Deployment Checklist for New App

### **Phase 1: DNS & Database Setup**

- [ ] Create Route 53 A Record (ems.gitsoft.uk → 35.178.231.85)
- [ ] Create database and user in MariaDB
- [ ] Test database connection

### **Phase 2: Application Preparation**

- [ ] Build React frontend (`npm run build`)
- [ ] Configure Spring Boot for new port
- [ ] Build Spring Boot JAR
- [ ] Test locally

### **Phase 3: Server Deployment**

- [ ] Upload JAR and React build to EC2
- [ ] Create systemd service
- [ ] Create Nginx configuration
- [ ] Start application
- [ ] Get SSL certificate

### **Phase 4: Verification**

- [ ] Test HTTP access
- [ ] Test HTTPS access
- [ ] Test API endpoints
- [ ] Verify database connectivity

---

## 🚀 Step-by-Step Deployment

### **Step 1: DNS Configuration**

**AWS Console → Route 53 → Hosted Zones → gitsoft.uk**

Create A Record:
- **Name:** ems
- **Type:** A
- **Value:** 35.178.231.85
- **TTL:** 300

**Verify:**
```bash
nslookup ems.gitsoft.uk
# Should return: 35.178.231.85
```

---

### **Step 2: Database Setup**

**SSH to EC2:**
```bash
ssh -i ~/.ssh/your-key.pem ec2-user@35.178.231.85
```

**Create database:**
```bash
mysql -u root -p
```

```sql
-- Create database
CREATE DATABASE ems CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create user
CREATE USER 'ems'@'localhost' IDENTIFIED BY 'YourSecurePassword123!';

-- Grant privileges
GRANT ALL PRIVILEGES ON ems.* TO 'ems'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User='ems';
EXIT;
```

**Test connection:**
```bash
mysql -u ems -p ems
# Enter password, should connect successfully
EXIT;
```

---

### **Step 3: Build React Frontend**

**On your local machine (in EMS frontend folder):**

```bash
# Install dependencies
npm install

# Create production build
npm run build

# This creates a 'build' folder with optimized static files
```

**Configure API URL in React:**

Create `.env.production`:
```
REACT_APP_API_URL=/api
```

Or update your API calls:
```javascript
const API_BASE_URL = process.env.REACT_APP_API_URL || '/api';
```

---

### **Step 4: Configure Spring Boot**

**Create `src/main/resources/application-prod.properties`:**

```properties
# Application name
spring.application.name=ems

# Server port (MUST be different from JobPortal's 8080)
server.port=8081

# Database connection
spring.datasource.url=jdbc:mysql://localhost:3306/ems
spring.datasource.username=ems
spring.datasource.password=YourSecurePassword123!
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Logging
logging.level.root=INFO
logging.file.name=/var/log/ems/application.log

# CORS (if needed for development)
# spring.web.cors.allowed-origins=https://ems.gitsoft.uk
```

**Build JAR:**
```bash
./mvnw clean package -DskipTests
```

---

### **Step 5: Upload Files to EC2**

**Upload Spring Boot JAR:**
```bash
scp -i ~/.ssh/your-key.pem target/ems-*.jar ec2-user@35.178.231.85:/tmp/ems.jar
```

**Upload React build:**
```bash
scp -i ~/.ssh/your-key.pem -r build/* ec2-user@35.178.231.85:/tmp/ems-frontend/
```

---

### **Step 6: Setup Application on EC2**

**SSH to EC2:**
```bash
ssh -i ~/.ssh/your-key.pem ec2-user@35.178.231.85
```

**Create directories:**
```bash
sudo mkdir -p /opt/ems
sudo mkdir -p /var/www/ems
sudo mkdir -p /var/log/ems
```

**Move files:**
```bash
sudo mv /tmp/ems.jar /opt/ems/app.jar
sudo mv /tmp/ems-frontend/* /var/www/ems/
```

**Set permissions:**
```bash
sudo chown -R spring:spring /opt/ems
sudo chown -R spring:spring /var/log/ems
sudo chown -R nginx:nginx /var/www/ems
```

---

### **Step 7: Create Systemd Service**

**Create service file:**
```bash
sudo nano /etc/systemd/system/ems.service
```

**Content:**
```ini
[Unit]
Description=EMS Spring Boot Application
After=network.target mariadb.service
Requires=mariadb.service

[Service]
User=spring
Group=spring
WorkingDirectory=/opt/ems

Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_URL=jdbc:mysql://localhost:3306/ems"
Environment="DB_USERNAME=ems"
Environment="DB_PASSWORD=YourSecurePassword123!"

ExecStart=/usr/bin/java \
    -XX:MaxRAMPercentage=50.0 \
    -XX:+UseContainerSupport \
    -Dserver.port=8081 \
    -jar /opt/ems/app.jar

Restart=on-failure
RestartSec=10s

StandardOutput=journal
StandardError=journal
SyslogIdentifier=ems

NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
```

**Enable and start:**
```bash
sudo systemctl daemon-reload
sudo systemctl enable ems
sudo systemctl start ems
```

**Check status:**
```bash
sudo systemctl status ems
sudo journalctl -u ems -f
```

**Verify it's listening:**
```bash
sudo netstat -tlnp | grep 8081
# Should show java listening on port 8081
```

---

### **Step 8: Configure Nginx**

**Create Nginx config:**
```bash
sudo nano /etc/nginx/conf.d/ems.conf
```

**Content:**
```nginx
# HTTP server (before SSL)
server {
    listen 80;
    listen [::]:80;
    server_name ems.gitsoft.uk;

    # Let's Encrypt challenge
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    # Serve React frontend
    location / {
        root /var/www/ems;
        try_files $uri $uri/ /index.html;
        
        # Cache static assets
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }

    # Proxy API requests to Spring Boot
    location /api/ {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Logging
    access_log /var/log/nginx/ems-access.log;
    error_log /var/log/nginx/ems-error.log;
}
```

**Test and reload:**
```bash
sudo nginx -t
sudo systemctl reload nginx
```

**Test HTTP access:**
```bash
curl -I http://ems.gitsoft.uk
# Should return 200 OK
```

---

### **Step 9: Get SSL Certificate**

**Run Certbot:**
```bash
sudo certbot --nginx -d ems.gitsoft.uk
```

**Follow prompts:**
1. Enter email
2. Agree to Terms (Y)
3. Share email with EFF (optional)

**Certbot will automatically:**
- Get certificate
- Update Nginx config with HTTPS
- Add HTTP → HTTPS redirect

**Verify:**
```bash
sudo certbot certificates
curl -I https://ems.gitsoft.uk
```

---

### **Step 10: Verification**

**Test frontend:**
```bash
curl https://ems.gitsoft.uk
# Should return React HTML
```

**Test API:**
```bash
curl https://ems.gitsoft.uk/api/your-endpoint
# Should return JSON from Spring Boot
```

**Check logs:**
```bash
# Application logs
sudo journalctl -u ems -f

# Nginx logs
sudo tail -f /var/log/nginx/ems-access.log
sudo tail -f /var/log/nginx/ems-error.log
```

**Open in browser:**
```
https://ems.gitsoft.uk
```

---

## 🔧 Port Assignment Strategy

| Application | Port | Type | Access |
|-------------|------|------|--------|
| Nginx | 80 | HTTP | Public |
| Nginx | 443 | HTTPS | Public |
| JobPortal | 8080 | Spring Boot | localhost only |
| EMS Backend | 8081 | Spring Boot | localhost only |
| MariaDB | 3306 | Database | localhost only |

**Next app:** Use port 8082, 8083, etc.

---

## 💾 Resource Management

### **Memory Usage (EC2 t3.micro - 1GB RAM)**

```
Component          Memory    Notes
─────────────────────────────────────────
System/OS          ~150MB    Amazon Linux
MariaDB            ~200MB    Shared database
JobPortal          ~400MB    -XX:MaxRAMPercentage=75
EMS Backend        ~250MB    -XX:MaxRAMPercentage=50
Nginx              ~10MB     Lightweight
─────────────────────────────────────────
Total              ~1010MB   Tight but works!
```

**If memory issues:**
1. Reduce Java heap: `-XX:MaxRAMPercentage=40.0`
2. Add swap space: `sudo dd if=/dev/zero of=/swapfile bs=1M count=2048`
3. Upgrade to t3.small (2GB RAM)

---

## 📊 Nginx Configuration Patterns

### **Pattern 1: Spring Boot Only (Thymeleaf)**
```nginx
location / {
    proxy_pass http://localhost:8080;
}
```
**Use for:** Server-side rendered apps (like JobPortal)

---

### **Pattern 2: React + Spring Boot API**
```nginx
location / {
    root /var/www/ems;
    try_files $uri /index.html;
}

location /api/ {
    proxy_pass http://localhost:8081;
}
```
**Use for:** SPA with separate backend (like EMS)

---

### **Pattern 3: Static Site Only**
```nginx
location / {
    root /var/www/mysite;
    index index.html;
}
```
**Use for:** Pure HTML/CSS/JS sites

---

## 🔍 Troubleshooting

### **Problem: Port already in use**
```bash
# Find what's using the port
sudo netstat -tlnp | grep 8081

# Kill the process
sudo kill -9 <PID>

# Or change port in application-prod.properties
```

---

### **Problem: 502 Bad Gateway**
```bash
# Check if backend is running
sudo systemctl status ems

# Check if listening on correct port
sudo netstat -tlnp | grep 8081

# Check logs
sudo journalctl -u ems -n 50
```

---

### **Problem: React app shows blank page**
```bash
# Check browser console for errors
# Common issue: API URL not configured

# Check Nginx is serving files
ls -la /var/www/ems/

# Check Nginx error log
sudo tail -f /var/log/nginx/ems-error.log
```

---

### **Problem: CORS errors**
Add to Spring Boot `application-prod.properties`:
```properties
spring.web.cors.allowed-origins=https://ems.gitsoft.uk
spring.web.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
spring.web.cors.allowed-headers=*
```

Or use `@CrossOrigin` annotation in controllers.

---

### **Problem: Database connection refused**
```bash
# Check MariaDB is running
sudo systemctl status mariadb

# Test connection
mysql -u ems -p ems

# Check credentials in service file
sudo cat /etc/systemd/system/ems.service | grep DB_
```

---

## 🚀 Deployment Script Template

**Create `deploy-ems.sh`:**

```bash
#!/bin/bash
set -e

EC2_IP="35.178.231.85"
SSH_KEY="~/.ssh/your-key.pem"

echo "Building application..."
./mvnw clean package -DskipTests

echo "Uploading JAR..."
scp -i $SSH_KEY target/ems-*.jar ec2-user@$EC2_IP:/tmp/ems.jar

echo "Deploying..."
ssh -i $SSH_KEY ec2-user@$EC2_IP << 'EOF'
sudo systemctl stop ems
sudo mv /tmp/ems.jar /opt/ems/app.jar
sudo chown spring:spring /opt/ems/app.jar
sudo systemctl start ems
sudo systemctl status ems
EOF

echo "Deployment complete!"
```

---

## 📋 Quick Reference Commands

```bash
# View all running apps
sudo systemctl status jobportal ems mariadb nginx

# View all listening ports
sudo netstat -tlnp

# Restart all services
sudo systemctl restart jobportal ems nginx

# View all logs
sudo journalctl -u jobportal -u ems -f

# Check disk space
df -h

# Check memory usage
free -h

# List all databases
mysql -u root -p -e "SHOW DATABASES;"

# List all Nginx configs
ls -la /etc/nginx/conf.d/

# Test all Nginx configs
sudo nginx -t

# View all SSL certificates
sudo certbot certificates
```

---

## 💰 Cost Impact

**Adding EMS to existing EC2:**
- Additional cost: **$0** (same server)
- Memory: Tight but manageable
- CPU: Minimal impact (low traffic)

**If need more resources:**
- Upgrade to t3.small (2GB RAM): +$7.50/month
- Total: ~$15/month after Free Tier

---

## 🎯 Best Practices

1. **Always use different ports** for each Spring Boot app
2. **Test locally first** before deploying
3. **Backup database** before major changes
4. **Monitor memory usage** with `free -h`
5. **Use systemd** for auto-restart
6. **Keep logs** for troubleshooting
7. **Document everything** (like this guide!)

---

## 📚 Files to Create for New App

1. `application-prod.properties` - Spring Boot config
2. `ems.service` - Systemd service
3. `nginx-ems.conf` - Nginx configuration
4. `deploy-ems.sh` - Deployment script
5. `.env.production` - React environment variables

---

## ✅ Deployment Checklist Summary

**Before deployment:**
- [ ] DNS record created and verified
- [ ] Database created and tested
- [ ] Application builds successfully
- [ ] Port number assigned (not conflicting)

**During deployment:**
- [ ] Files uploaded to EC2
- [ ] Systemd service created and started
- [ ] Nginx configured and reloaded
- [ ] SSL certificate obtained

**After deployment:**
- [ ] HTTP/HTTPS access verified
- [ ] API endpoints tested
- [ ] Logs checked for errors
- [ ] Performance monitored

---

## 🔗 Related Documentation

- **DEPLOYMENT_GUIDE.md** - Full EC2 setup guide
- **NGINX_EXPLAINED.md** - Nginx concepts explained
- **QUICK_REFERENCE.md** - Command cheat sheet
- **ARCHITECTURE.md** - System architecture diagrams

---

## 📞 When You Switch Projects

**Copy this file to your EMS project and provide it to Amazon Q with:**

"I have an existing EC2 server running JobPortal. Here's the multi-app deployment guide. Please help me deploy my EMS application (React + Spring Boot) following this pattern."

**Amazon Q will then:**
1. Create all necessary configuration files
2. Generate deployment scripts
3. Provide step-by-step instructions
4. Help troubleshoot any issues

---

**Current Infrastructure:**
- **EC2 IP:** 35.178.231.85
- **Domain:** gitsoft.uk
- **Existing Apps:** JobPortal (port 8080)
- **Database:** MariaDB 10.5
- **Available Ports:** 8081, 8082, 8083...

**Ready to deploy your next app! 🚀**
