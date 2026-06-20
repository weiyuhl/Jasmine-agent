# Jasmine 使用说明

`Jasmine` 是给 App 使用的 Compose 设计系统库，当前拆成两个制品：

- `com.lhzkml.jasmine:jasmine-theme:0.1.0`：Jasmine 自定义主题、颜色、字体、圆角、间距 token。
- `com.lhzkml.jasmine:jasmine-components:0.1.0`：Jasmine 组件库，从当前工作区的 AndroidX Material3 / Material3 Adaptive 源码复刻整理，不桥接官方 `androidx.compose.material3:material3`。

父级工作区里的 Material3 项目是源码参考。Jasmine 组件源码保留 AndroidX Apache 2.0 许可头，包名改为 `com.lhzkml.jasmine.components`。

## 风格

- 简约、干净、偏应用工具型。
- 主背景和 Surface 以白色为主。
- 主要操作色是接近黑色的中性色。
- 辅助线、边框、禁用态使用灰色。
- 不使用紫色、紫蓝渐变或 AI 风格渐变。
- 字体使用 Compose 默认系统字体路线，没有内置第三方字体文件。

## 已复刻的 App 关注组件

普通组件：

- `Surface`
- `BadgedBox`
- `Badge`
- `Scaffold`
- `TopAppBar`
- `CenterAlignedTopAppBar`
- `MediumTopAppBar`
- `MediumFlexibleTopAppBar`
- `LargeTopAppBar`
- `LargeFlexibleTopAppBar`
- `TwoRowsTopAppBar`
- `AppBarRow`
- `AppBarColumn`
- `AppBarOverflowIndicator`
- `TooltipBox`
- `PlainTooltip`
- `RichTooltip`
- `IconButton`
- `Icon`
- `Text`
- `DismissibleNavigationDrawer`
- `DismissibleDrawerSheet`
- `NavigationDrawerItem`
- `NavigationSuiteScaffold`
- `Button`
- `ElevatedButton`
- `FilledTonalButton`
- `OutlinedButton`
- `TextButton`
- `Checkbox`
- `TriStateCheckbox`
- `TextField`
- `OutlinedTextField`
- `SecureTextField`
- `OutlinedSecureTextField`
- `PasswordTextField`
- `OutlinedPasswordTextField`
- `CircularProgressIndicator`
- `Snackbar`
- `SnackbarHost`
- `AlertDialog`
- `BasicAlertDialog`
- `FloatingActionButtonMenu`
- `FloatingActionButtonMenuItem`
- `ToggleFloatingActionButton`
- `ToggleFloatingActionButton` 默认 Add/Close 图标重载
- `PullToRefreshBox`
- `pullToRefresh`
- `PullToRefreshDefaults.Indicator`
- `PullToRefreshDefaults.LoadingIndicator`
- `LoadingIndicator`
- `ContainedLoadingIndicator`
- `LinearWavyProgressIndicator`
- `CircularWavyProgressIndicator`
- `DropdownMenu`
- `DropdownMenuItem`
- `DropdownMenuPopup`
- `DropdownMenuGroup`
- `ExposedDropdownMenuBox`
- `BottomSheet`
- `BottomSheetScaffold`
- `ModalBottomSheet`
- `Switch`
- `SearchBar`
- `DockedSearchBar`
- `TopSearchBar`
- `ExpandedFullScreenSearchBar`
- `ExpandedDockedSearchBar`
- `HorizontalDivider`

Adaptive / Navigation3 相关能力：

- `currentWindowAdaptiveInfoV2`
- `calculatePaneScaffoldDirective`
- `rememberListDetailSceneStrategy`
- `rememberNavigationSuiteScaffoldState`

状态和配置：

- `BadgeDefaults`
- `DrawerState`
- `DrawerValue`
- `rememberDrawerState`
- `TopAppBarDefaults`
- `TopAppBarColors`
- `TopAppBarScrollBehavior`
- `TopAppBarState`
- `rememberTopAppBarState`
- `AppBarScope`
- `AppBarRowScope`
- `AppBarColumnScope`
- `AppBarMenuState`
- `TooltipDefaults`
- `TooltipState`
- `TooltipScope`
- `TooltipAnchorPosition`
- `rememberTooltipState`
- `RichTooltipColors`
- `ButtonDefaults`
- `ButtonColors`
- `ButtonElevation`
- `ButtonShapes`
- `CheckboxDefaults`
- `CheckboxColors`
- `TextFieldDefaults`
- `OutlinedTextFieldDefaults`
- `TextFieldColors`
- `TextFieldLabelScope`
- `SnackbarHostState`
- `AlertDialogDefaults`
- `FloatingActionButtonMenuScope`
- `ToggleFloatingActionButtonDefaults`
- `ToggleFloatingActionButtonScope`
- `PullToRefreshState`
- `rememberPullToRefreshState`
- `PullToRefreshDefaults`
- `LoadingIndicatorDefaults`
- `WavyProgressIndicatorDefaults`
- `MaterialShapes`
- `MenuDefaults`
- `MenuItemColors`
- `MenuItemShapes`
- `MenuGroupShapes`
- `MenuAnchorPosition`
- `DropdownMenuPopupPositionProvider`
- `ExposedDropdownMenuDefaults`
- `ExposedDropdownMenuBoxScope`
- `ExposedDropdownMenuAnchorType`
- `SheetState`
- `SheetValue`
- `BottomSheetScaffoldState`
- `rememberBottomSheetState`
- `rememberBottomSheetScaffoldState`
- `rememberStandardBottomSheetState`
- `rememberModalBottomSheetState`
- `BottomSheetDefaults`
- `ModalBottomSheetDefaults`
- `ModalBottomSheetProperties`
- `SwitchDefaults`
- `SwitchColors`
- `SearchBarState`
- `SearchBarValue`
- `rememberSearchBarState`
- `rememberContainedSearchBarState`
- `rememberSearchBarWithGapState`
- `SearchBarDefaults`
- `SearchBarColors`
- `SearchBarScrollBehavior`
- `DividerDefaults`

