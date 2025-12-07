# EMS Deployment Guide - AWS EC2
## Employee Management System - Complete Deployment Tutorial

**Project:** Employee Management System (React + Spring Boot)  
**Target:** AWS EC2 (35.178.231.85) - Shared with JobPortal  
**Domain:** ems.gitsoft.uk  
**Architecture:** React Frontend + Spring Boot REST API + MariaDB

---

## 📚 What You'll Learn

This hands-on guide teaches you:
- DNS configuration with AWS Route 53
- Database isolation and security
- Production builds for React (Vite)
- Spring Boot multi-profile configuration
- Systemd service management
- Nginx reverse proxy setup
- SSL certificate automation with Let's Encrypt
- Multi-application deployment on single server

---

## 🏗️ Architecture Overview

```
ems.gitsoft.uk (HTTPS)
         ↓
    Nginx (Port 443)
         ↓
    ┌────────────────┬─────────────────┐
    │                │                 │
React Frontend   Spring Boot API   MariaDB
/var/www/ems    localhost:8081    ems database
(Static Files)   (REST API)       (Data Layer)
```

**Key Concepts:**
- **Frontend:** Static files served by Nginx (fast, cached)
- **Backend:** Spring Boot on port 8081 (localhost only, secure)
- **Proxy:** Nginx routes `/api/*` requests to Spring Boot
- **Database:** Isolated `ems` database with dedicated user

---

## ✅ Prerequisites Checklist

- [x] AWS EC2 instance running (35.178.231.85)
- [x] SSH access to EC2 (`your-key.pem`)
- [x] MariaDB installed on EC2
- [x] Nginx installed on EC2
- [x] Domain `gitsoft.uk` in Route 53
- [x] Java 21 on EC2 (for Spring Boot)
- [x] Node.js & npm on local machine
- [x] Maven on local machine

---

## 🚀 Deployment Steps

### **Phase 1: DNS Configuration** ✅ (Already Done)

**What:** Map `ems.gitsoft.uk` to your EC2 IP address.

**Why:** DNS tells browsers where to find your application.

**Status:** You've already created the A record!

**Verify it worked:**
```bash
nslookup ems.gitsoft.uk
```
Expected output: `Address: 35.178.231.85`

**💡 Learning Note:** DNS uses A records (Address records) to map domain names to IP addresses. The TTL (Time To Live) of 300 seconds means DNS servers cache this for 5 minutes.

---

### **Phase 2: Database Setup**

**What:** Create isolated database and user for EMS.

**Why:** Security best practice - each app gets its own database and credentials.

#### **Step 2.1: Connect to EC2**
```bash
ssh -i ~/.ssh/your-key.pem ec2-user@35.178.231.85
```

#### **Step 2.2: Create Database**
```bash
mysql -u root -p
```

Run these SQL commands:
```sql
-- Create database with UTF-8 support
CREATE DATABASE ems CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Create dedicated user (localhost only = secure)cd
CREATE USER 'ems'@'localhost' IDENTIFIED BY 'emsbobby69#';

-- Grant privileges ONLY to ems database
GRANT ALL PRIVILEGES ON ems.* TO 'ems'@'localhost';
FLUSH PRIVILEGES;

-- Verify
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User='ems';
EXIT;
```

#### **Step 2.3: Test Connection**
```bash
mysql -u ems -p ems
```
Enter password. If you see `mysql>` prompt, success! Type `EXIT;`

**💡 Learning Note:** 
- `'ems'@'localhost'` restricts access to local connections only
- If EMS is compromised, JobPortal database remains safe (isolation)
- `utf8mb4` supports emojis and international characters

---

### **Phase 3: Prepare Frontend for Production**

**What:** Build optimized React production bundle.

**Why:** Development builds are 10x larger and slower than production builds.

#### **Step 3.1: Navigate to Frontend**
```bash
cd c:\MyStudy\Portfolio\Projects\ems\frontend
```

#### **Step 3.2: Install Dependencies**
```bash
npm install
```

#### **Step 3.3: Build Production Bundle**
```bash
npm run build
```

**What happens:**
- Vite creates `dist` folder
- JavaScript is minified (compressed)
- CSS is optimized
- Images are compressed
- Source maps removed
- Code is bundled for fast loading

**Output:** `dist` folder contains everything users will download.

