# TdxL2Analyzer - Android L2 逐笔交易分析器

## 项目简介
通达信 L2 数据逐笔交易分析 Android APP，支持：
- 📊 买卖力量对比分析
- 🔔 异常大单监控提醒  
- 🎯 成交节奏分析

## 快速构建 APK

### 方式一：GitHub Actions 在线构建（推荐）
1. 将此项目上传到 GitHub 仓库
2. 进入仓库 → Actions → 选择 "Build APK" → Run workflow
3. 构建完成后在 Artifacts 中下载 APK

### 方式二：本地 Android Studio 构建
1. 安装 Android Studio：https://developer.android.com/studio
2. 解压项目，修改 `local.properties` 中的 SDK 路径
3. 打开项目 → Build → Build Bundle(s) / APK(s) → Build APK
4. 生成的 APK：`app/build/outputs/apk/release/app-release.apk`

### 方式三：命令行构建
```bash
# 1. 安装 JDK 17
# 2. 安装 Android SDK (API 34, Build Tools 34.0.0)
# 3. 运行：
./gradlew.bat assembleRelease
```

## SDK 配置示例（local.properties）
```
sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

## 权限说明
- 网络权限：连接通达信 L2 数据源
- 前台服务：保持 L2 数据连接
- 振动权限：大单提醒

## L2 数据源配置
在 `app/src/main/java/com/tdx/l2analyzer/data/TdxSocketClient.kt` 中配置：
- `host`: 通达信 L2 服务器地址
- `port`: 服务器端口
- `accountToken`: 你的 L2 账号 Token

## 项目结构
```
app/
├── src/main/
│   ├── java/com/tdx/l2analyzer/
│   │   ├── data/         # L2 数据连接
│   │   ├── analyzer/     # 分析引擎
│   │   ├── service/      # 前台服务
│   │   ├── ui/          # 界面
│   │   └── util/        # 工具类
│   ├── res/             # 布局/资源
│   └── AndroidManifest.xml
├── build.gradle          # 模块构建配置
└── proguard-rules.pro   # 混淆规则
```

## 常见问题
**Q: 编译失败，提示 SDK 找不到？**  
A: 修改 `local.properties` 中的 `sdk.dir` 为你的 Android SDK 实际路径。

**Q: APK 安装后无法连接 L2 数据？**  
A: 需要在 `TdxSocketClient.kt` 中填入正确的服务器地址和 Token。

**Q: 没有 L2 账号可以测试吗？**  
A: 可以，APP 内置模拟数据模式，无需真实 L2 连接即可体验界面和功能。
