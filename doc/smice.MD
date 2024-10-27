### 第一步：配置build.gradle

在app目录下的build.gradle的dependencies里加入：

```gradle
compileOnly 'de.robv.android.xposed:api:82'
compileOnly 'de.robv.android.xposed:api:82:sources'
```

其实最新的api是89，但89是测试版本，用的最多的还是82版本。目前lsposed团队宣布开发新的api：libxposed，期待一波。

### 第二步：配置settings.gradle

在settings.gradle的repositories里加上：

```gradle
maven { url 'https://maven.aliyun.com/repository/public/' }
```

### 第三步：修改AndroidManifest.xml

在AndroidManifest.xml的application标签内加上：

```xml
    <meta-data
        android:name="xposedmodule"
        android:value="true" />
    <meta-data
        android:name="xposeddescription"
        android:value="这里是模块描述" />
    <meta-data
        android:name="xposedminversion"
        android:value="89" />
    <meta-data
        android:name="xposedscope"
        android:resource="@array/scope" />
```

其中xposedmodule属性声明为true时声明是xposed模块，才能被lsposed及其衍生框架识别，xposedminversion为最低兼容的xposed框架api版本，xposedscope为模块作用域列表，填写后方便用户勾选作用域。

### 第四步：新建xposed\_init

在src路径下创建assets目录，在该目录下新建xposed\_init文件，以文本方式打开填入完整的hook类类名（包名+类名）。

### 第五步：编写hook类

新建hook类时注意要与xposed\_init类中所填写的完整类名一致，否则xposed及其衍生框架找不到该hook类。

### 第六步：编写lsposed作用域列表

在res/values路径下新建array.xml，内容如下：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="scope">
        <item>com.test.app</item>
    </string-array>
</resources>
```

其中item里填写要hook的目标应用的包名即可。