**💡 Learning Note:** The `.env.production` file (already created) tells React to use `https://ems.gitsoft.uk` as the API base URL in production.

---

### **Phase 4: Prepare Backend for Production**

**What:** Build Spring Boot JAR with production configuration.

**Why:** Production needs different settings (port 8081, production DB, no debug logs).

#### **Step 4.1: Navigate to Backend**
```bash
cd c:\MyStudy\Portfolio\Projects\ems\backend
```

#### **Step 4.2: Update Production Password**

Edit `src/main/resources/application-prod.properties`:
- Change `YourSecurePassword123!` to your actual database password

#### **Step 4.3: Build JAR**
```bash
mvnw clean package -DskipTests
```

**What happens:**
- Maven compiles Java code
- Runs tests (skipped with `-DskipTests`)
- Packages everything into single JAR file
- JAR includes all dependencies (fat JAR)

**Output:** `target/ems-0.0.1-SNAPSHOT.jar`

**💡 Learning Note:** Spring Boot creates an executable JAR with embedded Tomcat server. No need to install Tomcat separately!

---

### **Phase 5: Upload Files to EC2**

**What:** Transfer built files from your Windows machine to EC2.

**Why:** EC2 needs the compiled code to run your application.

#### **Step 5.1: Upload Spring Boot JAR**
```bash
scp -i ~/.ssh/your-key.pem target/ems-0.0.1-SNAPSHOT.jar ec2-user@35.178.231.85:/tmp/ems.jar
```

#### **Step 5.2: Upload React Build**
```bash
scp -i ~/.ssh/your-key.pem -r dist/* ec2-user@35.178.231.85:/tmp/ems-frontend/
```

**💡 Learning Note:** 
- `scp` = Secure Copy Protocol (encrypted file transfer)
- We upload to `/tmp` first (temporary location)
- Later we'll move files to proper locations with correct permissions

---

### **Phase 6: Setup Application on EC2**

**What:** Organize files and set proper permissions.

**Why:** Linux security requires correct file ownership and locations.

#### **Step 6.1: SSH to EC2**
```bash
ssh -i ~/.ssh/your-key.pem ec2-user@35.178.231.85
```

#### **Step 6.2: Create Directories**
```bash
sudo mkdir -p /opt/ems          # Backend JAR location
sudo mkdir -p /var/www/ems      # Frontend static files
sudo mkdir -p /var/log/ems      # Application logs
```

#### **Step 6.3: Move Files**
```bash
sudo mv /tmp/ems.jar /opt/ems/app.jar
sudo mv /tmp/ems-frontend/* /var/www/ems/
```

#### **Step 6.4: Set Permissions**
```bash
sudo chown -R spring:spring /opt/ems
sudo chown -R spring:spring /var/log/ems
sudo chown -R nginx:nginx /var/www/ems
```

**💡 Learning Note:**
- `/opt` = Optional software (third-party apps)
- `/var/www` = Web server content
- `/var/log` = Log files
- `spring` user runs Java app (security - not root)
- `nginx` user serves static files

---

### **Phase 7: Create Systemd Service**

**What:** Configure EMS to run as a system service.

**Why:** Auto-start on boot, automatic restart on failure, easy management.

#### **Step 7.1: Create Service File Using tee Command**

**IMPORTANT:** Use the `tee` command to create the file correctly. Do NOT use `nano` with manual copy-paste as systemd requires the `ExecStart` line to be continuous.

**Replace `YOUR_DB_PASSWORD` with your actual database password:**

```bash
sudo tee /etc/systemd/system/ems.service > /dev/null <<'EOF'
[Unit]
Description=EMS Spring Boot Application
After=network.target mariadb.service
Requires=mariadb.service

[Service]
User=spring
Group=spring
WorkingDirectory=/opt/ems
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="DB_USERNAME=ems"
Environment="DB_PASSWORD=YOUR_DB_PASSWORD"
ExecStart=/usr/bin/java -Xmx300m -Dserver.port=8081 -Dspring.profiles.active=prod -Dspring.datasource.url=jdbc:mysql://localhost:3306/ems -Dspring.datasource.username=ems -Dspring.datasource.password=YOUR_DB_PASSWORD -jar /opt/ems/app.jar
Restart=on-failure
RestartSec=10s
StandardOutput=journal
StandardError=journal
SyslogIdentifier=ems

[Install]
WantedBy=multi-user.target
EOF
```

