# 🚀 TdxL2Analyzer - GitHub 上传 + Actions 自动构建指南

## 完整操作步骤

### 📋 第一步：创建 GitHub 账号（如已有则跳过）

1. 打开 👉 [https://github.com/signup](https://github.com/signup)
2. 填写：邮箱、密码、用户名
3. 验证邮箱 → 完成注册

---

### 📁 第二步：在 GitHub 创建新仓库

1. 登录 GitHub → 点击右上角 **+** → **New repository**
2. 填写仓库信息：
   ```
   Repository name: TdxL2Analyzer
   Description:    通达信 L2 逐笔交易分析 Android APP
   Public ✅ (必须选 Public，否则 Actions 需要付费)
   ✅ Add a README file  (不要勾选，我们已经有了)
   ✅ Add .gitignore    (不要勾选)
   ```
3. 点击 **Create repository**
4. **复制仓库 URL**（类似 `https://github.com/你的用户名/TdxL2Analyzer.git`）

---

### 💻 第三步：推送代码到 GitHub

在你本地的 **Windows 电脑**上（不是这个沙箱环境），操作如下：

#### 3.1 下载项目文件
将沙箱中的 `TdxL2Analyzer_Final/` 目录下载到本地，比如放到：
```
C:\Projects\TdxL2Analyzer\
```

#### 3.2 打开命令行（PowerShell 或 CMD）
```bash
# 进入项目目录
cd C:\Projects\TdxL2Analyzer

# 添加远程仓库（将 YOUR_USERNAME 替换为你的 GitHub 用户名）
git remote add origin https://github.com/YOUR_USERNAME/TdxL2Analyzer.git

# 推送代码到 GitHub
git branch -M main
git push -u origin main
```

#### 3.3 输入 GitHub 账号密码
- **Username**: 你的 GitHub 用户名
- **Password**: 需要使用 **Personal Access Token**（不是登录密码）
  - 获取 Token：GitHub → Settings → Developer settings → Personal access tokens → Generate new token
  - 勾选 `repo` 权限 → 生成 → 复制 Token
  - 密码处粘贴 Token

---

### ⚙️ 第四步：启用 GitHub Actions 自动构建

代码推送成功后：

1. 打开你的 GitHub 仓库页面
2. 点击顶部 **Actions** 标签页
3. 你会看到 **"Build Android APK"** 工作流
4. 点击 **"I understand my workflows, go ahead and enable them"**
5. 点击 **"Build Android APK"** → 右侧 **"Run workflow"** → 选择 `main` 分支 → **Run workflow**

---

### 📦 第五步：下载构建好的 APK

构建大约需要 **5-10 分钟**：

1. 在 **Actions** 标签页查看构建进度
2. 构建完成后（绿色 ✓），点击对应的 workflow run
3. 在 **Artifacts** 区域找到 `TdxL2Analyzer-Release-APK`
4. 点击下载 → 解压得到 `app-release.apk`
5. 复制到手机 → 安装（需开启"允许未知来源"）

---

## 🔧 常见问题

### Q: 推送代码时提示 "Authentication failed"？
**A**: 必须使用 Personal Access Token，不能用登录密码。
获取 Token：https://github.com/settings/tokens

### Q: Actions 页面找不到工作流？
**A**: 确保 `.github/workflows/build-apk.yml` 文件已推送到仓库。

### Q: 构建失败，提示 "SDK not found"？
**A**: 工作流会自动安装 SDK，无需手动配置。如仍失败，检查 `build-apk.yml` 是否正确。

### Q: Public 仓库会不会泄露代码？
**A**: 代码完全开源，但 `local.properties` 和 `release.keystore` 已被 `.gitignore` 排除，不会上传。

---

## 📱 快速验证（无需等待构建）

在等待 APK 构建的同时，你可以：
1. 将 `html_demo/index.html` 发送到手机
2. 用浏览器打开 → 添加到主屏幕
3. 立即体验 APP 界面和模拟数据

---

## 📞 需要帮助？

如遇任何问题，截图发给我，我来帮你解决！
