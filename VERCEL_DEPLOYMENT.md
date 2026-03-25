# 🚀 Deploy Frontend to Vercel

Complete guide for deploying your React frontend to Vercel.

## Prerequisites

- GitHub account (you already have this ✅)
- Vercel account (free tier available)
- Your repository: https://github.com/Srikar-segmentation-fault/stockanalysis

---

## Step 1: Create Vercel Account

1. Go to https://vercel.com
2. Click "Sign Up"
3. Choose "Continue with GitHub"
4. Authorize Vercel to access your GitHub account

---

## Step 2: Import Your Project

### Option A: Using Vercel Dashboard (Recommended)

1. **Go to Vercel Dashboard**
   - Visit https://vercel.com/dashboard
   - Click "Add New..." → "Project"

2. **Import Git Repository**
   - Find "stockanalysis" in the list
   - Click "Import"

3. **Configure Project**
   
   **Framework Preset:** Vite
   
   **Root Directory:** `frontend`
   
   **Build Settings:**
   - Build Command: `npm run build`
   - Output Directory: `dist`
   - Install Command: `npm install`
   
   **Environment Variables:**
   - Click "Add Environment Variable"
   - Name: `VITE_API_BASE_URL`
   - Value: `https://your-backend-url.com/api` (update later)

4. **Deploy**
   - Click "Deploy"
   - Wait 2-3 minutes for build to complete

### Option B: Using Vercel CLI

```bash
# Install Vercel CLI
npm install -g vercel

# Login to Vercel
vercel login

# Deploy from project root
cd C:\projects\stockanalysis
vercel

# Follow prompts:
# - Set up and deploy? Yes
# - Which scope? Your account
# - Link to existing project? No
# - Project name? stockanalysis-frontend
# - Directory? ./frontend
# - Override settings? No

# Deploy to production
vercel --prod
```

---

## Step 3: Configure Environment Variables

After initial deployment, you need to configure the API URL:

1. **Go to Project Settings**
   - Dashboard → Your Project → Settings → Environment Variables

2. **Add Environment Variable**
   - Name: `VITE_API_BASE_URL`
   - Value: Your backend API URL (e.g., `https://your-backend.com/api`)
   - Environment: Production

3. **Redeploy**
   - Go to Deployments tab
   - Click "..." on latest deployment
   - Click "Redeploy"

---

## Step 4: Update Backend CORS

Your backend needs to allow requests from your Vercel domain.

**Your Vercel URL will be:** `https://stockanalysis-frontend.vercel.app` (or similar)

### Update Backend CORS Configuration

When you deploy your backend, update the `.env` file:

```bash
CORS_ALLOWED_ORIGINS=https://stockanalysis-frontend.vercel.app,https://your-custom-domain.com
```

---

## Step 5: Custom Domain (Optional)

### Add Custom Domain

1. **Go to Project Settings**
   - Dashboard → Your Project → Settings → Domains

2. **Add Domain**
   - Enter your domain (e.g., `stockanalysis.yourdomain.com`)
   - Follow DNS configuration instructions

3. **Configure DNS**
   - Add CNAME record pointing to `cname.vercel-dns.com`
   - Or add A record pointing to Vercel's IP

4. **Wait for SSL**
   - Vercel automatically provisions SSL certificate
   - Usually takes 1-2 minutes

---

## Step 6: Verify Deployment

### Check Your Deployment

1. **Visit Your Vercel URL**
   - You'll get a URL like: `https://stockanalysis-frontend.vercel.app`
   - Or your custom domain

2. **Test the Application**
   - Page should load without errors
   - Check browser console for errors
   - Note: API calls will fail until backend is deployed

3. **Check Build Logs**
   - Dashboard → Deployments → Click on deployment
   - View build logs for any errors

---

## Step 7: Connect to Backend

### When Backend is Ready

1. **Get Backend URL**
   - Deploy backend to your preferred platform
   - Get the API URL (e.g., `https://api.yourdomain.com`)

2. **Update Environment Variable**
   - Vercel Dashboard → Settings → Environment Variables
   - Update `VITE_API_BASE_URL` with your backend URL

3. **Redeploy**
   - Deployments → Redeploy latest

4. **Update Backend CORS**
   - Add your Vercel URL to backend CORS configuration