**💡 Learning Note:** 
- The `tee` command writes the file correctly in one operation
- `ExecStart` MUST be a single continuous line (no backslashes)
- Systemd doesn't handle line continuation like shell scripts
- `-Xmx300m` limits memory to 300MB (important for t3.micro with 1GB RAM)

#### **Step 7.2: Enable and Start Service**
```bash
sudo systemctl daemon-reload
sudo systemctl enable ems
sudo systemctl start ems
```

#### **Step 7.3: Check Status**
```bash
sudo systemctl status ems
```

Should show: `Active: active (running)`

#### **Step 7.4: View Logs**
```bash
sudo journalctl -u ems -f
```

You should see:
```
Started BackendApplication in X.XXX seconds
Tomcat started on port 8081 (http) with context path '/'
```

Press Ctrl+C to exit.

#### **Step 7.5: Verify Port**
```bash
sudo netstat -tlnp | grep 8081
```

Should show: `java` listening on `0.0.0.0:8081`

#### **Step 7.6: Test Backend API**
```bash
curl http://localhost:8081/api/employees
curl http://localhost:8081/api/departments
```

Should return: `[]` (empty JSON arrays)

**💡 Learning Note:**
- `systemd` = Linux service manager
- `After=mariadb.service` = Start after database
- `Restart=on-failure` = Auto-restart if crash
- `-Xmx300m` = Limit Java heap to 300MB (leaves room for other apps)
- `SPRING_PROFILES_ACTIVE=prod` = Use production config
- All database credentials passed as JVM arguments for security

---

### **Phase 8: Configure Nginx**

**What:** Setup Nginx to serve frontend and proxy API requests.

**Why:** Nginx handles HTTPS, static files, and routes API calls to Spring Boot.

#### **Step 8.1: Create Nginx Config**
```bash
sudo nano /etc/nginx/conf.d/ems.conf
```

#### **Step 8.2: Paste Configuration**
```nginx
server {
    listen 80;
    listen [::]:80;
    server_name ems.gitsoft.uk;

    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }

    location / {
        root /var/www/ems;
        try_files $uri $uri/ /index.html;
        
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
            expires 30d;
            add_header Cache-Control "public, immutable";
        }
    }

    location /api/ {
        proxy_pass http://localhost:8081;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    access_log /var/log/nginx/ems-access.log;
    error_log /var/log/nginx/ems-error.log;
}
```

**Save:** Ctrl+O, Enter, Ctrl+X

#### **Step 8.3: Test and Reload Nginx**
```bash
sudo nginx -t
sudo systemctl reload nginx
```

#### **Step 8.4: Test HTTP Access**
```bash
curl -I http://ems.gitsoft.uk
```

Should return: `HTTP/1.1 200 OK`

**💡 Learning Note:**
- `location /` = Serve React frontend for all routes
- `try_files $uri $uri/ /index.html` = React Router support (SPA)
- `location /api/` = Proxy API requests to Spring Boot
- `proxy_pass http://localhost:8081` = Forward to backend
- Static files cached for 30 days (performance optimizationon / {
        root /var/www/ems;
        try_files $uri $uri/ /index.html;
        
        # Cache static assets (performance)
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
        
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
    }

    # Logging
    access_log /var/log/nginx/ems-access.log;
    error_log /var/log/nginx/ems-error.log;
}
```

**Save:** Ctrl+O, Enter, Ctrl+X

#### **Step 8.3: Test Configuration**
```bash
sudo nginx -t
```

Should show: `syntax is ok` and `test is successful`

#### **Step 8.4: Reload Nginx**
```bash
sudo systemctl reload nginx
```

#### **Step 8.5: Test HTTP Access**
```bash
curl -I http://ems.gitsoft.uk
```

Should show: `HTTP/1.1 200 OK`

**💡 Learning Note:**
- `location /` = Serve React app for all routes
- `try_files $uri $uri/ /index.html` = React Router support (SPA)
- `location /api/` = Proxy to Spring Boot
- `proxy_pass http://localhost:8081` = Forward to backend
- Static files cached for 30 days (performance)

---

### **Phase 9: Get SSL Certificate**

**What:** Enable HTTPS with free Let's Encrypt certificate.

**Why:** Security, SEO, browser trust, required for modern web apps.

