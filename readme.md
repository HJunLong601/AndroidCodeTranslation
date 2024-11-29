### Android 原生工程 中文硬编码抽取

将工程里面的中文硬编码抽取到string

#### 功能支持

- [x] java/kotlin代码
- [x] xml 布局文件
- [x] kotlin模版字符串
- [x] 自动生成xmlkey值、防重复策略
- [x] 自动替换引用
- [x] 自动导包
- [x] 生成新增的strings文件内容 拷贝到粘贴板

#### 使用步骤

- 使用步骤

1. 配置检测的类型，代码、XML文件，配置是否未仅扫描

```kotlin
    // Main.kt 文件
val checkCode = true // 扫描 java、kotlin文件
val checkXML = true // 扫描替换layout文件
TransCodeUtils.scanOnly = false
```

2. 配置模块路径

```kotlin
        // todo:替换为模块路径
val modulePath = "E:\\AndroidProject\\LWanAndroid\\commonlib"
```

3. 配置预设的中文 - key 映射表

```kotlin
        // 预设的中文 - key映射，默认只读取 strings文件
initPreSetKey(
        arrayOf(XMLKeyParser("$modulePath/src/main/res/values/strings.xml")),
        presetKeyMap)
```

4. 替换百度翻译api的appid和key，或者自行实现翻译功能，默认提供百度、随机key生成，参考```ITranslateAPI```和子类
5. 配置自动导包的内容，根据项目自行实现

```kotlin
// TransCodeUtils.kt
// todo: 根据项目 返回对应的生成的字符串模版
private fun getGenerateStringTemplate(xmlKey: String?): String {
    return "BaseApplication.getApplication().getString(R.string.$xmlKey)"
}

// todo: 根据项目 返回对应自动导入的包
fun getImportString(): Array<String> {
    return arrayOf("import com.hjl.commonlib.base.BaseApplication", "import com.hjl.commonlib.R")
}

```

5. 配置需要过滤的代码文件，默认自带log等，具体参考```ICodeStringFilter```和子类实现

6. 运行Main.kt文件，根据项目需要自行实现其他类型的翻译

#### 流程示意

![](process.jpg) 