---

## Automatic Deployments

Vercel automatically deploys when you push to GitHub:

- **Push to `main` branch** → Production deployment
- **Push to other branches** → Preview deployment
- **Pull requests** → Preview deployment with unique URL

### Trigger Deployment

```bash
# Make a change
git add .
git commit -m "Update frontend"
git push origin main

# Vercel automatically deploys in 2-3 minutes
```

---

## Configuration Files

### vercel.json (Already Created)

```json
{
  "version": 2,
  "buildCommand": "cd frontend && npm install && npm run build",
  "outputDirectory": "frontend/dist",
  "framework": "vite",
  "rewrites": [
    {
      "source": "/(.*)",
      "destination": "/index.html"
    }
  ]
}
```

### frontend/.env.production

```bash
VITE_API_BASE_URL=https://your-backend-url.com/api
```

---

## Troubleshooting

### Build Fails

**Check build logs:**
1. Dashboard → Deployments → Click deployment
2. View "Building" logs
3. Look for error messages

**Common issues:**
- Missing dependencies: Check `package.json`
- Build errors: Test locally with `npm run build`
- Environment variables: Verify they're set correctly

### API Calls Fail

**Check:**
1. Backend is deployed and running
2. `VITE_API_BASE_URL` is set correctly
3. Backend CORS allows your Vercel domain
4. Backend health endpoint is accessible

**Test backend:**
```bash
curl https://your-backend-url.com/api/health
```

### Routing Issues (404 on refresh)

**Solution:** Already configured in `vercel.json`
- All routes redirect to `index.html`
- React Router handles client-side routing

### Environment Variables Not Working

**Check:**
1. Variable name starts with `VITE_`
2. Variable is set in Vercel dashboard
3. Redeployed after adding variable
4. Using `import.meta.env.VITE_API_BASE_URL` in code

---

## Vercel CLI Commands

```bash
# Install CLI
npm install -g vercel

# Login
vercel login

# Deploy to preview
vercel

# Deploy to production
vercel --prod

# View logs
vercel logs

# List deployments
vercel ls

# Remove deployment
vercel rm <deployment-url>

# Link local project to Vercel
vercel link

# Pull environment variables
vercel env pull
```

---

## Performance Optimization

Vercel automatically provides:
- ✅ Global CDN
- ✅ Automatic HTTPS
- ✅ Gzip compression
- ✅ Image optimization
- ✅ Edge caching
- ✅ DDoS protection

### Additional Optimizations

1. **Enable Analytics**
   - Dashboard → Analytics → Enable

2. **Configure Caching**
   - Already configured in `vercel.json`
   - Static assets cached for 1 year

3. **Monitor Performance**
   - Dashboard → Analytics → Web Vitals

---

## Deployment Checklist

- [ ] Vercel account created
- [ ] Project imported from GitHub
- [ ] Root directory set to `frontend`
- [ ] Build command configured
- [ ] Environment variables set
- [ ] Initial deployment successful
- [ ] Custom domain added (optional)
- [ ] Backend deployed
- [ ] Backend CORS configured
- [ ] API URL updated in Vercel
- [ ] Redeployed with correct API URL
- [ ] All features tested

---

## Quick Reference

**Repository:** https://github.com/Srikar-segmentation-fault/stockanalysis

**Vercel Dashboard:** https://vercel.com/dashboard

**Deploy Command:**
```bash
vercel --prod
```

**Environment Variable:**
```
VITE_API_BASE_URL=https://your-backend-url.com/api
```

**Redeploy:**
```bash
git push origin main
```

---

## Next Steps

1. ✅ Deploy frontend to Vercel (you're doing this now)
2. ⏳ Deploy backend (AWS, Railway, Render, etc.)
3. ⏳ Update `VITE_API_BASE_URL` in Vercel
4. ⏳ Update backend CORS with Vercel URL
5. ⏳ Test end-to-end functionality
6. ⏳ Add custom domain (optional)

---

## Support

**Vercel Documentation:** https://vercel.com/docs

**Vercel Support:** https://vercel.com/support

**Your Repository:** https://github.com/Srikar-segmentation-fault/stockanalysis

---

**Your frontend will be live in 2-3 minutes! 🚀**