#### **Step 9.1: Run Certbot**
```bash
sudo certbot --nginx -d ems.gitsoft.uk
```

#### **Step 9.2: Follow Prompts**
1. **Email:** Enter your email (for renewal reminders)
2. **Terms:** Type `Y` to agree
3. **Share email:** `Y` or `N` (optional)

**Certbot automatically:**
- Validates domain ownership
- Gets SSL certificate
- Updates Nginx config with HTTPS
- Adds HTTP → HTTPS redirect
- Sets up auto-renewal

#### **Step 9.3: Verify Certificate**
```bash
sudo certbot certificates
```

Should show certificate for `ems.gitsoft.uk` with expiry date.

#### **Step 9.4: Test HTTPS**
```bash
curl -I https://ems.gitsoft.uk
```

Should show: `HTTP/2 200`

**💡 Learning Note:**
- Let's Encrypt = Free, automated SSL certificates
- Certificates expire in 90 days
- Certbot auto-renews via cron job
- HTTPS encrypts all traffic (security)

---

### **Phase 10: Final Verification**

**What:** Test all components are working correctly.

**Why:** Ensure complete functionality before declaring success.

#### **Step 10.1: Test Frontend**
```bash
curl https://ems.gitsoft.uk
```

Should return HTML with React app.

#### **Step 10.2: Test API Endpoints**
```bash
# Test departments endpoint
curl https://ems.gitsoft.uk/api/departments

# Test employees endpoint
curl https://ems.gitsoft.uk/api/employees
```

Should return JSON arrays (might be empty initially).

#### **Step 10.3: Check Application Logs**
```bash
sudo journalctl -u ems -n 50
```

Look for:
- `Started BackendApplication`
- No error messages
- Database connection successful

#### **Step 10.4: Check Nginx Logs**
```bash
sudo tail -f /var/log/nginx/ems-access.log
```

Press Ctrl+C to exit.

#### **Step 10.5: Open in Browser**

Navigate to: `https://ems.gitsoft.uk`

**Test:**
1. ✅ Page loads (React frontend working)
2. ✅ No console errors (F12 Developer Tools)
3. ✅ Can view employees list
4. ✅ Can view departments list
5. ✅ Can add new department
6. ✅ Can add new employee
7. ✅ Can edit records
8. ✅ Can delete records

**🎉 If all tests pass, deployment is successful!**

---

## 🔧 Useful Commands Reference

### **Service Management**
```bash
# Check status
sudo systemctl status ems

# Start service
sudo systemctl start ems

# Stop service
sudo systemctl stop ems

# Restart service
sudo systemctl restart ems

# View logs (live)
sudo journalctl -u ems -f

# View last 100 lines
sudo journalctl -u ems -n 100
```

### **Nginx Management**
```bash
# Test configuration
sudo nginx -t

# Reload (no downtime)
sudo systemctl reload nginx

# Restart
sudo systemctl restart nginx

# View access logs
sudo tail -f /var/log/nginx/ems-access.log

# View error logs
sudo tail -f /var/log/nginx/ems-error.log
```

### **Database Management**
```bash
# Connect to database
mysql -u ems -p ems

# View tables
SHOW TABLES;

# View employees
SELECT * FROM employee;

# View departments
SELECT * FROM department;
```

### **SSL Certificate Management**
```bash
# List certificates
sudo certbot certificates

# Renew manually (auto-renewal is configured)
sudo certbot renew

# Test renewal
sudo certbot renew --dry-run
```

---

## 🐛 Troubleshooting Guide

### **Problem: Application won't start**

**Check logs:**
```bash
sudo journalctl -u ems -n 100
```

**Common causes:**
- Database connection failed (check password)
- Port 8081 already in use
- Java not found
- JAR file corrupted

**Solution:**
```bash
# Check if port is in use
sudo netstat -tlnp | grep 8081

# Check Java version
java -version

# Verify JAR exists
ls -lh /opt/ems/app.jar
```

---

### **Problem: 502 Bad Gateway**

**Meaning:** Nginx can't connect to Spring Boot.

**Check:**
```bash
# Is Spring Boot running?
sudo systemctl status ems

# Is it listening on 8081?
sudo netstat -tlnp | grep 8081
```

**Solution:**
```bash
sudo systemctl restart ems
sudo journalctl -u ems -f
```

---

### **Problem: API calls return 404**

