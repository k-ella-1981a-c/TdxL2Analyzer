# TdxL2Analyzer - L2 逐笔交易分析系统

<div align="center">

# 📊 L2 逐笔交易分析系统

**通达信 L2 数据 · 逐笔交易分析 · Android APP**

[功能特性](#功能特性) · [快速开始](#快速开始) · [构建APK](#构建apk) · [项目结构](#项目结构)

</div>

---

## 功能特性

### 💪 买卖力量对比
- 实时统计主动买入/卖出量
- 买卖压力指数（0-100）
- 大单买卖占比分析

### 🚨 异常大单监控
- 10万+ 大单实时预警
- 50万+ 超大单标记
- 200万+ 极大单醒目提醒
- 最近20笔大单滚动显示

### 🎯 成交节奏分析
- 成交密度（笔/秒）
- 连续买卖节奏评分
- 平均成交间隔监测

---

## 快速开始

### 方式一：HTML 测试包（立即体验）

1. 用手机浏览器打开 `html_demo/index.html`
2. 输入6位股票代码（如 `000001`）
3. 点击"开始分析"查看模拟数据

> 💡 测试包使用模拟数据，界面与真实 APP 一致

### 方式二：构建 APK（真实使用）

#### 环境要求
- Android Studio Hedgehog+ 或 Android SDK
- JDK 17+
- 通达信 L2 账号（真机运行时需要）

#### 步骤

**1. 修改 SDK 路径**

编辑 `local.properties`：
```properties
sdk.dir=C\:\\Users\\你的用户名\\AppData\\Local\\Android\\Sdk
```

**2. 生成签名密钥**（首次）

```bash
keytool -genkeypair -v -keystore release.keystore ^
  -alias tdxL2 -keyalg RSA -keysize 2048 -validity 10000 ^
  -storepass tdxL2@2026 -keypass tdxL2@2026
```

**3. 构建 APK**

双击运行 `build_apk.bat`，或手动执行：

```bash
gradlew.bat assembleRelease
```

**4. 安装到手机**

将 `app/build/outputs/apk/release/app-release.apk` 复制到手机安装

---

## 构建 APK

### 使用构建脚本（推荐）

双击 `build_apk.bat`，脚本会自动：
1. 检测 Android SDK 路径
2. 生成签名密钥（如不存在）
3. 更新 `local.properties`
4. 执行 Gradle 构建
5. 输出 APK 路径

### 手动构建

```bash
# 清理
gradlew.bat clean

# 编译 Release
gradlew.bat assembleRelease

# APK 输出位置
# app/build/outputs/apk/release/app-release.apk
```

### 常见构建错误

| 错误 | 解决方案 |
|------|---------|
| SDK not found | 修改 `local.properties` 中的 `sdk.dir` |
| Build tools not found | 用 Android Studio SDK Manager 安装 Build Tools 34.0.0 |
| Gradle sync failed | 检查网络，或配置 Gradle 镜像源 |
| keystore not found | 运行 `build_apk.bat` 自动生成，或手动执行 keytool 命令 |

---

## 项目结构

```
TdxL2Analyzer_Final/
├── app/
│   └── src/main/
│       ├── java/com/tdx/l2analyzer/
│       │   ├── TdxL2Application.kt      # Application
│       │   ├── ui/
│       │   │   ├── MainActivity.kt        # 主界面
│       │   │   └── GuideActivity.kt       # 引导页
│       │   ├── service/
│       │   │   └── L2DataService.kt     # 前台数据服务
│       │   ├── data/
│       │   │   └── TdxSocketClient.kt   # L2 Socket 客户端
│       │   ├── analyzer/
│       │   │   ├── BuySellAnalyzer.kt   # 买卖力量分析
│       │   │   ├── LargeOrderMonitor.kt  # 大单监控
│       │   │   └── RhythmAnalyzer.kt     # 节奏分析
│       │   └── util/
│       │       ├── CrashGuard.kt          # 崩溃捕获
│       │       ├── ConnectionRetryHelper.kt # 重连辅助
│       │       └── InputValidator.kt      # 输入校验
│       ├── res/
│       │   ├── layout/                   # 布局文件
│       │   ├── values/                   # 字符串/颜色/样式
│       │   └── drawable/                # 图标和背景
│       └── AndroidManifest.xml
├── build.gradle                       # 根级构建文件
├── settings.gradle
├── gradle.properties
├── gradlew.bat                        # Windows Gradle 启动器
├── build_apk.bat                     # APK 构建脚本
└── local.properties                   # SDK 路径配置
```

---

## 配置说明

### L2 数据源配置

在 `L2DataService.kt` 中修改连接参数：

```kotlin
val host = intent?.getStringExtra("host") ?: "hq.tdx.com.cn"
val port = intent?.getIntExtra("port", 7709) ?: 7709
val token = intent?.getStringExtra("token") ?: ""
```

### 大单阈值配置

在 `LargeOrderMonitor.kt` 中修改：

```kotlin
private const val LARGE_THRESHOLD = 100_000f    // 10万 = 大单
private const val SUPER_THRESHOLD = 500_000f    // 50万 = 超大单
private const val EXTREME_THRESHOLD = 2_000_000f // 200万 = 极大单
```

---

## 安全特性

- ✅ AES-256 加密存储敏感配置
- ✅ SSL 证书锁定（可在 `network_security_config.xml` 中配置）
- ✅ R8 全量混淆（Release 构建）
- ✅ 输入校验防止注入
- ✅ 全局异常捕获（崩溃日志保存至 `Documents/crash/`）

---

## 稳定性保障

- ✅ 指数退避重连（最多5次，2s/4s/8s/16s/32s）
- ✅ 多服务器容灾切换
- ✅ 前台服务保活（Android 8.0+ 兼容）
- ✅ 协程 SupervisorJob 隔离，单分析器崩溃不影响整体
- ✅ 主线程 UI 更新安全

---

## 免责声明

⚠️ **本软件仅供学习研究使用，不构成任何投资建议。**

- 股市有风险，投资需谨慎
- L2 数据需自行确保有合法使用授权
- 开发者不对使用本软件产生的任何投资损失负责

---

## 版本记录

| 版本 | 日期 | 说明 |
|------|------|------|
| v1.0.0 | 2024-06 | 初始版本，买卖力量/大单监控/节奏分析 |

---

<div align="center">

**开发：TdxL2Analyzer 团队**

</div>
