# 🚀 Free Deployment Guide - EMS Application

## 📋 Overview

This guide will help you deploy the EMS application for **FREE** using:
- **Backend:** Render.com (Free tier)
- **Frontend:** Vercel (Free tier)
- **Database:** Render PostgreSQL (Free tier)

---

## 🔧 Prerequisites

1. GitHub account
2. Render.com account (sign up at https://render.com)
3. Vercel account (sign up at https://vercel.com)

---

## 📦 Step 1: Prepare Your Repository

### 1.1 Commit all changes
```bash
cd C:\MyStudy\Portfolio\Projects\ems
git add .
git commit -m "feat: Add deployment configuration files"
git push origin master
```

---

## 🗄️ Step 2: Deploy Database on Render

1. Go to https://dashboard.render.com
2. Click **"New +"** → **"PostgreSQL"**
3. Configure:
   - **Name:** `ems-database`
   - **Database:** `ems`
   - **User:** `ems_user`
   - **Region:** Choose closest to you
   - **Plan:** **Free**
4. Click **"Create Database"**
5. **Copy the "External Database URL"** (you'll need this)

---

## 🖥️ Step 3: Deploy Backend on Render

1. Go to https://dashboard.render.com
2. Click **"New +"** → **"Web Service"**
3. Connect your GitHub repository: `ems-fullstack-app`
4. Configure:
   - **Name:** `ems-backend`
   - **Region:** Same as database
   - **Root Directory:** `backend`
   - **Environment:** `Java`
   - **Build Command:** `./mvnw clean package -DskipTests`
   - **Start Command:** `java -jar target/ems-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod`
   - **Plan:** **Free**

5. Add Environment Variables:
   - Click **"Advanced"** → **"Add Environment Variable"**
   - **Key:** `DATABASE_URL`
   - **Value:** Paste the External Database URL from Step 2
   - **Key:** `PORT`
   - **Value:** `8080`

6. Click **"Create Web Service"**
7. Wait 5-10 minutes for deployment
8. **Copy your backend URL:** `https://ems-backend.onrender.com`

---

## 🌐 Step 4: Update Frontend Configuration

1. Edit `frontend/.env.production`:
```env
VITE_API_BASE_URL=https://ems-backend.onrender.com
```
Replace with your actual Render backend URL.

2. Commit changes:
```bash
git add frontend/.env.production
git commit -m "chore: Update production API URL"
git push origin master
```

---

## 🎨 Step 5: Deploy Frontend on Vercel

### Option A: Using Vercel CLI (Recommended)

1. Install Vercel CLI:
```bash
npm install -g vercel
```

2. Deploy:
```bash
cd frontend
vercel login
vercel --prod
```

### Option B: Using Vercel Dashboard

1. Go to https://vercel.com/dashboard
2. Click **"Add New..."** → **"Project"**
3. Import your GitHub repository: `ems-fullstack-app`
4. Configure:
   - **Framework Preset:** Vite
   - **Root Directory:** `frontend`
   - **Build Command:** `npm run build`
   - **Output Directory:** `dist`
5. Click **"Deploy"**
6. Your app will be live at: `https://your-app.vercel.app`

---

## ✅ Step 6: Test Your Deployment

1. Visit your Vercel URL: `https://your-app.vercel.app`
2. Test all features:
   - View employees list
   - Add new employee
   - Edit employee
   - Delete employee
   - Department management

---

## 🔄 Step 7: Enable CORS (If Needed)

If you get CORS errors, update `backend/src/main/java/uk/gitsoft/ems/config/WebConfig.java`:

```java
@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**")
            .allowedOrigins("https://your-app.vercel.app")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*");
}
```

---

## 💡 Important Notes

### Free Tier Limitations:

**Render Free Tier:**
- Backend sleeps after 15 minutes of inactivity
- First request after sleep takes 30-60 seconds (cold start)
- 750 hours/month free

**Render PostgreSQL Free:**
- 90 days free trial
- After 90 days, migrate to Railway.app or Supabase (also free)

**Vercel Free Tier:**
- Unlimited deployments
- 100GB bandwidth/month
- Automatic HTTPS

### Tips:
- Keep your app active by pinging it every 10 minutes (use cron-job.org)
- Monitor your Render dashboard for sleep status
- Use Railway.app as alternative for database after 90 days

---

## 🔗 Live URLs

After deployment, update your README.md with:

```markdown
## 🌐 Live Demo

- **Frontend:** https://your-app.vercel.app
- **Backend API:** https://ems-backend.onrender.com
- **API Docs:** https://ems-backend.onrender.com/api/employees
```

---

## 🐛 Troubleshooting

### Backend not starting:
- Check Render logs: Dashboard → Your Service → Logs
- Verify DATABASE_URL is set correctly
- Ensure Java 21 is specified

### Frontend can't connect to backend:
- Check CORS configuration
- Verify .env.production has correct backend URL
- Check browser console for errors

### Database connection failed:
- Verify DATABASE_URL format
- Check database is running on Render
- Ensure PostgreSQL dependency is in pom.xml

---

## 📞 Support

For issues, check:
- Render Status: https://status.render.com
- Vercel Status: https://www.vercel-status.com
- GitHub Issues: https://github.com/duchnb/ems-fullstack-app/issues
