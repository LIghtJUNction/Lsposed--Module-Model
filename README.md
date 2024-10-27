# Lsposed--Module-Model



### 1.根目录下 settings.gradle.kts 添加源[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_1-%E6%A0%B9%E7%9B%AE%E5%BD%95%E4%B8%8B-settings-gradle-kts-%E6%B7%BB%E5%8A%A0%E6%BA%90)

**java**

```
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://api.xposed.info/") // 添加这一行即可
    }
}
```

### 2. app目录下 ./app/build.gradle.kts 添加jar[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_2-app%E7%9B%AE%E5%BD%95%E4%B8%8B-app-build-gradle-kts-%E6%B7%BB%E5%8A%A0jar)

**java**

```
dependencies {
    compileOnly("de.robv.android.xposed:api:82")
    ...
}
```

### 3.添加作用域 ./app/src/main/res/values 新建资源文件arrays.xml[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_3-%E6%B7%BB%E5%8A%A0%E4%BD%9C%E7%94%A8%E5%9F%9F-app-src-main-res-values-%E6%96%B0%E5%BB%BA%E8%B5%84%E6%BA%90%E6%96%87%E4%BB%B6arrays-xml)

**xml**

```
<resources>
    <string-array name="xposedscope" >
        <!-- 这里填写模块的作用域应用的包名，可以填多个。 -->
        <item>com.asobimo.toramonline</item>
    </string-array>
</resources>
```

### 4.添加声明模块 ./app/src/main/AndroidManifest.xml[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_4-%E6%B7%BB%E5%8A%A0%E5%A3%B0%E6%98%8E%E6%A8%A1%E5%9D%97-app-src-main-androidmanifest-xml)

**xml**

```
<application 
    ...>
    ...

    <!-- 是否为Xposed模块 -->
    <meta-data
        android:name="xposedmodule"
        android:value="true"/>
    <!-- 模块的简介（在框架中显示） -->
    <meta-data
        android:name="xposeddescription"
        android:value="我是Xposed模块简介" />
    <!-- 模块最低支持的Api版本 一般填82即可 -->
    <meta-data
        android:name="xposedminversion"
        android:value="82"/>
    <!-- 模块作用域 -->
    <meta-data
        android:name="xposedscope"
        android:resource="@array/xposedscope"/>
</application>
```

### 5.添加xposed入口[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_5-%E6%B7%BB%E5%8A%A0xposed%E5%85%A5%E5%8F%A3)

./app/src/main下面创建**assets**文件夹并在其中创建**xposed\_init**文件 (没有后缀)
填入下面xposed入口类名 com.kpa.toramhide.MyTest

### 6.添加hook 类[](https://kpa32.github.io/post/Android/Xposed%E6%A8%A1%E5%9D%97%E7%BC%96%E5%86%99#_6-%E6%B7%BB%E5%8A%A0hook-%E7%B1%BB)

**java**

```
package com.kpa.toramhide;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MyTest implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 过滤不必要的应用
        if (!lpparam.packageName.equals("me.kyuubiran.xposedapp")) return;
        // 执行Hook
        hook(lpparam);
    }

    private void hook(XC_LoadPackage.LoadPackageParam lpparam) {
        // 具体流程
    }
}
```
