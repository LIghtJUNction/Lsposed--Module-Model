## [从零开始编写Xposed模块](https://lbqaq.top/p/init-xposed/)

### 记录第一次编写Xposed模块的步骤

2023-07-21

阅读时长: 13 分钟

之前写过一篇关于[Magisk模块](https://lbqaq.top/p/%E7%AE%80%E6%98%93magisk%E6%A8%A1%E5%9D%97%E7%9A%84%E5%88%B6%E4%BD%9C/)的编写的文章，不过Magisk修改的都是比较偏系统的，一般情况下用的并不是很多。于是我便把目光转向Xposed，毕竟会Hook真的太酷辣😎

## [#](https://lbqaq.top/p/init-xposed/#xposed%e7%9a%84%e5%9f%ba%e6%9c%ac%e6%a6%82%e5%bf%b5) Xposed的基本概念

提到Xposed，爱玩机的用户都应该听过他的大名，作为一款可以在不修改APK的情况下影响程序运行的框架，可以基于它可以制作出许多功能强大的模块，且在功能不冲突的情况下同时运作。微信防撤回、步数修改、去广告、美化…….他能实现许许多多的功能，可以说没有它玩机的世界将会少很多的乐趣。

它的原理就是通过替换系统原本的`app_process`，加载一个额外的jar包，从而实现对zygote进程及其创建的Dalvik/ART虚拟机的劫持。

没听懂？没关系，~我也不懂~。目前我们只要关注如何使用即可了，至于原理可以先放一放。

## [#](https://lbqaq.top/p/init-xposed/#%e7%8e%af%e5%a2%83%e5%87%86%e5%a4%87) 环境准备

在开始之前，我们需要：

* 一台可以安装Xposed框架的手机（推荐LSPosed、Android 10+）
* 一台可以编写代码并且装有jdk的电脑
* 一个名叫[Android Studio](https://developer.android.com/studio)的软件（我主打一个叛逆，用[IDEA](https://www.jetbrains.com/zh-cn/idea/download/)同样可以）
* 一个反编译软件，如：[JADX](https://github.com/skylot/jadx)
* 一个可以查看布局的App，如：开发者助手

这次我打算从一个实例出发：小明手机上装了一些恶意软件，我们需要通过Xposed进行Hook，不让小明启动这些软件。

## [#](https://lbqaq.top/p/init-xposed/#%e5%87%86%e5%a4%87%e5%b7%a5%e4%bd%9c) 准备工作

### [#](https://lbqaq.top/p/init-xposed/#%e5%88%9b%e5%bb%ba%e9%a1%b9%e7%9b%ae%e5%bc%95%e5%85%a5%e4%be%9d%e8%b5%96) 创建项目&引入依赖

首先在IDEA选择新建项目，可以看到生成器下有Android这项，我们选择它。

第一次选择时可能会要求你下载安卓的SDK，按照指示一步一步进行就好。

#### [#](https://lbqaq.top/p/init-xposed/#java) Java

我们选择创建一个`No Activity`，语言选择`Java`，SDK选默认的`API 24`就可。

然后，我们需要引入Xposed的库，不过它并没有上传到MavenCentral上，所以我们需要在`settings.gradle`里修改一下(gradle 7.0+)

打开`settings.gradle`，添加一行代码

```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://api.xposed.info/' }  // 添加这一行即可
    }
}
```

之后，进入我们app目录下的build.gradle，引入xposed的依赖


```gradle
dependencies {
    compileOnly 'de.robv.android.xposed:api:82' //添加我
    // compileOnly 'de.robv.android.xposed:api:82:sources' // 不要导入源码，这会导致idea无法索引文件，从而让语法提示失效
}
```


|  |  |
| - | - |

我们还要在`./app/src/main/res/values`目录下创建`arrays.xml`，填入下面的内容：


```xml
<resources>
    <string-array name="xposedscope" >
        <!-- 这里填写模块的作用域应用的包名，可以填多个。 -->
        <item>ceui.lisa.pixiv</item>
        <item>com.xjs.ehviewer</item>
        <item>com.picacomic.fregata</item>
    </string-array>
</resources>
```


|  |  |
| - | - |

这一步主要是指定模块的作用域包名，效果就是在Lsposed中勾选作用域时会在应用下提示推荐应用。

[![推荐应用效果](https://lbqaq.top/p/init-xposed/IMAGE/1.jpg)](https://lbqaq.top/p/init-xposed/IMAGE/1.jpg)
推荐应用效果

最后，我们在Run那里编辑一下启动配置，勾选`Always install with package manager`并且将`Launch Options`改成`Nothing`

#### [#](https://lbqaq.top/p/init-xposed/#kotlin) Kotlin

创建项目的部分和Java的设置没有什么区别，只不过是需要将语言切换一下。新版本的Android Studio将原来的打包语言换成了Kotlin，所以我们的设置有一些不一样的地方。[2024.09.15更新](https://lbqaq.top/p/init-xposed/#20240915)

原来的`settings.gradle`变为`settings.gradle.kts`


```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url =  uri("https://api.xposed.info/") } //添加这一行
    }
}
```



原来的`build.gradle`变为`build.gradle.kts`

```gradle
dependencies {
    compileOnly("de.robv.android.xposed:api:82") //添加我
}
```



### [#](https://lbqaq.top/p/init-xposed/#%e5%a3%b0%e6%98%8e%e6%a8%a1%e5%9d%97) 声明模块

接下来就是要声明我们是一个Xposed模块，方便框架发现。

在`./app/src/main/AndroidManifest.xml`里，我们将`<application ... />`改成以下形式（注意，是改成！就是把结尾的`/>`换成`> </application>`）


```xml
<application ... > 
		<!-- 是否是xposed模块，xposed根据这个来判断是否是模块 -->
        <meta-data
                android:name="xposedmodule"
                android:value="true" />
        <!-- 模块描述，显示在xposed模块列表那里第二行 -->
        <meta-data
                android:name="xposeddescription"
                android:value="不可以涩涩" />
        <!-- 最低xposed版本号(lib文件名可知,一般填54即可) -->
        <meta-data
                android:name="xposedminversion"
                android:value="54" />
        <!-- 模块作用域 -->
        <meta-data
                android:name="xposedscope"
                android:resource="@array/xposedscope"/>
    </application>
```



然后在`src/main`目录下创建一个文件夹名叫`assets`，并且创建一个文件叫`xposed_init`，**注意，它没有后缀名！！**。

接着我们需要创建一个入口类，名叫`MainHook`（或者随便你想取什么名字都行），创建好后回到我们的`xposed_init`里并用文本文件的方式打开它，输入我们刚刚创建的类的完整路径。如：`top.lbqaq.nosese.MainHook`，同时**注意大小写**。

完成以上步骤后，我们就可以正式开始编写Xposed模块了。

## [#](https://lbqaq.top/p/init-xposed/#%e6%a8%a1%e5%9d%97%e7%bc%96%e5%86%99) 模块编写

### [#](https://lbqaq.top/p/init-xposed/#mainhook) MainHook

在`MainHook`里，我们需要实现Xposed的IXposedHookLoadPackage接口，以便执行Hook操作。将以下内容替换原来的类

```java
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 过滤不必要的应用
        if (!lpparam.packageName.equals("ceui.lisa.pixiv")) return;
        // 执行Hook
        hook(lpparam);
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        // 具体流程
    }
}
```



对于Kotlin，代码如下：


```kotlin
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

class MainHook : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 过滤不必要的应用
        if (lpparam.packageName != "ceui.lisa.pixiv") return
        // 执行Hook
        hook(lpparam)
    }

    private fun hook(lpparam: XC_LoadPackage.LoadPackageParam) {
        // 具体流程
    }
}
```



到这里，我们的准备工作已经完成，安装模块并在框架中激活它！

### [#](https://lbqaq.top/p/init-xposed/#%e9%98%bb%e6%ad%a2%e5%ba%94%e7%94%a8%e5%90%af%e5%8a%a8) 阻止应用启动

接下来，我们需要反编译程序来找到需要Hook的点。根据实例的要求，我们需要阻止小明启动某些应用。那么，我们只需要Hook启动函数，让其无法运行即可。

在此之前，我们要先了解Android的四大组件之一——“Activity(活动)”

> 在应用中的一个Activity可以用来表示一个界面，意思可以理解为“活动”，即一个活动开始，代表 Activity组件启动，活动结束，代表一个Activity的生命周期结束。一个Android应用必须通过Activity来运行和启动，Activity的生命周期交给系统统一管理。


| 函数名称    | 描述                                                                                                                               |
| ----------- | ---------------------------------------------------------------------------------------------------------------------------------- |
| onCreate()  | 一个Activity启动后第一个被调用的函数，常用来在此方法中进行Activity的一些初始化操作。例如创建View，绑定数据，注册监听，加载参数等。 |
| onStart()   | 当Activity显示在屏幕上时，此方法被调用但此时还无法进行与用户的交互操作。                                                           |
| onResume()  | 这个方法在onStart()之后调用，也就是在Activity准备好与用户进行交互的时候调用，此时的Activity一定位于Activity栈顶，处于运行状态。    |
| onPause()   | 这个方法是在系统准备去启动或者恢复另外一个Activity的时候调用，通常在这个方法中执行一些释放资源的方法，以及保存一些关键数据。       |
| onStop()    | 这个方法是在Activity完全不可见的时候调用的。                                                                                       |
| onDestroy() | 这个方法在Activity销毁之前调用，之后Activity的状态为销毁状态。                                                                     |
| onRestart() | 当Activity从停止stop状态恢进入start状态时调用状态。                                                                                |

当 Activity 进入新状态时，系统会调用其中每个回调。

[![Activity生命周期](https://lbqaq.top/p/init-xposed/IMAGE/activity_lifecycle.png)](https://lbqaq.top/p/init-xposed/IMAGE/activity_lifecycle.png)
Activity生命周期

那么，我们的思路就很明确了：只要找到这些程序的起始Activity，并Hook它的`onCreate()`函数，在其启动时就将其杀死，这样能实现无法启动该程序。

我们使用开发者助手，选择布局查看，然后打开目标应用。可以看到，它显示了当前的包名和当前Activity。

[![布局查看](https://lbqaq.top/p/init-xposed/IMAGE/2.jpg)](https://lbqaq.top/p/init-xposed/IMAGE/2.jpg)
布局查看

接下来，我们使用jadx-gui，反编译该应用，找到该Activity(`ceui.lisa.activities.MainActivity`)，并寻找是否有`onCreate()`函数，如果存在直接Hook即可。

[![jadx-gui反编译](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721100655336.png)](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721100655336.png)
jadx-gui反编译

然而，这个程序居然没有😥，说明它没有重写该方法，那该如何做呢？这时就可以用第二种方法了。

### [#](https://lbqaq.top/p/init-xposed/#%e9%81%8d%e5%8e%86%e6%89%80%e6%9c%89%e7%b1%bb%e4%b8%8b%e7%9a%84%e6%89%80%e6%9c%89%e6%96%b9%e6%b3%95) 遍历所有类下的所有方法

从标题就能看出来，我直接把你所有能Hook的方法全部读出来，那不就随便我挑了嘛。

我们知道，Java程序都运行在jvm虚拟机中，jvm虚拟机通过ClassLoader动态装载所需的class。那么，我们直接Hook ClassLoader ，不就知道你有哪些方法被加载进来了。


```java
public void hook(XC_LoadPackage.LoadPackageParam lpparam) {
    XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod(param);
            Class clazz = (Class) param.getResult();
            String clazzName = clazz.getName();
            //排除非包名的类
            if(clazzName.contains("ceui.lisa")){
                Method[] mds = clazz.getDeclaredMethods();
                for(int i =0;i<mds.length;i++){
                    final Method md = mds[i];
                    int mod = mds[i].getModifiers();
                    //去除抽象、native、接口方法
                    if(!Modifier.isAbstract(mod)
                            && !Modifier.isNative(mod)
                            &&!Modifier.isAbstract(mod)){
                        XposedBridge.hookMethod(mds[i], new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                Log.d("lbqaq",md.toString());
                            }
                        });
                    }
                }
            }
        }
    });
}
```


|  |  |
| - | - |

（PS：这个程序很奇怪，包名有pixiv，但类名没有，所以用`ceui.lisa.pixiv`会报空指针异常）

将上面这段代码复制到之前写好的`MainHook`中去，编译推送到手机。

在终端执行`adb logcat "lbqaq:D *:S"`开启logcat并进行过滤。


```text
❯ adb logcat "lbqaq:D *:S"
--------- beginning of main
07-21 10:49:48.185  5050  5050 D lbqaq   : public void ceui.lisa.activities.Shaft.onCreate()
07-21 10:49:48.194  5050  5050 D lbqaq   : private void ceui.lisa.activities.Shaft.updateTheme()
07-21 10:49:48.216  5050  5050 D lbqaq   : public static android.content.Context ceui.lisa.activities.Shaft.getContext()
07-21 10:49:48.238  5050  5050 D lbqaq   : protected int ceui.lisa.activities.MainActivity.initLayout()
07-21 10:49:48.238  5050  5050 D lbqaq   : public boolean ceui.lisa.activities.MainActivity.hideStatusBar()
07-21 10:49:48.276  5050  5050 D lbqaq   : protected void ceui.lisa.activities.MainActivity.initView()
07-21 10:49:48.276  5050  5050 D lbqaq   : public static com.tencent.mmkv.MMKV ceui.lisa.activities.Shaft.getMMKV()
07-21 10:49:48.276  5050  5050 D lbqaq   : private void ceui.lisa.activities.MainActivity.initDrawerHeader()
07-21 10:49:48.278  5050  5050 D lbqaq   : public androidx.drawerlayout.widget.DrawerLayout ceui.lisa.activities.MainActivity.getDrawer()
07-21 10:49:48.279  5050  5050 D lbqaq   : protected void ceui.lisa.activities.MainActivity.initData()
07-21 10:49:48.279  5050  5050 D lbqaq   : private void ceui.lisa.activities.MainActivity.initFragment()
```



可以看到该程序的调用方法。这里，我们选择靠后的`ceui.lisa.activities.MainActivity.initFragment()`作为我们的Hook目标。

为什么要选择靠后的方法作为我们的Hook目标呢？

这是由于如果选择较前的方法，有些变量还没初始化完成，这时调用`finish()`可能会报错。

### [#](https://lbqaq.top/p/init-xposed/#hook-activity) Hook activity

我们使用最基础的hook方式，即Xposed自带的`XposedHelpers.findAndHookMethod`，使用方法如下：



```
private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
    // 它有两个重载，区别是一个是填Class，一个是填ClassName以及ClassLoader
    // 第一种 填ClassName
    XC_MethodHook.Unhook unhook = XposedHelpers.findAndHookMethod("me.kyuubiran.xposedapp.MainActivity",    // className
            lpparam.classLoader,    // classLoader 使用lpparam.classLoader
            "onCreate",             // 要hook的方法
            Bundle.class,           // 要hook的方法的参数表，如果有多个就用逗号隔开 
            new XC_MethodHook() {   // 最后一个填hook的回调
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {} // Hook方法执行前  
                @Override
                protected void afterHookedMethod(MethodHookParam param) {} // Hook方法执行后
            });
    // 它返回一个unhook 在你不需要继续hook的时候可以调用它来取消Hook
    unhook.unhook();    // 取消空的Hook 

    // 第二种方式 填Class
    // 首先你得加载它的类 我们使用XposedHelpers.findClass即可 参数有两个 一个是类名 一个是类加载器
    Class<?> clazz = XposedHelpers.findClass("me.kyuubiran.xposedapp.MainActivity", lpparam.classLoader);
    XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param){
            // 由于我们需要在Activity创建之后再弹出Toast，所以我们Hook方法执行之后
            Toast.makeText((Activity) param.thisObject, "模块加载成功！", Toast.LENGTH_SHORT).show();
        }
    });
}

```

根据上面的示例，我们就可以写出对应的方法了。那么，我们该如何结束这个程序呢？在前面我们介绍了Activity的生存周期，通过查询可知Activity存在一个关闭的方法`finish()`。所以我们只要手动调用即可，具体的代码如下。

```java
if (lpparam.packageName.equals("ceui.lisa.pixiv")){
    XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initFragment", new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
            ((Activity) param.thisObject).finish();
        }
    });
}
```



同样，另外两个应用也可以通过这种方式实现


    XposedHelpers.findAndHookMethod("com.hippo.ehviewer.ui.MainActivity", lpparam.classLoader, "initUserImage", new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
            ((Activity) param.thisObject).finish();
        }
    });
}



```java
if(lpparam.packageName.equals("com.picacomic.fregata")){
    XposedHelpers.findAndHookMethod("com.picacomic.fregata.activities.SplashActivity", lpparam.classLoader, "onCreate", Bundle.class ,new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
            ((Activity) param.thisObject).finish();
        }
    });
}
```


这里要注意，使用`findAndHookMethod`时要hook的方法的参数表一定要填对。如果没有填对就会报`java.lang.NoSuchMethodError`错误。我之前被这个问题折磨了很久😭（仔细想想也是这样，Java里有重载机制，参数不同就不是同一个方法了）

kotlin所对应的代码如下：


```kotlin
if (lpparam.packageName == "com.xjs.ehviewer"){
    XposedHelpers.findAndHookMethod("com.hippo.ehviewer.ui.MainActivity", lpparam.classLoader, "initUserImage",object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam){
            Toast.makeText(param.thisObject as Activity,"不许涩涩！",Toast.LENGTH_SHORT).show()
            (param.thisObject as Activity).finish()
        }
    })
}
        
```


### [#](https://lbqaq.top/p/init-xposed/#%e4%bf%ae%e6%94%b9%e5%86%85%e9%83%a8%e5%8f%82%e6%95%b0) 修改内部参数

虽然一股脑关闭程序非常简单，但小明不乐意了。对于`ceui.lisa.pixiv`来说，只有长按头像才会触发，直接一刀切实在是太粗暴了。没问题，直接安排上。

首先我们要定位到这个切换的方法究竟在哪？仔细观察，每次切换都会弹出一个颜文字的Toast，我们就从此处切入。这两个颜文字分别为`ԅ(♡﹃♡ԅ)`和`X﹏X`。

我们使用jadx-gui反编译程序，全局搜索，直接就找到了所在位置。

[![全局搜索](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721141436862.png)](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721141436862.png)
全局搜索

双击查看此处的代码，坏起来了，这是一个匿名函数，根据上面的方法，我们没有这个函数名，自然也就无法对其进行Hook。

[![具体代码](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721141535571.png)](https://lbqaq.top/p/init-xposed/IMAGE/image-20230721141535571.png)
具体代码

难道就要卡在这里了吗？~如果真是这样我就不会写这篇文章了~

仔细分析这段代码，这里对`this.userHead`设置了一个`OnLongClickListener`。一个控件只能绑定一个侦听器，所以我们可以进行一个替换，不就实现对其的Hook了吗😎

直接Hook `initView`这个方法：


```java
if (lpparam.packageName.equals("ceui.lisa.pixiv")){
    XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initView", new XC_MethodHook() {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            Field f = param.thisObject.getClass().getDeclaredField("userHead");
            f.setAccessible(true);
            ImageView v = (ImageView) f.get(param.thisObject);
//          v.setLongClickable(false);    //设置其无法响应
            v.setOnLongClickListener(v1 -> {
                Toast.makeText((Activity) param.thisObject, "不许涩涩！", Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    });
}
```


我们这里使用了Java的反射机制，拿到了它内部的变量`userHead`，然后通过`setAccessible`将其设置为可访问。这样我们就能对其私有变量进行修改了。

我们可以简单的将其可点击关闭，但是这样一点也不酷。我直接使用自己的函数将其进行替换，这样每次点击都会出现一个`Toast`。

kotlin的代码如下：


```kotlin
if (lpparam.packageName == "ceui.lisa.pixiv"){
    XposedHelpers.findAndHookMethod("ceui.lisa.activities.MainActivity", lpparam.classLoader, "initView",object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam){
            val f = param.thisObject.javaClass.getDeclaredField("userHead")
            f.isAccessible = true
            val v = f[param.thisObject] as ImageView
            v.setOnLongClickListener{
                Toast.makeText(param.thisObject as Activity,"不许涩涩！",Toast.LENGTH_SHORT).show()
                true
            }
    }
    })
}
```


至此，一个简单的Xposed模块就写好了。

## [#](https://lbqaq.top/p/init-xposed/#%e9%85%8d%e7%bd%ae%e6%96%87%e4%bb%b6%e8%af%bb%e5%8f%96) 配置文件读取

现在的模块往往还会搭配一个UI来实现配置文件的设置，在安卓开发中，常用`SharedPreferences`来实现配置的保存。随着google的更新，现在的配置文件无法设置`MODE_WORLD_READABLE`的权限，即只允许本应用读取。虽然Xposed本身提供了`XSharedPreferences`来读取配置文件，然而模块是依附于宿主程序所执行的，它的访问权限和宿主一致，所以无法访问到我们的配置文件。[2024.09.15更新](https://lbqaq.top/p/init-xposed/#20240915)

好在目前常用的框架LSPosed提供了[New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences)，我们只需要指定xposedAPI的最低版本≥93就可以了。

在`AndroidManifest.xml`里将`xposedminversion`的值改为`93`

对于模块的Activity来说，我们只需要指定权限为`Context.MODE_WORLD_READABLE`即可

```kotlin
val sharedPreferences : SharedPreferences = getSharedPreferences("config", Context.MODE_WORLD_READABLE)
```


对于hook的应用来说，我们使用原来的`XSharedPreferences`即可


```kotlin
val xsp = XSharedPreferences("top.lbqaq.nosese","config")
```


## [#](https://lbqaq.top/p/init-xposed/#%e7%bb%93%e8%af%ad) 结语

完整的项目代码可以在[Github仓库](https://github.com/luoboQAQ/nosese)中查看，通过这次的编写，我发现Xposed实际上还是一个工具，想要实现去广告等功能，都是通过反编译来找到突破口，有了Hook的目标后才需要Xposed。

所以，之后的学习还是要以安卓逆向为主，只有打好基本功，才能写出优秀的代码。

## [#](https://lbqaq.top/p/init-xposed/#%e5%90%8e%e7%bb%ad%e6%9b%b4%e6%96%b0) 后续更新

### [#](https://lbqaq.top/p/init-xposed/#20240915) 2024.09.15

将项目使用了目前流行的Kotlin重构了一波，并添加了UI和模块的配置读取的内容

## [#](https://lbqaq.top/p/init-xposed/#%e5%8f%82%e8%80%83%e6%96%87%e7%8c%ae) 参考文献

* [《安卓逆向这档事》七、Sorry，会Hook真的可以为所欲为-Xposed快速上手(上)模块编.. - 吾爱破解](https://www.52pojie.cn/thread-1740944-1-1.html)
* [Xposed模块开发入门保姆级教程 - 狐言狐语和仙贝的魔法学习记录](https://blog.ketal.icu/cn/Xposed%E6%A8%A1%E5%9D%97%E5%BC%80%E5%8F%91%E5%85%A5%E9%97%A8%E4%BF%9D%E5%A7%86%E7%BA%A7%E6%95%99%E7%A8%8B/)
* [《安卓逆向这档事》四、恭喜你获得广告&弹窗静默卡 - 吾爱破解](https://www.52pojie.cn/thread-1706691-1-1.html)
* [《安卓逆向这档事》八、Sorry，会Hook真的可以为所欲为-Xposed快速上手(下)模块编.. - 吾爱破解](https://www.52pojie.cn/thread-1748081-1-1.html)

[教程](https://lbqaq.top/tags/%E6%95%99%E7%A8%8B/)[Xposed](https://lbqaq.top/tags/xposed/)[安卓](https://lbqaq.top/tags/%E5%AE%89%E5%8D%93/)

Licensed under CC BY-NC-SA 4.0

最后更新于 2023-09-15 15:52:44
