# 架构对齐说明

本文档记录 `Android端Agent应用架构深度研究报告.md` 中的目标结构与当前工作区的实际对齐结果。

## 目录结构

| 目标区域 | 当前路径 | 状态 |
|---|---|---|
| 应用装配层 | `app/` | Gradle 模块 `:app`，只负责 Application、Activity、导航宿主、启动与发布装配 |
| Core Common | `core/common/` | Gradle 模块 `:core:common`，承接通用错误/结果等基础类型 |
| Core Model | `core/model/` | Gradle 模块 `:core:model`，承接跨层只读模型与领域枚举 |
| Core Navigation | `core/navigation/` | Gradle 模块 `:core:navigation`，承接 Navigation 3 route/key 与 feature entry 契约 |
| Core Design System | `core/designsystem/` | Gradle 模块 `:core:designsystem`，承接 Compose Material 3 theme |
| Core UI | `core/ui/` | Gradle 模块 `:core:ui`，保留兼容外观并转发到 `:core:designsystem` |
| Core Domain | `core/domain/` | Gradle 模块 `:core:domain`，承接 Repository 契约、UseCase、纯 Kotlin 校验策略 |
| Core Database | `core/database/` | Gradle 模块 `:core:database`，承接 Room、SQLCipher、DAO 与数据库安全测试 |
| Core DataStore | `core/datastore/` | Gradle 模块 `:core:datastore`，承接 DataStore 基础设施 |
| Core Network | `core/network/` | Gradle 模块 `:core:network`，承接网络基础设施 |
| Core Testing | `core/testing/` | Gradle 模块 `:core:testing`，承接 Hilt 测试 runner 等共享测试基础设施 |
| Data Repository 层 | `data/agent/` | Gradle 模块 `:data:agent`，实现 `AgentRepository` 并集中接入 database/native |
| Feature Home API | `feature/home/api/` | Gradle 模块 `:feature:home:api`，承接 Home feature 对外 API |
| Feature Home 实现层 | `feature/home/impl/` | Gradle 模块 `:feature:home:impl`，承接 Agent 首页 UI、ViewModel、Navigation entry 注册 |
| Native Bridge | `native/bridge/` | Gradle 模块 `:native:bridge`，负责 Android Gradle + Rust/UniFFI 集成 |
| Rust Workspace | `rust/` | Cargo workspace，当前 FFI 入口位于 `rust/crates/ffi_entry/` |
| Benchmark | `benchmark/` | Gradle 模块 `:benchmark`，承接 Macrobenchmark/Profile 质量验证 |
| 测试应用 | `test-app/` | Gradle 模块 `:test-app`，保留为 E2E / instrumented test app |
| Platform 适配层 | `platform/` | 报告推荐占位目录，后续按 OS 能力拆模块 |
| Sandbox 执行层 | `sandbox/` | 报告推荐占位目录，后续拆 `sandbox:api/service/protocol` |
| Baseline Profile | `baselineprofile/` | 报告推荐占位目录，当前已有 app 内 baseline profile 文件 |
| CI / Docs | `ci/`、`docs/` | 工程基础设施目录 |

## Gradle 模块

当前 `settings.gradle.kts` 中的实际模块如下：

| Gradle 模块 | 当前路径 |
|---|---|
| `:app` | `app/` |
| `:core:common` | `core/common/` |
| `:core:database` | `core/database/` |
| `:core:datastore` | `core/datastore/` |
| `:core:designsystem` | `core/designsystem/` |
| `:core:domain` | `core/domain/` |
| `:core:model` | `core/model/` |
| `:core:navigation` | `core/navigation/` |
| `:core:network` | `core/network/` |
| `:core:testing` | `core/testing/` |
| `:core:ui` | `core/ui/` |
| `:data:agent` | `data/agent/` |
| `:feature:home:api` | `feature/home/api/` |
| `:feature:home:impl` | `feature/home/impl/` |
| `:native:bridge` | `native/bridge/` |
| `:benchmark` | `benchmark/` |
| `:test-app` | `test-app/` |

## 分层边界

当前代码已按报告建议收紧主要依赖方向：

| 边界 | 当前处理 |
|---|---|
| `:app` 不直接依赖 `data:*` | `:app` 只依赖 `:core:designsystem`、`:core:navigation`、`:feature:home:api`、`:feature:home:impl` |
| `:app` 不直接调用 Rust FFI | Rust/UniFFI 只通过 `:native:bridge` 暴露给 `:data:agent` |
| route/key 不放 feature impl | `Main`、`BlankOne`、`BlankTwo` 已迁入 `:core:navigation` |
| feature entry 由 feature 贡献 | `:feature:home:impl` 通过 Hilt multibinding 贡献 `NavigationEntryRegistrar` |
| 共享模型不放 Repository 文件 | `AgentRecord`、`AgentRecordStatus` 已迁入 `:core:model` |
| Domain 不直接依赖 native | `AgentNamePolicy` 留在 domain；Rust 实现在 `:data:agent` 中绑定 |
| Design system 独立 | `AgentMaterialTheme` 已迁入 `:core:designsystem`，`:core:ui` 保留废弃兼容入口 |

## 规划但未完全落地模块

报告中的 `:core:security`、`:platform:*`、`:sandbox:api`、`:sandbox:service`、`:sandbox:protocol`、`:native:generated-bindings` 等仍属于后续业务扩展范围。当前工作区已保留对应目录骨架；只有已有职责和源码的部分被纳入 Gradle，避免空模块拉长构建图。

## 非模块目录

| 路径 | 说明 |
|---|---|
| `release-apk/` | 发布 APK 归档目录，由 release 构建后同步产物 |
| `build/`、`.gradle/`、`.kotlin/`、各模块 `build/` | 构建缓存和生成产物 |
| `.agents/`、`.claude/`、`.trae/` | 本地工具配置目录，不纳入架构模块 |
| `.git/`、`.github/` | 版本控制和 GitHub 配置 |

## 版本基线

| 组件 | 当前版本 |
|---|---:|
| JDK | `26.0.1` |
| Gradle Wrapper | `9.6.1` |
| Android Gradle Plugin | `9.2.1` |
| Kotlin | `2.4.0` |
| KSP | `2.3.9` |
| Hilt | `2.60` |
| AndroidX Hilt | `1.3.0` |
| Compose BOM | `2026.06.00` |
| Compose UI explicit modules | `1.11.3` |
| Activity Compose | `1.13.0` |
| Lifecycle | `2.11.0` |
| Navigation 3 | `1.1.3` |
| Room | `2.8.4` |
| DataStore | `1.2.1` |
| WorkManager | `2.11.2` |
| Benchmark / ProfileInstaller | `1.4.1` |
| Retrofit | `3.0.0` |
| Ktor | `3.5.1` |
| Coroutines | `1.11.0` |
| SQLCipher Android | `4.16.0` |
| Jetpack Security Crypto | `1.1.0` |
| UniFFI | `0.31.2` |
| JNA | `5.19.1` |
| Detekt | `1.23.8` |
| Dokka | `2.2.0` |
| Spotless | `8.8.0` |
| Compose Preview Screenshot Testing | `0.0.1-alpha15`（官方暂无稳定版） |

## 验证命令

本次分层结构对齐已通过以下命令验证：

```powershell
$env:JAVA_HOME='D:\jdk-26'
$env:Path='D:\jdk-26\bin;' + $env:Path
.\gradlew.bat :app:compileDebugKotlin --warning-mode all
```

当前工具进程仍可能持有旧 `JAVA_HOME` 环境快照；运行 Gradle 前显式设置为 `D:\jdk-26` 即可使用已安装的 JDK 26。
