package com.hjl.translatecode

import com.hjl.translatecode.preset.XMLKeyParser
import com.hjl.translatecode.processor.*


/**
 * 安卓原生工程 硬编码中文抽取脚本
 * [x] java/kotlin代码
 * [x] xml 布局文件
 * [x] kotlin模版字符串
 * [x] 支持多个、多种预设映射表，支持拓展
 * [x] 自动生成xmlkey值、防重复策略
 * [x] 自动替换引用
 * [x] 自动导包
 * [x] 生成新增的strings文件内容
 */

object Main {


//    todo : 1. 功能：扫描模块下面的布局文件，Java、Kotlin文件，生成对应的string.xml的key，并对代码里面的硬编码进行直接替换
//    todo : 2. 初始化配置 ，设置 需要检测的模块路径，检测 代码与xml文件
//    todo : 3. 替换 InitProcessor 的 翻译api实现
//    todo:  4. 替换TransCodeUtils 里面生成的模版字符串及import
//    todo:  5. 根据项目具体情况新增/删除代码过滤
//    todo:  6. 脚本会根据设置的字符串模版直接替换所有的代码文件，并生成对应的新增strings.xml文件到项目根目录，复制到粘贴板
//    todo:  7. 其他语言翻译可根据需要自行参考 GenerateEnglishProcessor 实现，或者直接贴到网页进行翻译减少api调用


    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    // 解析预设key值 -> 扫描文件的里面的中文字符（过滤日志等） -> 翻译成英文，生成string的key值 -> 解析是否为模板字符串
    // -> 根据匹配的字符串替换为对应的模版文件 -> 自动导包 -> 替换文件内容 -> 所有文件遍历结束后生成新增的xml文件
    @JvmStatic
    fun main(args: Array<String>) {

        // todo: 初始化配置

        val modulePath = "E:\\AndroidProject\\LWanAndroid\\commonlib"
        // 预设的 中文 - key 映射表
        val keyMap = LinkedHashMap<String, String>()
        val presetKeyMap = LinkedHashMap<String, String>()

        val config = TranslateConfig(presetKeyMap, keyMap, modulePath, { xmlKey: String? ->
            return@TranslateConfig "BaseApplication.getApplication().getString(R.string.$xmlKey)"
        },
                importArray = arrayOf("import com.hjl.commonlib.base.BaseApplication", "import com.hjl.commonlib.R")
        )

        // todo: 初始化配置 end

        // todo: 配置流程 记得配置 TransCodeUtils 生成的字符串模版和 import 模版

        // 需要过滤的文件名 比如test.kt
        val filterFileName: List<String> = emptyList()

        val processors = arrayListOf(
                InitProcessor(), // todo:   必须 初始化配置 ，在里面设置翻译实例实现
                PresetKeyProcessor(arrayOf(XMLKeyParser("$modulePath/src/main/res/values/strings.xml"))), // 可选 预设的key值
                XMLFileProcessor(), // 可选 转换 xml 代码
                CodeFileProcessor(filterFileName), // 可选 转换 kotlin、java代码
                ResultOutputProcessor(), // 必须 结果输出，可自行修改为json等格式
//                GenerateEnglishProcessor() // 可选，输出英文XML
        )

        processors.forEach {
            it.process(config)
        }


    }

}