**Check Nginx config:**
```bash
sudo nginx -t
cat /etc/nginx/conf.d/ems.conf
```

**Verify proxy_pass:**
- Should be `http://localhost:8081` (not 8080)

**Solution:**
```bash
sudo nano /etc/nginx/conf.d/ems.conf
# Fix proxy_pass line
sudo systemctl reload nginx
```

---

### **Problem: Database connection failed**

**Check:**
```bash
# Can you connect manually?
mysql -u ems -p ems

# Check user exists
mysql -u root -p
SELECT User, Host FROM mysql.user WHERE User='ems';
```

**Solution:**
```bash
# Reset password
mysql -u root -p
ALTER USER 'ems'@'localhost' IDENTIFIED BY 'NewPassword123!';
FLUSH PRIVILEGES;

# Update service
sudo nano /etc/systemd/system/ems.service
# Change DB_PASSWORD
sudo systemctl daemon-reload
sudo systemctl restart ems
```

---

### **Problem: Frontend loads but API fails**

**Check browser console (F12):**
- Look for CORS errors
- Check API URL

**Verify:**
```bash
# Test API directly
curl https://ems.gitsoft.uk/api/departments

# Check Spring Boot logs
sudo journalctl -u ems -f
```

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Internet                              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   ems.gitsoft.uk      │
         │   (DNS - Route 53)    │
         └───────────┬───────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   EC2: 35.178.231.85  │
         │   ┌───────────────┐   │
         │   │  Nginx :443   │   │
         │   │  (SSL/HTTPS)  │   │
         │   └───┬───────┬───┘   │
         │       │       │       │
         │   ┌───▼───┐ ┌─▼─────────────┐
         │   │ React │ │ Spring Boot   │
         │   │ /var/ │ │ :8081         │
         │   │ www/  │ │ /opt/ems      │
         │   │ ems   │ └───┬───────────┘
         │   └───────┘     │
         │                 │
         │         ┌───────▼───────┐
         │         │   MariaDB     │
         │         │   :3306       │
         │         │   ems DB      │
         │         └───────────────┘
         └───────────────────────────┘
