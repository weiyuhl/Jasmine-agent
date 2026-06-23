# 架构对齐说明

本文档记录 `Android端Agent应用架构深度研究报告.md` 中的目标结构与当前工作区的实际对齐结果。

## 目录结构

| 目标区域 | 当前路径 | 状态 |
|---|---|---|
| 应用装配层 | `app/` | 保留现有 app 模块 |
| Core UI | `core/ui/` | 从 `core-ui` 移入 |
| Core Domain | `core/domain/` | 从 `core-domain` 移入 |
| Core Database | `core/database/` | 从 `core-database` 移入 |
| Core Testing | `core/testing/` | 从 `core-testing` 移入 |
| Data Repository 层 | `data/agent/` | 从 `core-data` 移入，因为它包含 `DefaultAgentRepository` |
| Feature API | `feature/agent/api/` | 从 `feature-agent-navigation` 移入 |
| Feature 实现层 | `feature/agent/impl/` | 从 `feature-agent` 移入 |
| Native Bridge | `native/bridge/` | 从 `core-rust` 移入，负责 Android Gradle + UniFFI 集成 |
| Rust Workspace | `rust/` | 从 Android 模块内部移到根目录 |
| 测试应用 | `test-app/` | 保留为 E2E / instrumented test app，并已作为 `:test-app` 纳入 Gradle |
| Jasmine 主题与组件库 | `jasmine-theme/` | 作为 included build 接入，提供 `:theme` 与 `:components`，承接设计系统职责 |
| Platform 适配层 | `platform/` | 已补占位结构 |
| Sandbox 执行层 | `sandbox/` | 已补占位结构 |
| CI | `ci/` | 已补占位结构 |
| Docs | `docs/` | 已补占位结构 |
| Benchmark | `benchmark/` | 已补占位结构 |
| Baseline Profile | `baselineprofile/` | 已补占位结构 |

## Gradle 模块

当前 `settings.gradle.kts` 中的实际模块如下，均与当前目录结构一致：

| Gradle 模块 | 当前路径 |
|---|---|
| `:app` | `app/` |
| `:core:database` | `core/database/` |
| `:core:domain` | `core/domain/` |
| `:core:testing` | `core/testing/` |
| `:core:ui` | `core/ui/` |
| `:data:agent` | `data/agent/` |
| `:feature:agent:api` | `feature/agent/api/` |
| `:feature:agent:impl` | `feature/agent/impl/` |
| `:native:bridge` | `native/bridge/` |
| `:test-app` | `test-app/` |

`jasmine-theme/` 通过 `includeBuild("jasmine-theme")` 接入，不属于主工程 `include(...)` 模块列表；它内部提供 `:theme` 与 `:components`，并通过 dependency substitution 替换 `com.lhzkml.jasmine:jasmine-theme` 与 `com.lhzkml.jasmine:jasmine-components`。

## 已清理旧目录

以下旧扁平目录已经完成迁移，当前文件系统和 `settings.gradle.kts` 均不再引用：

| 旧目录 | 新目录 |
|---|---|
| `core-ui/` | `core/ui/` |
| `core-domain/` | `core/domain/` |
| `core-database/` | `core/database/` |
| `core-testing/` | `core/testing/` |
| `core-data/` | `data/agent/` |
| `feature-agent-navigation/` | `feature/agent/api/` |
| `feature-agent/` | `feature/agent/impl/` |
| `core-rust/` | `native/bridge/` + `rust/` |

## 规划但未落地模块

`Android端Agent应用架构深度研究报告.md` 中还包含若干面向完整 Agent 应用的推荐模块，例如 `core:common`、`core:model`、`core:network`、`core:datastore`、`core:designsystem`、`core:navigation`、更多 `data:*` / `feature:*` 切片，以及 `native:generated-bindings`。这些属于后续扩展目标，当前工作区没有实际源码模块，因此未写入 `settings.gradle.kts`。

当前设计系统职责由 `jasmine-theme/` included build 承接，而不是单独落为主工程内的 `:core:designsystem` 模块。

## 非模块目录

以下目录存在于工作区，但不属于当前架构模块边界：

| 路径 | 说明 |
|---|---|
| `release-apk/` | 发布 APK 归档目录 |
| `build/`、`.gradle/`、`.kotlin/`、各模块 `build/` | 构建缓存和生成产物 |
| `.agents/`、`.claude/`、`.trae/` | 本地工具配置目录，不纳入架构模块 |
| `.git/`、`.github/` | 版本控制和 GitHub 配置 |

## 版本基线

| 组件 | 当前版本 |
|---|---:|
| Gradle Wrapper | `9.5.1` |
| Android Gradle Plugin | `9.2.1` |
| Kotlin | `2.4.0` |
| KSP | `2.3.9` |
| Hilt | `2.59.2` |
| AndroidX Hilt | `1.3.0` |
| Compose BOM | `2026.06.00` |
| Compose UI explicit modules | `1.11.3` |
| Activity Compose | `1.13.0` |
| Lifecycle | `2.11.0` |
| Navigation 3 | `1.1.3` |
| Room | `2.8.4` |
| Coroutines | `1.11.0` |
| SQLCipher Android | `4.16.0` |
| Jetpack Security Crypto | `1.1.0` |
| UniFFI | `0.31.2` |
| JNA | `5.19.1` |

## 验证命令

本次结构和版本对齐已通过以下命令验证：

```powershell
.\gradlew.bat --gradle-user-home .gradle --console=plain help --no-daemon --stacktrace
.\gradlew.bat --gradle-user-home .gradle --console=plain "-Pkotlin.compiler.execution.strategy=in-process" :app:compileReleaseKotlin --no-daemon --stacktrace
.\gradlew.bat --gradle-user-home .gradle --console=plain "-Pkotlin.compiler.execution.strategy=in-process" :app:assembleRelease --no-daemon --stacktrace
```

当前受限执行环境不允许 Kotlin daemon 在用户目录写入 marker 文件，所以验证命令显式使用 `kotlin.compiler.execution.strategy=in-process`。
