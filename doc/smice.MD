# Android Studio 2021.3.1 最新 Xposed 模块编写指南

## 前言

最新的 Xposed 框架已经从 Xposed 到 EdXposed 再到 LSPosed，虽然 Xposed 的 API 依然是通用的 82 版本，但现在网上大多数的在 Android Studio 上配置 Xposed 的教程已经有点落后了，因此写下这篇来记录自己安装的流程。LSPosed 如何安装可以看我之前的小米 root 文章。

## 一. 配置 [`build.gradle`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)

找到 [app](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 目录下的 [`build.gradle`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)，在 [`dependencies`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里加上：

**compileOnly **'**de.robv.android.xposed:api:82**'

**compileOnly **de.robv.android.xposed:api:82:sources**'

这一步和网上的教程都是一样的，虽然 Xposed 的 API 最新的是 89，但最初的开发者只更新到 82，89 是后面其他开发者补充的，因此选用 82 是最稳定的。

<img alt="build.gradle" src="https://i-blog.csdnimg.cn/blog_migrate/37a16cb425bb129e7f27883f73f1e371.png">

不要像网上很多教程说的一样添加 `jcenter`，这个仓库在 21 年已经宣布停止更新了。现在跟 Android Studio 合作的是 [`maven`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)，或者可以用国内阿里云的镜像。找到 [`settings.gradle`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)（以前 [`build.gradle`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里的 `allprojects/repositories` 现在统一搬到这里了），在 [`repositories`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里加上：

`maven { url 'https://maven.aliyun.com/repository/public/'}`

或者

`maven { url "https://api.xposed.info/"}`

<img alt="例如" src="https://i-blog.csdnimg.cn/blog_migrate/0fc69c961c365db5ebd7a138b09fdd85.png">

至于从哪个仓库同步能成功我也不确定，或者你像我一样总是同步不成功，只能下载 `XposedBridgeApi-82.jar` 然后放进 `app/libs` 文件夹下，右击 jar 文件——>add to library，再把 [`dependencies`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 中的 `implementation files('libs\\XposedBridgeApi-82.jar')` 改成 `compileOnly files('libs\\XposedBridgeApi-82.jar')`。

跟 Xposed 有关的尽量不用 `implementation`，如果后面报错: `Cannot load module The Xposed API classes are compiled into the module’s APK. This may cause strange issues and must be fixed by the module developer.` 可能就是在 [`dependencies`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里用到了 `implementation`。

## 二. 配置 [`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)

在 [`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的 [`application`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 标签中加入：



<meta-data
android:name="xposedmodule"
android:value="true" />

<meta-data
android:name="xposeddescription"
android:value="这是一个 xposed demo" />

<meta-data
android:name="xposedminversion"
android:value="82" />

<img alt="在这里插入图片描述" src="https://i-blog.csdnimg.cn/blog\_migrate/b5d58edb3702018cb74b5ff6f120347a.png">

[`xposedmodule`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 告诉 Xposed 框架这是一个 Xposed 模块，[`xposeddescription`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 给你的模块添加描述，[`xposedminversion`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 表示支持的最低 Xposed 版本。

## 三. 创建 [`xposed_init`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)

在 [`main`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 目录下，[`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的同级，新建一个 [`assets`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 文件夹。

<img alt="在这里插入图片描述" src="https://i-blog.csdnimg.cn/blog\_migrate/7d803f98aaa63c73ae8eebd9f48a7b8e.png">

在 [`assets`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 文件夹下新建文件 [`xposed_init`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)，文件类型选择 [`text`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)，文件内容填上你要新建的 Xposed 类的名字。这个文件标记了你的 Xposed 模块的入口。

<img alt="在这里插入图片描述" src="https://i-blog.csdnimg.cn/blog\_migrate/8c062098141896841311c305482d69ab.png">

## 四. 编写 Xposed 类

在 `main/java/package name` 文件夹下，跟 `main activity` 同级，新建一个 Java 类，这就是你模块的功能主体，可以在这里添加 hook app 内方法的函数。以下是打印 app 包名的一个模块示例：

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

如果只是用作 LSPosed 的一个模块的话，`Main Activity` 其实是不需要的，可以选择 safe delete。但是要先把 [`AndroidManifest.xml`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 里 `Main Activity` 的部分删除并且把 `main/res/layout` 里的主界面布局文件删掉。

## 五. 调试

可以用手机连接 Android Studio，确保你的手机能够打开开发者选项里的 USB 调试和 USB 安装，并且电脑上已经装好 adb。

Android Studio 上打开 `File`——> [`settings`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)——> `System settings`——> `Android SDK`，下载好你手机 Android 版本对应的 SDK。

<img alt="在这里插入图片描述" src="https://i-blog.csdnimg.cn/blog\_migrate/ebc989d27a01ac453e43638417a392ec.png">

`File`——> [`settings`](vscode-file://vscode-app/d:/HOME/APP/Programmer/Microsoft%20VS%20Code/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html)——> `System settings`——> `Android SDK`——> `SDK Tools`，下载 `Google USB Driver`。

<img alt="在这里插入图片描述" src="https://i-blog.csdnimg.cn/blog\_migrate/df5600d0b5138a65ec92a4afc0a70f43.png">

用 USB 连接手机和电脑，打开设备管理器，选中便携设备中你的手机。选择驱动程序，更新驱动程序，选择刚刚安装的 USB Driver，路径一般是在你存放 SDK 的路径下：`Sdk\extras\google\usb_driver`。

如果安装后提示你的手机已经安装了最适合的驱动那么这步就不用做了。然后打开手机的 USB 调试和安装，运行 Android Studio 的 app，就能在手机上安装了，然后 LSPosed 也会检测到纳入这个模块。