```

---

## 🎯 Port Assignment Strategy

| Application | Port | Access | Purpose |
|-------------|------|--------|---------|
| Nginx | 80 | Public | HTTP (redirects to HTTPS) |
| Nginx | 443 | Public | HTTPS (main entry point) |
| JobPortal | 8080 | localhost | Spring Boot (Thymeleaf) |
| **EMS Backend** | **8081** | **localhost** | **Spring Boot (REST API)** |
| MariaDB | 3306 | localhost | Database |

**Security:** Only ports 80 and 443 are exposed to internet. Backend and database are localhost-only.

---

## 💾 Resource Usage (EC2 t3.micro - 1GB RAM)

```
Component          Memory    CPU    Notes
─────────────────────────────────────────────────
System/OS          ~150MB    5%     Amazon Linux
MariaDB            ~200MB    10%    Shared database
JobPortal          ~400MB    15%    Existing app
EMS Backend        ~250MB    10%    New app
Nginx              ~10MB     2%     Lightweight
─────────────────────────────────────────────────
Total              ~1010MB   42%    Tight but works!
```

**Memory Management:**
- `-XX:MaxRAMPercentage=50.0` limits EMS to ~500MB
- If memory issues occur, reduce to 40.0
- Consider adding swap space or upgrading to t3.small (2GB)

---

## 🔐 Security Best Practices Implemented

✅ **Database Isolation:** Separate user and database per application  
✅ **Localhost Only:** Backend not exposed to internet  
✅ **HTTPS Everywhere:** SSL certificate with auto-renewal  
✅ **Non-root User:** Spring Boot runs as `spring` user  
✅ **NoNewPrivileges:** Systemd security hardening  
✅ **PrivateTmp:** Isolated temporary directory  
✅ **Firewall Ready:** Only ports 80/443 need to be open  

---

## 🚀 Next Steps & Enhancements

**Immediate:**
- [ ] Add monitoring (CloudWatch, Prometheus)
- [ ] Setup automated backups for database
- [ ] Configure log rotation

**Future:**
- [ ] Add Spring Security (authentication)
- [ ] Implement JWT tokens
- [ ] Add Redis for caching
- [ ] Setup CI/CD pipeline
- [ ] Add health check endpoints
- [ ] Implement rate limiting

---

## 📞 Support & Resources

**AWS Documentation:**
- [EC2 User Guide](https://docs.aws.amazon.com/ec2/)
- [Route 53 Guide](https://docs.aws.amazon.com/route53/)

**Spring Boot:**
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Production Ready Features](https://docs.spring.io/spring-boot/reference/actuator/index.html)

**Nginx:**
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)

**Let's Encrypt:**
- [Certbot Documentation](https://certbot.eff.org/)

---

## ✅ Deployment Checklist

- [x] DNS A record created
- [ ] Database created and tested
- [ ] Frontend built (`npm run build`)
- [ ] Backend built (`mvnw package`)
- [ ] Files uploaded to EC2
- [ ] Systemd service created and running
- [ ] Nginx configured and tested
- [ ] SSL certificate obtained
- [ ] Application accessible via HTTPS
- [ ] All features tested in browser

---

**🎉 Congratulations!** You've successfully deployed a full-stack application to AWS EC2!

**What you learned:**
- DNS configuration
- Database management
- Production builds
- Linux system administration
- Nginx reverse proxy
- SSL/TLS certificates
- Systemd service management
- Multi-application deployment

**Author:** Bogdan Duchnowski  
**Project:** Employee Management System  
**Deployment Date:** 2024


---

### **Phase 9: Get SSL Certificate**

**What:** Enable HTTPS with free Let's Encrypt certificate.

**Why:** Security, SEO, browser trust, required for modern web apps.

#### **Step 9.1: Run Certbot**
```bash
sudo certbot --nginx -d ems.gitsoft.uk
```

#### **Step 9.2: Follow Prompts**
1. **Email:** Enter your email (for renewal reminders)
2. **Terms:** Type `Y` to agree
3. **Share email:** `Y` or `N` (optional)

**Certbot automatically:**
- Validates domain ownership
- Gets SSL certificate
- Updates Nginx config with HTTPS
- Adds HTTP → HTTPS redirect
- Sets up auto-renewal

#### **Step 9.3: Verify Certificate**
```bash
sudo certbot certificates
```

Should show certificate for `ems.gitsoft.uk` with expiry date.

#### **Step 9.4: Test HTTPS**
```bash
curl -I https://ems.gitsoft.uk
```

Should show: `HTTP/2 200`

**💡 Learning Note:**
- Let's Encrypt = Free, automated SSL certificates
- Certificates expire in 90 days
- Certbot auto-renews via cron job
- HTTPS encrypts all traffic (security)

---

### **Phase 10: Final Verification**

**What:** Test all components are working correctly.

**Why:** Ensure complete functionality before declaring success.

#### **Step 10.1: Test Frontend**
```bash
curl https://ems.gitsoft.uk
```

Should return HTML with React app.

#### **Step 10.2: Test API Endpoints**
```bash
curl https://ems.gitsoft.uk/api/departments
curl https://ems.gitsoft.uk/api/employees
```

Should return JSON arrays (might be empty initially).

#### **Step 10.3: Check Application Logs**
```bash
sudo journalctl -u ems -n 50
```

Look for:
- `Started BackendApplication`
- No error messages
- Database connection successful

#### **Step 10.4: Check Nginx Logs**
```bash
sudo tail -f /var/log/nginx/ems-access.log
```

Press Ctrl+C to exit.

#### **Step 10.5: Open in Browser**

Navigate to: `https://ems.gitsoft.uk`

**Test:**
1. ✅ Page loads (React frontend working)
2. ✅ No console errors (F12 Developer Tools)
3. ✅ Can view employees list
4. ✅ Can view departments list
5. ✅ Can add new department
6. ✅ Can add new employee
7. ✅ Can edit records
8. ✅ Can delete records

**🎉 If all tests pass, deployment is successful!**

---

## 🔧 Useful Commands Reference

### **Service Management**
```bash
# Check status
sudo systemctl status ems

# Start service
sudo systemctl start ems

# Stop service
sudo systemctl stop ems

# Restart service
sudo systemctl restart ems

# View logs (live)
sudo journalctl -u ems -f

# View last 100 lines
sudo journalctl -u ems -n 100
```

### **Nginx Management**
```bash
# Test configuration
sudo nginx -t

# Reload (no downtime)
sudo systemctl reload nginx

# Restart
sudo systemctl restart nginx

# View access logs
sudo tail -f /var/log/nginx/ems-access.log

# View error logs
sudo tail -f /var/log/nginx/ems-error.log
```

