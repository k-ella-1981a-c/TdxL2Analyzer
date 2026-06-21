@echo off
REM ============================================================
REM TdxL2Analyzer - APK 构建脚本
REM 使用前请确保已安装 Android Studio 或 Android SDK
REM ============================================================

setlocal enabledelayedexpansion

echo ==============================================
echo   TdxL2Analyzer - APK 构建工具
echo ==============================================
echo.

REM 查找 Android SDK
set SDK_DIR=
if exist "%LOCALAPPDATA%\Android\Sdk" set SDK_DIR=%LOCALAPPDATA%\Android\Sdk
if exist "%ANDROID_HOME%" set SDK_DIR=%ANDROID_HOME%
if exist "%ANDROID_SDK_ROOT%" set SDK_DIR=%ANDROID_SDK_ROOT%

if "%SDK_DIR%"=="" (
    echo [错误] 未找到 Android SDK
    echo 请修改 local.properties 中的 sdk.dir 路径
    echo 或设置环境变量 ANDROID_HOME
    pause
    exit /b 1
)

echo [OK] 找到 Android SDK: %SDK_DIR%
echo.

REM 生成签名密钥（如果不存在）
if not exist "release.keystore" (
    echo [生成] 签名密钥 release.keystore ...
    keytool -genkeypair -v -keystore release.keystore -alias tdxL2 -keyalg RSA -keysize 2048 -validity 10000 -storepass tdxL2@2026 -keypass tdxL2@2026 -dname "CN=L2Analyzer, OU=Dev, O=Tdx, L=Shenzhen, ST=GD, C=CN"
    if !errorlevel! == 0 (
        echo [警告] 密钥生成失败，将使用调试签名
    ) else (
        echo [OK] 签名密钥已生成
    )
) else (
    echo [OK] 签名密钥已存在
)
echo.

REM 设置 local.properties
echo sdk.dir=%SDK_DIR:\=\\% > local.properties
echo [OK] local.properties 已更新

echo.
echo [构建] 开始编译 Release APK ...
echo.

REM 使用 Gradle 构建
call gradlew.bat assembleRelease

if !errorlevel! == 0 (
    echo.
    echo ==============================================
    echo  构建成功！
    echo  APK 位置: app\build\outputs\apk\release\
    echo ==============================================
    dir app\build\outputs\apk\release\*.apk 2>nul
) else (
    echo.
    echo [失败] 构建失败，请检查错误信息
    echo 常见问题:
    echo   1. SDK 路径不正确 - 修改 local.properties
    echo   2. 缺少 build-tools - 用 SDK Manager 安装
    echo   3. 网络问题 - 检查 Gradle 镜像源
)

echo.
pause
