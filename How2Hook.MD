# YukiHookAPI 使用指南
.
本文档介绍了 YukiHookAPI 的基本工作方式/常用功能以及一些示例代码,帮助开发者快速上手并使用该 API 进行 Hook 操作.

## 目录
- [YukiHookAPI 使用指南](#yukihookapi-使用指南)
  - [目录](#目录)
  - [用法示例](#用法示例)
  - [结构图解](#结构图解)
  - [代码示例](#代码示例)
  - [Demo](#demo)
  - [Hook APP 示例](#hook-app-示例)
  - [Hook 系统框架](#hook-系统框架)
  - [Hook Resources](#hook-resources)
  - [解除 Hook](#解除-hook)
  - [异常处理](#异常处理)
  - [扩展用法](#扩展用法)
  - [Xposed 模块状态](#xposed-模块状态)
    - [旧版写法](#旧版写法)
    - [Resources Hook (2.0.0 将停止支持)](#resources-hook-200-将停止支持)
  - [Demo](#demo-1)
  - [一个简单的 Hook 例子](#一个简单的-hook-例子)
    - [Hook APP](#hook-app)
    - [Hook onStart 方法](#hook-onstart-方法)
    - [Hook 当前项目下没有的 Class](#hook-当前项目下没有的-class)
    - [Hook Zygote](#hook-zygote)
    - [Hook 系统框架](#hook-系统框架-1)
    - [Hook Resources](#hook-resources-1)
    - [解除 Hook](#解除-hook-1)
    - [异常处理](#异常处理-1)
      - [监听异常](#监听异常)
      - [抛出异常](#抛出异常)
    - [扩展用法](#扩展用法-1)
      - [多个宿主](#多个宿主)
      - [多个进程](#多个进程)
      - [写法优化](#写法优化)
    - [Xposed 模块状态](#xposed-模块状态-1)
      - [判断自身激活状态](#判断自身激活状态)
    - [获取 Hook Framework 信息](#获取-hook-framework-信息)

## 用法示例
介绍了 YukiHookAPI 的基本工作方式以及简单的 Hook 例子和常用功能.

## 结构图解
描述了 YukiHookAPI 的基本工作方式和原理.

## 代码示例
展示了新版和旧版的 Hook 写法,以及 Resources Hook 的示例.

## Demo
提供了宿主 APP 和模块 APP 的 Demo 链接,帮助学习 YukiHookAPI 的使用方法.

## Hook APP 示例
展示了如何 Hook APP 中的方法,例如 Hook `com.android.browser` 中的 `onCreate` 和 `onStart` 方法.

## Hook 系统框架
介绍了如何 Hook 系统框架中的方法,例如获取 `ApplicationInfo` 和 `PackageInfo` 并进行操作.

## Hook Resources
展示了如何 Hook APP 中的资源,例如替换 `com.android.browser` 中的 `app_name` 和 `ic_launcher`。

## 解除 Hook
介绍了如何使用 `XC_MethodHook.Unhook` 功能解除 Hook。

## 异常处理
介绍了如何监听和处理 Hook 过程中发生的异常,避免打断下一个 Hook 流程.

## 扩展用法
展示了多个宿主/多个进程的 Hook 方法,以及代码写法优化的示例.

## Xposed 模块状态
介绍了如何判断 Xposed 模块的激活状态以及获取 Hook Framework 的相关信息.
好的，以下是格式化后的 Markdown 内容：

```markdown
# YukiHookAPI 基本工作方式和常用功能

## 结构图解

下方的结构描述了 YukiHookAPI 的基本工作方式和原理。

```
Host Environment
└─ YukiMemberHookCreator
   └─ Class
      └─ MemberHookCreator
         └─ Member
            ├─ Before
            └─ After
         MemberHookCreator
         └─ Member
            ├─ Before
            └─ After
         ...
   YukiResourcesHookCreator
   └─ Resources
      └─ ResourcesHookCreator
         └─ Drawable
            └─ Replace
         ResourcesHookCreator
         └─ Layout
            └─ Inject
         ...
```

上方的结构换做代码将可写为如下形式。

### 新版写法

```kotlin
TargetClass.method { 
    // Your code here.
}.hook {
    before {
        // Your code here.
    }
    after {
        // Your code here.
    }
}
```

### 旧版写法

```kotlin
TargetClass.hook { 
    injectMember { 
        method { 
            // Your code here.
        }
        beforeHook {
            // Your code here.
        }
        afterHook {
            // Your code here.
        }
    }
}
```

### Resources Hook (2.0.0 将停止支持)

```kotlin
resources().hook {
    injectResource {
        conditions {
            // Your code here.
        }
        replaceTo(...)
    }
}
```

## Demo

你可以在下方找到 API 提供的 Demo 来学习 YukiHookAPI 的使用方法。

- [宿主 APP Demo 点击这里查看](#)
- [模块 APP Demo 点击这里查看](#)

同时安装宿主和模块 Demo，通过激活模块来测试宿主中被 Hook 的功能。

## 一个简单的 Hook 例子

这里给出了 Hook APP、Hook 系统框架与 Hook Resources 等例子，可供参考。

### Hook APP

假设，我们要 Hook `com.android.browser` 中的 `onCreate` 方法并弹出一个对话框。

在 `encase` 方法体中添加代码。

示例如下：

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.method { 
        name = "onCreate"
        param(BundleClass)
        returnType = UnitType
    }.hook {
        after {
            AlertDialog.Builder(instance())
                .setTitle("Hooked")
                .setMessage("I am hook!")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
```

至此，`onCreate` 方法将被成功 Hook 并在 `com.android.browser` 中的每个 Activity 启动时弹出此对话框。

### Hook onStart 方法

我们可以对 `ActivityClass` 使用 Kotlin 的 `apply` 方法创建一个调用空间。

示例如下：

```kotlin
loadApp(name = "com.android.browser") {
    ActivityClass.apply { 
        method { 
            name = "onCreate"
            param(BundleClass)
            returnType = UnitType
        }.hook {
            after {
                AlertDialog.Builder(instance())
                    .setTitle("Hooked")
                    .setMessage("I am hook!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
        method { 
            name = "onStart"
            emptyParam()
            returnType = UnitType
        }.hook {
            after {
                // Your code here.
            }
        }
    }
}
```

### Hook 当前项目下没有的 Class

你可以使用 stub 方式或 `String.toClass(...)` 方法来得到需要 Hook 的类。

示例如下：

```kotlin
"com.example.demo.TestClass".toClass()
    .method {
        // Your code here.
    }.hook {
        // Your code here.
    }
```

若 `com.example.demo` 是你要 Hook 的 APP，那么写法可以更简单。

示例如下：

```kotlin
"$packageName.TestClass".toClass()
    .method {
        // Your code here.
    }.hook {
        // Your code here.
    }
```

若这个 Class 不是马上就能被得到的，你可以使用 `lazyClass(...)` 来定义它。

示例如下：

定义 `TestClass`。

```kotlin
val TestClass by lazyClass("com.example.demo.TestClass")
```

在适当的时候使用它。

```kotlin
TestClass.method {
    // Your code here.
}.hook {
    // Your code here.
}
```

小提示：更多功能请参考 `MemberHookCreator`。

### Hook Zygote

在 APP 启动时，新的进程被 fork 后的第一个事件 `initZygote`。

假设我们要全局 Hook 一个 APP Activity 的 `onCreate` 事件。

在 `encase` 方法体中添加代码。

示例如下：

```kotlin
loadZygote {
    ActivityClass.method { 
        name = "onCreate"
        param(BundleClass)
        returnType = UnitType
    }.hook {
        after {
            // Your code here.
        }
    }
}
```

注意：在 `loadZygote` 中进行的功能十分有限，几乎很少的情况下需要用到 `loadZygote` 方法。

### Hook 系统框架

在 YukiHookAPI 中，Hook 系统框架的实现非常简单。

假设，你要得到 `ApplicationInfo` 与 `PackageInfo` 并对它们进行一些操作。

在 `encase` 方法体中添加代码。

示例如下：

```kotlin
loadSystem {
    ApplicationInfoClass.method {
        // Your code here.
    }.hook {
        // Your code here.
    }
    PackageInfoClass.method {
        // Your code here.
    }.hook {
        // Your code here.
    }
}
```

特别注意：`loadZygote` 与 `loadSystem` 有直接性区别，`loadZygote` 会在 `initZygote` 中装载，系统框架被视为 `loadApp(name = "android")` 而存在，若要 Hook 系统框架，可直接使用 `loadSystem`。

### Hook Resources

注意：此功能将在 2.0.0 版本停止支持并移除。

假设，我们要 Hook `com.android.browser` 中 string 类型的 `app_name` 内容替换为 `123`。

在 `encase` 方法体中添加代码。

示例如下：

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "app_name"
                string()
            }
            replaceTo("123")
        }
    }
}
```

若当前 APP 使用 `app_name` 设置了标题栏文本，则它就会变成我们的 `123`。

你还可以使用当前 Xposed 模块的 Resources 替换 Hook APP 的 Resources。

假设，我们要继续 Hook `com.android.browser` 中 mipmap 类型的 `ic_launcher`。

示例如下：

```kotlin
loadApp(name = "com.android.browser") {
    resources().hook {
        injectResource {
            conditions {
                name = "ic_launcher"
                mipmap()
            }
            replaceToModuleResource(R.mipmap.ic_launcher)
        }
    }
}
```

至此目标 APP 的图标将会被替换为我们设置的图标。

若你想替换系统框架的资源，同样也可以这样实现，只需要把 `loadApp` 换成 `loadZygote` 即可。

示例如下：

```kotlin
loadZygote {
    resources().hook {
        // Your code here.
    }
}
```

小提示：更多功能请参考 `ResourcesHookCreator`。

### 解除 Hook

原生的 Xposed 为我们提供了一个 `XC_MethodHook.Unhook` 功能，可以从 Hook 队列中将当前 Hook 移除，YukiHookAPI 同样可以实现此功能。

第一种方法，保存当前注入对象的 Result 实例，在适当的时候和地方调用 `remove` 即可解除该注入对象。

示例如下：

```kotlin
// 设置一个变量保存当前实例
val hookResult = 
    method { 
        name = "test"
        returnType = UnitType
    }.hook {
        after {
            // ...
        }
    }
// 在适当的时候调用如下方法即可
hookResult.remove()
```

第二种方法，在 Hook 回调方法中调用 `removeSelf` 移除自身。

示例如下：

```kotlin
method { 
    name = "test"
    returnType = UnitType
}.hook {
    after {
        // 直接调用如下方法即可
        removeSelf()
    }
}
```

小提示：更多功能请参考 `MemberHookCreator`。

### 异常处理

YukiHookAPI 重新设计了对异常的监听，任何异常都不会在 Hook 过程中抛出，避免打断下一个 Hook 流程导致 Hook 进程“死掉”。

#### 监听异常

你可以处理 Hook 方法过程发生的异常。

示例如下：

```kotlin
hook {
    // Your code here.
}.result {
    // 处理 Hook 开始时的异常
    onHookingFailure {}
    // 处理 Hook 过程中的异常
    onConductFailure { param, throwable -> }
    // 处理全部异常
    onAllFailure {}
    // ...
}
```

在 Resources Hook 时此方法同样适用。

示例如下：

```kotlin
injectResource {
    // Your code here.
}.result {
    // 处理 Hook 时的任意异常
    onHookingFailure {}
    // ...
}
```

(旧版本适用) 你还可以处理 Hook 的 Class 不存在时发生的异常。

示例如下：

```kotlin
TargetClass.hook {
    injectMember {
        // Your code here.
    }
}.onHookClassNotFoundFailure {
    // Your code here.
}
```

你还可以处理查找方法时的异常。

示例如下：

```kotlin
method {
    // Your code here.
}.onNoSuchMethod {
    // Your code here.
}
```

小提示：更多功能请参考 `MemberHookCreator.Result`、`ResourcesHookCreator.Result`。

这里介绍了可能发生的常见异常，若要了解更多请参考 API 异常处理。

#### 抛出异常

在某些情况下，你可以手动抛出异常来达到提醒某些功能存在问题的目的。

上面已经介绍过，在 hook 方法体内抛出的异常会被 YukiHookAPI 接管，避免打断下一个 Hook 流程导致 Hook 进程“死掉”。

以下是 YukiHookAPI 接管时这些异常的运作方式。

示例如下：

```kotlin
// <情景1>
injectMember {
    method {
        throw RuntimeException("Exception Test")
    }
    afterHook {
        // ...
    }
}.result {
    // 能够捕获到 RuntimeException
    onHookingFailure {}
}
// <情景2>
injectMember {
    method {
        // ...
    }
    afterHook {
        throw RuntimeException("Exception Test")
    }
}.result {
    // 能够捕获到 RuntimeException
    onConductFailure { param, throwable -> }
}
```

以上情景只会在 (Xposed) 宿主环境被处理，不会对宿主自身造成任何影响。

若我们想将这些异常直接抛给宿主，原生的 Xposed 为我们提供了 `param.throwable` 方法，YukiHookAPI 同样可以实现此功能。

若想在 Hook 回调方法体中将一个异常直接抛给宿主，可以有如下实现方法。

示例如下：

```kotlin
method {
    // ...
}.hook {
    after {
        RuntimeException("Exception Test").throwToApp()
    }
}
```

你也可以直接在 Hook 回调方法体中抛出异常，然后标识将异常抛给宿主。

示例如下：

```kotlin
method {
    // ...
}.hook {
    after {
        throw RuntimeException("Exception Test")
    }.onFailureThrowToApp()
}
```

以上两种方法均可在宿主接收到异常从而使宿主进程崩溃。

注意：为了保证 Hook 调用域与宿主内调用域相互隔离，异常只有在 `before` 与 `after` 回调方法体中才能抛给宿主。

小提示：更多功能请参考 `Throwable.throwToApp`、`YukiMemberHookCreator.MemberMookCreator.HookCallback`。

### 扩展用法

你可以在 Hook 过程中使用下面的方法方便地实现各种判断和功能。

#### 多个宿主

如果你的模块需要同时处理多个 APP 的 Hook 事件，你可以使用 `loadApp` 方法体来区分你要 Hook 的 APP。

示例如下：

```kotlin
loadApp(name = "com.android.browser") {
    // Your code here.
}
loadApp(name = "com.android.phone") {
    // Your code here.
}
```

小提示：更多功能请参考 `PackageParam.loadApp`。

#### 多个进程

如果你 Hook 的宿主 APP 有多个进程，你可以使用 `withProcess` 方法体来对它们分别进行 Hook。

示例如下：

```kotlin
withProcess(mainProcessName) {
    // Your code here.
}
withProcess(name = "$packageName:tool") {
    // Your code here.
}
```

小提示：更多功能请参考 `PackageParam.withProcess`。

#### 写法优化

为了使代码更加简洁，你可以删去 YukiHookAPI 的名称，将你的 `onHook` 入口写作 lambda 形式。

示例如下：

```kotlin
override fun onHook() = encase {
    // Your code here.
}
```

你还可以在仅需要一个 Hook 回调事件的时候简写 `hook { ... }` 方法体。

示例如下：

```kotlin
ActvityClass.method {
    // Your code here.
}.hook().after {
    // Your code here.
}
```

### Xposed 模块状态

通常情况下，Xposed 模块的开发者都会去选择读取当前 Xposed 模块的激活信息以更好地向用户展示当前功能的生效状态。

除了基本的 Hook 功能，YukiHookAPI 还为开发者设计了一套 Xposed 模块状态判断的功能，如激活状态、Hook Framework 信息。

#### 判断自身激活状态

通常情况下，我们会选择写一个方法，使其返回 `false`，然后 Hook 掉这个方法使其返回 `true` 来证明 Hook 已经生效。

在 YukiHookAPI 中你完全不需要再这么做了，YukiHookAPI 已经帮你封装好了这个操作，你可以直接进行使用。

现在，你可以直接使用 `YukiHookAPI.Status.isXposedModuleActive` 在模块中判断自身是否被激活。

示例如下：

```kotlin
if (YukiHookAPI.Status.isXposedModuleActive) {
    // Your code here.
}
```

由于一些特殊原因，在太极、无极中的模块无法使用标准方法检测激活状态。

此时你可以使用 `YukiHookAPI.Status.isTaiChiModuleActive` 判断自身是否被激活。

示例如下：

```kotlin
if (YukiHookAPI.Status.isTaiChiModuleActive) {
    // Your code here.
}
```

若你想使用两者得兼的判断方案，YukiHookAPI 同样为你封装了便捷的方式。

此时你可以使用 `YukiHookAPI.Status.isModuleActive` 判断自身是否在 Xposed 或太极、无极中被激活。

示例如下：

```kotlin
if (YukiHookAPI.Status.isModuleActive) {
    // Your code here.
}
```

小提示：更多功能请参考 `YukiHookAPI.Status`。

注意：如果你的模块 API 版本高于 29 且正在目标 API 为 29 以上的系统中运行，你需要在 `AndroidManifest.xml` 中添加如下权限声明才能正常判断模块在太极、无极中的激活状态。

示例如下：

```xml
<queries>
    <intent>
        <action android:name="android.intent.action.MAIN" />
    </intent>
</queries>
```

还有一种方案，你可以直接声明 `android.permission.QUERY_ALL_PACKAGES` 权限，但是不推荐且会被代码检查警告。

示例如下：

```xml
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
```

若模块激活判断中包含太极、无极中的激活状态，就必须将模块的 Application 继承于 `ModuleApplication` 或直接使用 `ModuleApplication`。

### 获取 Hook Framework 信息

除了判断自身激活状态之外，你还可以通过 `YukiHookAPI.Status` 中的 `Executor` 来获取当前 Hook Framework 的相关信息。

例如我们可以使用 `YukiHookAPI.Status.Executor.name` 来获取当前 Hook Framework 的名称。

示例如下：

```kotlin
val frameworkName = YukiHookAPI.Status.Executor.name
```

我们还可以使用 `YukiHookAPI.Status.Executor.apiLevel` 来获取当前 Hook Framework 的 API Level。

示例如下：

```kotlin
val frameworkApiLevel = YukiHookAPI.Status.Executor.apiLevel
```

小提示：更多功能请参考 `YukiHookAPI.Status.Executor`。

注意：1.0.91 版本后的 YukiHookAPI 修改了获取 Xposed 模块状态的逻辑判断方式，现在你可以在模块与 Hook APP (宿主) 中同时使用此 API；

需要确保 `InjectYukiHookWithXposed.isUsingXposedModuleStatus` 是启用状态；

YukiHookAPI 仅对已知的获取方式进行了对接，除了提供标准 API 的 Hook Framework 之外，其它情况下模块可能都将无法判断自己是否被激活或是获取 Hook Framework 的相关信息。
```