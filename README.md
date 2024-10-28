# Lsposed--Module-Model

https://highcapable.github.io/YukiHookAPI/zh-cn/about/changelog.html
# Xposed 模块开发详细教程

## 介绍

由教程集整合而来

## 准备工作

在开始之前，你需要准备好：

* 一台可以安装 Xposed 框架的手机（推荐 LSPosed、Android 10+）
* 一台可以编写代码并且装有 JDK 的电脑
* 一个名叫 [Android Studio](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的软件（当然你用 [IDEA](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 也没问题就是了）
* 一个反编译软件，如：[JADX](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)
* 一个可以查看布局的 App，如：开发者助手

其次，本文假定你已经学会以下内容：

* 会 Java/Kotlin 其中一种语言（强烈推荐 Kotlin，对模块开发特别友好）
* Java 的反射
* Android 的基础套件（如 Context、View 等）

## 创建项目 & 引入依赖

首先，我们打开 Android Studio 创建一个空项目，不需要任何 Activity，语言凭个人喜好创建。

### Java 项目

在 [`settings.gradle`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 中添加以下内容：

**dependencyResolutionManagement **{

**    repositoriesMode**.**set**(**RepositoriesMode**.**FAIL\_ON\_PROJECT\_REPOS**)

**    repositories **{

**        google**(**)**

**        mavenCentral**(**)**

**        maven **{** url **'**https://api.xposed.info/**'** **}**  **// 添加这一行即可

**    **}

**}**

在 `app/build.gradle` 中引入 Xposed 的依赖：

**dependencies **{

**    compileOnly **'**de.robv.android.xposed:api:82**'** **// 添加我

**    **// compileOnly **'de.robv.android.xposed:api:82:sources' // **不要导入源码，这会导致 IDEA 无法索引文件，从而让语法提示失效

**}**

### Kotlin 项目

在 [settings.gradle.kts](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 中添加以下内容：

**dependencyResolutionManagement **{

**    repositoriesMode**.**set**(**RepositoriesMode**.**FAIL\_ON\_PROJECT\_REPOS**)

**    repositories **{

**        google**(**)**

**        mavenCentral**(**)**

**        maven **{** url **=** uri**(**"**https://api.xposed.info/**"**)** **}** **// 添加这一行

**    **}

**}**

在 [build.gradle.kts](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 中引入 Xposed 的依赖：

**dependencies **{

**    compileOnly**(**"**de.robv.android.xposed:api:82**"**)** **// 添加我

**}**

## 配置 AndroidManifest.xml

在 [`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的 `<application>` 标签中加入以下内容：

**<**meta-data

**    **android:name**=**"**xposedmodule**"

**    **android:value**=**"**true**"** />**

**<**meta-data

**    **android:name**=**"**xposeddescription**"

**    **android:value**=**"**这是一个 xposed demo**"** />**

**<**meta-data

**    **android:name**=**"**xposedminversion**"

**    **android:value**=**"**82**"** />**

**<**meta-data

**    **android:name**=**"**xposedscope**"

**    **android:resource**=**"**@array/xposedscope**"**/>**

## 创建 xposed\_init 文件

在 [`src/main`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 目录下，新建一个 [`assets`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 文件夹，并在其中创建一个名为 [`xposed_init`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的文件，文件内容填上你要新建的 Xposed 类的名字。例如：

**me.kyuubiran.xposedtutorial.MainHook**

## 编写 Xposed 类

在 `src/main/java/package name` 文件夹下，新建一个 Java 类，这就是你模块的功能主体。以下是一个打印 app 包名的模块示例：

**package** **com.xposed.ssl**;

**import** **de.robv.android.xposed.IXposedHookLoadPackage**;

**import** **de.robv.android.xposed.XC\_MethodHook**;

**import** **de.robv.android.xposed.XposedBridge**;

**import** **de.robv.android.xposed.callbacks.XC\_LoadPackage**;

**public** **class** **passHook** **implements** **IXposedHookLoadPackage** **{**

**    @**Override

**    **public** **void** **handleLoadPackage**(**XC\_LoadPackage**.**LoadPackageParam** **lpparam**)** **throws** **Throwable** **{**

**        **XposedBridge**.**log**(**"**Loaded app: **"** **+** **lpparam**.**packageName**)**;

**    **}

**}**

## 调试

可以用手机连接 Android Studio，确保你的手机能够打开开发者选项里的 USB 调试和 USB 安装，并且电脑上已经装好 adb。

## 其他注意事项

* 如果你移除了所有依赖只保留了 Xposed，你就会发现，你的项目不能 build，会直接报错。可以通过移除 [`src/res/values/themes.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里面的主题和 [`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 文件里 [`<application ... />`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 中的 [`android:theme="xxx"`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 那一行来解决。
* 在 [values](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 目录下创建 [`arrays.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)，填入模块的作用域应用的包名。

**<**resources**>**

**    <**string-array** **name**=**"**xposedscope**"** >**

**        <**item**>me.kyuubiran.xposedapp</**item**> **

**    </**string-array**>**

**</**resources**>**

## 参考资料

* [《安卓逆向这档事》系列教程](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)
* [Xposed 模块开发入门保姆级教程](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)
* [Android Studio 最新 Xposed 模块编写指南](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)

希望这个详细的教程能帮助你顺利开发 Xposed 模块！
