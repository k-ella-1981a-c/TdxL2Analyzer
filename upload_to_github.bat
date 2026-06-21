@echo off
chcp 65001 >nul
echo ================================================
echo   TdxL2Analyzer - GitHub 上传助手
echo ================================================
echo.

REM 检查 git 是否安装
git --version >nul 2>&1
if errorlevel 1 (
    echo [错误] 未找到 Git！请先安装：https://git-scm.com/download/win
    pause
    exit /b 1
)

echo [1/4] 检查 Git 状态...
git status >nul 2>&1
if errorlevel 1 (
    echo [错误] 当前目录不是 Git 仓库！
    pause
    exit /b 1
)
echo ✓ Git 仓库正常
echo.

echo [2/4] 请输入你的 GitHub 信息：
echo.
set /p GITHUB_USER=  GitHub 用户名: 
set /p REPO_NAME=  仓库名称 [默认: TdxL2Analyzer]: 
if "%REPO_NAME%"=="" set REPO_NAME=TdxL2Analyzer

echo.
echo 仓库地址: https://github.com/%GITHUB_USER%/%REPO_NAME%.git
echo.

REM 添加远程仓库
git remote -v | find "origin" >nul 2>&1
if errorlevel 1 (
    echo [3/4] 添加远程仓库...
    git remote add origin https://github.com/%GITHUB_USER%/%REPO_NAME%.git
) else (
    echo [3/4] 远程仓库已存在，更新 URL...
    git remote set-url origin https://github.com/%GITHUB_USER%/%REPO_NAME%.git
)
echo ✓ 远程仓库配置完成
echo.

echo [4/4] 准备推送代码...
echo.
echo ⚠️  注意：
echo    - 首次推送需要输入 GitHub 用户名和密码
echo    - 密码处请使用 Personal Access Token（不是登录密码）
echo    - Token 获取：https://github.com/settings/tokens
echo.
pause

REM 推送代码
git branch -M main
git push -u origin main

if errorlevel 1 (
    echo.
    echo [失败] 推送失败，请检查：
    echo   1. 仓库是否已创建？
    echo   2. 用户名是否正确？
    echo   3. Token 是否正确？（密码处粘贴 Token）
    echo.
    echo 也可以手动推送：
    echo   git push -u origin main
) else (
    echo.
    echo ✅ 代码推送成功！
    echo.
    echo 下一步：
    echo   1. 打开 https://github.com/%GITHUB_USER%/%REPO_NAME%/actions
    echo   2. 启用 GitHub Actions
    echo   3. 点击 "Run workflow" 开始构建 APK
    echo   4. 等待 5-10 分钟后下载 APK
)

echo.
pause
