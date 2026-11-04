# TEACH2LEARN APK Download Website

**Official Production Website:** [https://teach2learn-app.netlify.app/](https://teach2learn-app.netlify.app/)  
**Direct APK Download:** [https://teach2learn-app.netlify.app/teach2learn.apk](https://teach2learn-app.netlify.app/teach2learn.apk)  
**Instant Auto-Download:** [https://teach2learn-app.netlify.app/?download=true](https://teach2learn-app.netlify.app/?download=true)

This folder contains the complete, self-contained promotional and download website for the **TEACH2LEARN** Android app.

---

## 📁 What's Included

- `index.html` - Complete responsive landing page (Tailwind CSS, Lucide icons, dynamic QR code, interactive feature tabs, step-by-step sideloading guide, release notes & FAQ).
- `teach2learn.apk` - Official compiled Android APK (v1.2.0 Build 2, ~23 MB, Android 8.0+).
- `app-icon.jpg` - Official high-resolution Teach2Learn application icon.

---

## 🌐 Official Deployed Site
The website is live on Netlify at:
👉 **`https://teach2learn-app.netlify.app/`**

---

## 🚀 How to Host & Deploy

### Option 1: GitHub Pages (Free & Automatic)
1. In your GitHub repository, go to **Settings** &rarr; **Pages**.
2. Under **Build and deployment**, choose **Deploy from a branch**.
3. Select your branch and set the folder to `/website` (or move the contents of `/website` to your root or a `gh-pages` branch).
4. Save. Your download page will be live at `https://<username>.github.io/<repo-name>/`.

### Option 2: Deploy to Netlify (Fastest & Free)

#### Method A: Netlify Drop (Zero Setup, Drag & Drop in 30 Seconds)
1. Export or download the **`website/`** folder from this project (or download as ZIP from the AI Studio top menu).
2. Go to **[https://app.netlify.com/drop](https://app.netlify.com/drop)** in your browser and log in.
3. Drag and drop the `website` folder directly into the dashed box.
4. Netlify will publish it immediately and give you a free live URL (e.g., `https://teach2learn-download.netlify.app`).

#### Method B: Netlify CLI
If you have `netlify-cli` installed:
```bash
cd website
netlify deploy --prod
```

#### Method C: Git Integration
If you push this project to GitHub/GitLab:
1. In Netlify, click **Add new site &rarr; Import an existing project**.
2. Select your repository.
3. Set **Base directory**: `website`
4. Set **Publish directory**: `website` (or leave empty if Base is set to `website`).
5. Click **Deploy Site**. Every time you push, Netlify deploys automatically.

---

### Option 3: GitHub Pages

### Option 3: Run Locally on Your Computer
From the project directory:
```bash
# Using Python (built-in on macOS, Linux, and Windows)
cd website
python3 -m http.server 8080
```
Then open `http://localhost:8080` in your web browser or phone browser on the same Wi-Fi.

---

## ⚡ Direct Download Link
When hosted, users can also trigger an instant download directly with:
```
https://your-domain.com/?download=true
```