### **Database Management**
```bash
# Connect to database
mysql -u ems -p ems

# View tables
SHOW TABLES;

# View employees
SELECT * FROM employee;

# View departments
SELECT * FROM department;
```

### **SSL Certificate Management**
```bash
# List certificates
sudo certbot certificates

# Renew manually (auto-renewal is configured)
sudo certbot renew

# Test renewal
sudo certbot renew --dry-run
```

---

## 🐛 Troubleshooting Guide

### **Problem: Application won't start**

**Check logs:**
```bash
sudo journalctl -u ems -n 100
```

**Common causes:**
- Database connection failed (check password)
- Port 8081 already in use
- Java not found
- JAR file corrupted

**Solution:**
```bash
# Check if port is in use
sudo netstat -tlnp | grep 8081

# Check Java version
java -version

# Verify JAR exists
ls -lh /opt/ems/app.jar

# Test manually
sudo systemctl stop ems
sudo -u spring java -Xmx300m -Dserver.port=8081 -jar /opt/ems/app.jar
```

---

### **Problem: Service file shows Java help instead of starting**

**Symptom:** Logs show Java command-line options instead of Spring Boot starting.

**Cause:** The `ExecStart` line in systemd service file is broken across multiple lines.

**Solution:** Recreate the service file using the `tee` command (see Phase 7, Step 7.1). The `ExecStart` line MUST be continuous without line breaks.

---

### **Problem: 502 Bad Gateway**

**Meaning:** Nginx can't connect to Spring Boot.

**Check:**
```bash
# Is Spring Boot running?
sudo systemctl status ems

# Is it listening on 8081?
sudo netstat -tlnp | grep 8081
```

**Solution:**
```bash
sudo systemctl restart ems
sudo journalctl -u ems -f
```

---

### **Problem: API calls return 404**

**Check Nginx config:**
```bash
sudo nginx -t
cat /etc/nginx/conf.d/ems.conf
```

**Verify proxy_pass:**
- Should be `http://localhost:8081` (not 8080)

**Solution:**
```bash
sudo nano /etc/nginx/conf.d/ems.conf
# Fix proxy_pass line
sudo systemctl reload nginx
```

---

### **Problem: Database connection failed**

**Check:**
```bash
# Can you connect manually?
mysql -u ems -p ems

# Check user exists
mysql -u root -p
SELECT User, Host FROM mysql.user WHERE User='ems';
```

**Solution:**
```bash
# Reset password
mysql -u root -p
ALTER USER 'ems'@'localhost' IDENTIFIED BY 'NewPassword123!';
FLUSH PRIVILEGES;

# Update service (recreate with new password)
sudo tee /etc/systemd/system/ems.service > /dev/null <<'EOF'
# ... (use new password in ExecStart line)
EOF

sudo systemctl daemon-reload
sudo systemctl restart ems
```

---

### **Problem: Out of Memory**

**Symptom:** Service keeps restarting, status shows exit code 143.

**Check memory:**
```bash
free -h
```

**Solution:**
```bash
# Reduce Java heap size in service file
# Change -Xmx300m to -Xmx250m or lower
sudo nano /etc/systemd/system/ems.service
sudo systemctl daemon-reload
sudo systemctl restart ems
```

---

### **Problem: Frontend loads but API fails**

**Check browser console (F12):**
- Look for CORS errors
- Check API URL

**Verify:**
```bash
# Test API directly
curl https://ems.gitsoft.uk/api/departments

# Check Spring Boot logs
sudo journalctl -u ems -f
```

---

## 📊 Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                    Internet                              │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   ems.gitsoft.uk      │
         │   (DNS - Route 53)    │
         └───────────┬───────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │   EC2: 35.178.231.85  │
         │   ┌───────────────┐   │
         │   │  Nginx :443   │   │
         │   │  (SSL/HTTPS)  │   │
         │   └───┬───────┬───┘   │
         │       │       │       │
         │   ┌───▼───┐ ┌─▼─────────────┐
         │   │ React │ │ Spring Boot   │
         │   │ /var/ │ │ :8081         │
         │   │ www/  │ │ /opt/ems      │
         │   │ ems   │ └───┬───────────┘
         │   └───────┘     │
         │                 │
         │         ┌───────▼───────┐
         │         │   MariaDB     │
         │         │   :3306       │
         │         │   ems DB      │
         │         └───────────────┘
         └───────────────────────────┘