组件库内部还保留了一些支撑 API，例如颜色、排版、形状、ripple、navigation bar/rail、adaptive layout 等。这些是为了让上面的组件完整工作，不是官方 Material3 依赖桥接。

## 本地构建环境

本项目 Gradle Wrapper 使用 `9.5.1`。本机已验证可用环境：

```powershell
$env:JAVA_HOME="D:\jdk-17.0.2"
$env:ANDROID_HOME="D:\Android\Sdk"
$env:ANDROID_SDK_ROOT=$env:ANDROID_HOME
$env:PATH="$env:JAVA_HOME\bin;$env:ANDROID_HOME\platform-tools;$env:PATH"
```

构建检查：

```powershell
.\gradlew.bat :theme:assembleRelease :components:assembleRelease
```

## 发布到本机 Maven

在 `jasmine-theme` 目录执行：

```powershell
.\gradlew.bat :theme:publishToMavenLocal :components:publishToMavenLocal
```

发布成功后，App 项目通过 `mavenLocal()` 引入。

## App 引入

在 App 项目的仓库配置里加入：

```kotlin
repositories {
    mavenLocal()
    google()
    mavenCentral()
}
```

App 模块依赖：

```kotlin
dependencies {
    implementation("com.lhzkml.jasmine:jasmine-theme:0.1.0")
    implementation("com.lhzkml.jasmine:jasmine-components:0.1.0")
}
```

如果你改了 `group` 或 `version`，这里也要同步修改。

## App 使用

在 `setContent` 最外层包一层 `JasmineTheme`：

```kotlin
import com.lhzkml.jasmine.theme.JasmineTheme

setContent {
    JasmineTheme {
        App()
    }
}
```

业务页面使用 Jasmine 组件：

```kotlin
import com.lhzkml.jasmine.components.Scaffold
import com.lhzkml.jasmine.components.Text
import com.lhzkml.jasmine.components.TopAppBar

Scaffold(
    topBar = {
        TopAppBar(title = { Text("Jasmine") })
    },
) { paddingValues ->
    Text("Hello Jasmine")
}
```

主题 token：

```kotlin
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import com.lhzkml.jasmine.components.Text
import com.lhzkml.jasmine.theme.JasmineTheme

Text(
    text = "Hello",
    color = JasmineTheme.colorScheme.onSurface,
    style = JasmineTheme.typography.bodyLarge,
    modifier = Modifier.padding(JasmineTheme.spacing.md),
)
```

Adaptive / Navigation Suite 示例导入：

```kotlin
import com.lhzkml.jasmine.components.adaptive.currentWindowAdaptiveInfoV2
import com.lhzkml.jasmine.components.adaptive.layout.calculatePaneScaffoldDirective
import com.lhzkml.jasmine.components.adaptive.navigation3.rememberListDetailSceneStrategy
import com.lhzkml.jasmine.components.adaptive.navigationsuite.NavigationSuiteScaffold
import com.lhzkml.jasmine.components.adaptive.navigationsuite.rememberNavigationSuiteScaffoldState
```

## includeBuild 接入

如果主项目用 `includeBuild("../jasmine-theme")` 接入，需要注意：

- 同一个 Gradle 构建里 Android Gradle Plugin 版本要统一。当前 Jasmine 使用 `9.3.0-alpha12`。
- included build 不会自动读取主项目的 `local.properties`，构建前需要设置 `ANDROID_HOME` / `ANDROID_SDK_ROOT`。

不想处理 included build 插件版本统一问题时，推荐先 `publishToMavenLocal`，再由 App 通过 Maven 坐标引入。

## 上传到哪里

本地开发阶段：

- 用 `publishToMavenLocal`。
- App 通过 `mavenLocal()` 引入。

团队或多项目共用时：

- 上传到私有 Maven 仓库。
- 上传到 GitHub Packages。
- 上传到公司制品库，例如 Nexus 或 Artifactory。

不建议上传回 Material3 源码项目。Jasmine 应该作为你自己的 App 设计系统库发布和维护。

## 许可证

`jasmine-components` 复制并改造了 AndroidX Material3 / Material3 Adaptive 源码，所以需要遵守 Apache License 2.0：

- 保留复制源码文件里的 Apache 2.0 copyright header。
- 发布组件库时保留 Apache 2.0 许可说明。
- 如果公开分发，建议附带 NOTICE / LICENSE 说明来源为 AndroidX Material3。

`jasmine-theme` 是自定义主题代码，没有复制官方 Material3 组件实现；当前 POM 也按 Apache License 2.0 发布，方便和组件库统一。

## 主要文件

- `theme/src/main/java/com/lhzkml/jasmine/theme/Color.kt`：Jasmine 颜色。
- `theme/src/main/java/com/lhzkml/jasmine/theme/Type.kt`：字体和字号。
- `theme/src/main/java/com/lhzkml/jasmine/theme/Shape.kt`：圆角。
- `theme/src/main/java/com/lhzkml/jasmine/theme/JasmineTokens.kt`：间距、尺寸 token。
- `theme/src/main/java/com/lhzkml/jasmine/theme/Theme.kt`：Jasmine 主题入口。
- `components/src/main/java/com/lhzkml/jasmine/components/`：Jasmine 普通组件。
- `components/src/main/java/com/lhzkml/jasmine/components/adaptive/`：Jasmine adaptive / navigation suite 组件。
