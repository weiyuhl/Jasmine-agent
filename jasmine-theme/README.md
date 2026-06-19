# Jasmine Theme 使用说明

`Jasmine` 是一个独立的 Compose App 自定义主题库，不是 Material3 源码 fork。

父级工作区里的 Material3 组件库只作为学习和结构参考。本主题库通过 Maven 依赖桥接官方 Material3，然后对 App 暴露自己的 `JasmineTheme`、设计 token 和少量组件包装。

默认风格：

- 简约、干净、偏应用工具型。
- 主背景和 Surface 以白色为主。
- 主要操作色是接近黑色的中性色。
- 辅助线、边框、禁用态使用灰色。
- 不使用紫色、紫蓝渐变、AI 风格渐变。
- 字体使用系统默认字体，也就是 Compose/Material3 默认的系统字体路线，没有内置第三方字体文件。

## 当前产物坐标

默认 Maven 坐标是：

```text
com.lhzkml.jasmine:jasmine-theme:0.1.0
```

正式给你的 App 使用前，建议先修改 `theme/build.gradle.kts` 里的：

```kotlin
group = "com.lhzkml.jasmine"
version = "0.1.0"
```

例如可以改成你自己的包名：

```kotlin
group = "com.yourcompany.jasmine"
version = "0.1.0"
```

同时也可以把 namespace 从：

```kotlin
namespace = "com.lhzkml.jasmine.theme"
```

改成你自己的：

```kotlin
namespace = "com.yourcompany.jasmine.theme"
```

如果改包名，还需要同步修改源码里的 `package com.lhzkml.jasmine.theme`。

## 本地发布

本项目 Gradle Wrapper 使用 `9.5.1`。本机已验证可用的环境是：

```powershell
$env:JAVA_HOME="D:\jdk-17.0.2"
$env:ANDROID_HOME="D:\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
$env:PATH="$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
```

在 `jasmine-theme` 目录执行：

```powershell
.\gradlew.bat :theme:publishToMavenLocal
```

发布成功后，它会进入你电脑本机的 Maven Local 仓库。你的 App 项目可以直接从本机引入。

如果只想检查能不能构建：

```powershell
.\gradlew.bat :theme:assembleRelease
```

## 作为 includeBuild 接入主项目

如果主项目用 `includeBuild("../jasmine-theme")` 这类方式接入 Jasmine，需要注意两个点：

- 同一个 Gradle 构建里 Android Gradle Plugin 版本要统一。当前 Jasmine 已对齐主项目，使用 `9.3.0-alpha12`。
- included build 不会自动读取主项目的 `local.properties`。构建前需要设置 Android SDK 环境变量：

```powershell
$env:ANDROID_HOME="D:\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
```

如果不想处理 included build 的插件版本统一问题，可以改用 `publishToMavenLocal`，然后主项目通过 `mavenLocal()` 和 Maven 坐标引入。

## App 里怎么引入

在 App 项目的仓库配置里加入 `mavenLocal()`：

```kotlin
repositories {
    mavenLocal()
    google()
    mavenCentral()
}
```

然后在 App 模块依赖里加入：

```kotlin
dependencies {
    implementation("com.lhzkml.jasmine:jasmine-theme:0.1.0")
}
```

如果你已经改了 `group` 或 `version`，这里也要跟着改。

## App 里怎么使用主题

在 `setContent` 最外层包一层 `JasmineTheme`：

```kotlin
import com.lhzkml.jasmine.theme.JasmineTheme

setContent {
    JasmineTheme {
        App()
    }
}
```

之后 App 里的页面、组件都在这个主题下面运行。

## 使用主题颜色、字体、间距

业务代码里优先使用这个库暴露的 `JasmineTheme`：

```kotlin
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.lhzkml.jasmine.theme.JasmineTheme

Text(
    text = "Hello",
    color = JasmineTheme.colorScheme.onSurface,
    style = JasmineTheme.typography.bodyLarge,
    modifier = Modifier.padding(JasmineTheme.spacing.md),
)
```

这样后面你要统一改颜色、字体、圆角、间距时，只改主题库即可。

## 使用 Jasmine 组件包装

库里已经提供了一个示例按钮包装：

```kotlin
import androidx.compose.material3.Text
import com.lhzkml.jasmine.theme.components.JasminePrimaryButton

JasminePrimaryButton(onClick = onContinue) {
    Text("Continue")
}
```

以后如果你想统一 Button、TextField、TopBar、Card、Dialog 的默认样式，可以继续在 `theme/src/main/java/com/lhzkml/jasmine/theme/components/` 下面添加包装组件。

## 还能不能直接用 Material3 组件

可以。

这个库内部已经用官方 `MaterialTheme` 承接了颜色、字体和形状，所以在 `JasmineTheme` 里面直接使用 Material3 组件也会吃到你的自定义主题：

```kotlin
import androidx.compose.material3.Button
import androidx.compose.material3.Text

Button(onClick = onSave) {
    Text("Save")
}
```

推荐规则：

- 普通业务页面：优先用 `JasmineTheme` 和你自己的 `JasminePrimaryButton` 这类包装组件。
- 临时页面或还没封装的组件：可以直接用官方 Material3 组件。
- 主题库内部：可以直接桥接 `MaterialTheme`。
- App 业务层：尽量不要到处直接依赖 `MaterialTheme`，避免以后全局改主题时不好统一。

## 上传到哪里

本地开发阶段：

- 用 `publishToMavenLocal`。
- App 通过 `mavenLocal()` 引入。

团队或多项目共用时：

- 可以上传到私有 Maven 仓库。
- 可以上传到 GitHub Packages。
- 也可以放到公司自己的制品库，例如 Nexus、Artifactory。

不建议把这个主题上传回 Material3 源码项目。它应该是你自己的 App 设计系统库，Material3 只是底层依赖。

## 许可证说明

当前主题库没有复制 Material3 源码，只是通过依赖使用官方 Material3。因此这个自定义主题库可以使用你自己的许可证策略。

需要注意：

- 官方 Material3 / AndroidX 依赖本身是 Apache License 2.0。
- 如果你公开发布这个库，建议保留依赖许可证说明。
- 如果只在自己的 App 或团队内部使用，可以按你项目自己的规则处理。
- 当前没有内置第三方字体文件，所以没有额外字体文件许可证问题。

`theme/build.gradle.kts` 里的 POM 目前写的是 Apache License 2.0：

```kotlin
licenses {
    license {
        name.set("The Apache License, Version 2.0")
        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
    }
}
```

如果你不想公开开源，可以在正式发布前改成你自己的许可证或移除公开发布信息。

## 主要文件怎么改

- `Color.kt`：改亮色、暗色主题颜色。
- `Type.kt`：改字体和字号。
- `Shape.kt`：改圆角。
- `JasmineTokens.kt`：改自定义间距、尺寸 token。
- `Theme.kt`：Jasmine 主题入口，桥接官方 Material3。
- `components/`：放你自己的组件包装。

## 推荐使用方式

App 以后只认 Jasmine：

```kotlin
JasmineTheme {
    YourApp()
}
```

页面内部使用：

```kotlin
JasmineTheme.colorScheme
JasmineTheme.typography
JasmineTheme.shapes
JasmineTheme.spacing
JasmineTheme.sizing
```

这样 Material3 可以继续升级，但你的 App 风格由 Jasmine 这个独立主题库统一控制。