```

---

## 🎯 Port Assignment Strategy

| Application | Port | Access | Purpose |
|-------------|------|--------|---------|
| Nginx | 80 | Public | HTTP (redirects to HTTPS) |
| Nginx | 443 | Public | HTTPS (main entry point) |
| JobPortal | 8080 | localhost | Spring Boot (Thymeleaf) |
| **EMS Backend** | **8081** | **localhost** | **Spring Boot (REST API)** |
| MariaDB | 3306 | localhost | Database |

**Security:** Only ports 80 and 443 are exposed to internet. Backend and database are localhost-only.

---

## 💾 Resource Usage (EC2 t3.micro - 1GB RAM)

```
Component          Memory    CPU    Notes
─────────────────────────────────────────────────
System/OS          ~150MB    5%     Amazon Linux
MariaDB            ~200MB    10%    Shared database
JobPortal          ~400MB    15%    Existing app
EMS Backend        ~250MB    10%    New app (300MB limit)
Nginx              ~10MB     2%     Lightweight
─────────────────────────────────────────────────
Total              ~1010MB   42%    Tight but works!
```

**Memory Management:**
- `-Xmx300m` limits EMS to 300MB max heap
- If memory issues occur, reduce to 250MB
- Consider adding swap space or upgrading to t3.small (2GB)

---

## 🔐 Security Best Practices Implemented

✅ **Database Isolation:** Separate user and database per application  
✅ **Localhost Only:** Backend not exposed to internet  
✅ **HTTPS Everywhere:** SSL certificate with auto-renewal  
✅ **Non-root User:** Spring Boot runs as `spring` user  
✅ **Minimal Permissions:** Each service has only required access  
✅ **Firewall Ready:** Only ports 80/443 need to be open  
✅ **Credentials in JVM args:** Not in properties files on disk

---

## 🚀 Next Steps & Enhancements

**Immediate:**
- [ ] Add monitoring (CloudWatch, Prometheus)
- [ ] Setup automated backups for database
- [ ] Configure log rotation
- [ ] Add health check endpoints

**Future:**
- [ ] Add Spring Security (authentication)
- [ ] Implement JWT tokens
- [ ] Add Redis for caching
- [ ] Setup CI/CD pipeline
- [ ] Implement rate limiting
- [ ] Add API documentation (Swagger)

---

## 📞 Support & Resources

**AWS Documentation:**
- [EC2 User Guide](https://docs.aws.amazon.com/ec2/)
- [Route 53 Guide](https://docs.aws.amazon.com/route53/)

**Spring Boot:**
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [Production Ready Features](https://docs.spring.io/spring-boot/reference/actuator/index.html)

**Nginx:**
- [Nginx Documentation](https://nginx.org/en/docs/)
- [Reverse Proxy Guide](https://docs.nginx.com/nginx/admin-guide/web-server/reverse-proxy/)

**Let's Encrypt:**
- [Certbot Documentation](https://certbot.eff.org/)

---

## ✅ Deployment Checklist

- [x] DNS A record created
- [x] Database created and tested
- [x] Frontend built (`npm run build`)
- [x] Backend built (`mvnw package`)
- [x] Files uploaded to EC2
- [x] Systemd service created and running
- [x] Nginx configured and tested
- [x] SSL certificate obtained
- [x] Application accessible via HTTPS
- [x] All features tested in browser

---

## 🎓 What You Learned

**Technical Skills:**
- DNS configuration with AWS Route 53
- Database management and security isolation
- Production builds for React applications
- Spring Boot production configuration
- Linux system administration
- Systemd service management
- Nginx reverse proxy configuration
- SSL/TLS certificate management
- Multi-application deployment strategies
- Troubleshooting production issues

**Best Practices:**
- Security through isolation
- Resource management on limited hardware
- Proper file permissions and ownership
- Service monitoring and logging
- Configuration management

---

**🎉 Congratulations!** You've successfully deployed a full-stack application to AWS EC2!

**Author:** Bogdan Duchnowski  
**Project:** Employee Management System  
**Deployment Date:** December 2024  
**Live URL:** https://ems.gitsoft.uk
