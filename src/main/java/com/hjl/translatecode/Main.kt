package com.hjl.translatecode

import com.hjl.translatecode.preset.IPresetKeyParser
import com.hjl.translatecode.preset.XMLKeyParser
import java.io.File
import java.io.FileWriter


/**
 * 安卓原生工程 硬编码中文抽取脚本
 * [x] java/kotlin代码
 * [x] xml 布局文件
 * [x] kotlin模版字符串
 * [x] 自动生成xmlkey值、防重复策略
 * [x] 自动替换引用
 * [x] 自动导包
 * [x] 生成新增的strings文件内容
 */

object Main {


    val checkCode = true // 扫描 java、kotlin文件
    val checkXML = true // 扫描替换layout文件

//    todo : 1. 功能：扫描模块下面的布局文件，Java、Kotlin文件，生成对应的string.xml的key，并对代码里面的硬编码进行直接替换
//    todo : 2. 替换 需要检测的模块路径，配置 检测 代码与xml文件
//    todo : 3. 替换 翻译api 的appid和key 或者自行实现翻译
//    todo:  4. 替换TransCodeUtils 里面生成的模版字符串及import
//    todo:  5. 根据项目具体情况新增/删除代码过滤
//    todo:  6. 脚本会根据设置的字符串模版直接替换所有的代码文件，并生成对应的新增strings.xml文件到项目根目录，复制到粘贴板
//    todo:  7. 其他语言翻译可根据需要自行参考实现，或者直接贴到网页进行翻译减少api调用


    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    // 解析预设key值 -> 扫描文件的里面的中文字符（过滤日志等） -> 翻译成英文，生成string的key值 -> 解析是否为模板字符串
    // -> 根据匹配的字符串替换为对应的模版文件 -> 自动导包 -> 替换文件内容 -> 所有文件遍历结束后生成新增的xml文件
    @JvmStatic
    fun main(args: Array<String>) {

        // todo: 只扫描 文件 不做替换和string的key生成，用于简单过一遍模块，减少不必要的api调用
        TransCodeUtils.scanOnly = false


        // todo:替换为模块路径
        val modulePath = "E:\\AndroidProject\\LWanAndroid\\commonlib"
        // 预设的 中文 - key 映射表
        val keyMap = LinkedHashMap<String, String>()
        val presetKeyMap = LinkedHashMap<String, String>()

        initPreSetKey(arrayOf(XMLKeyParser("$modulePath/src/main/res/values/strings.xml")), presetKeyMap)

        println("preset map : ${presetKeyMap.size}")

        keyMap.putAll(presetKeyMap)
        checkModule(modulePath, checkCode, checkXML, keyMap)


        println("====================  findXMLKey ${keyMap.size} =======================")

        val resultXMLSb = StringBuilder()

        // 过滤掉已经存在的key
        presetKeyMap.keys.forEach {
            keyMap.remove(it)
        }

        println("after filter  ${keyMap.size}")

        keyMap.keys.forEach {

            if (presetKeyMap.containsKey(it)) return@forEach

            val generateXMLItem = TransCodeUtils.generateXMLItem(keyMap[it]!!, it)
            resultXMLSb.append(generateXMLItem).append("\n")
        }

        // 输出生成的string item 并复制到粘贴板
        // 结果输出到模块目录
        println(resultXMLSb)
        TransCodeUtils.copyToClipboard(resultXMLSb.toString())
        File("$modulePath/transResult.txt").apply {
            println("write result:${this.absolutePath}")
            createNewFile()
            FileWriter(this).apply {
                write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                        "<resources>\n")
                write(resultXMLSb.toString())
                write("\n" +
                        "</resources>")
                close()
            }
        }

        println("=============================================")


        println("scan result : ${TransCodeUtils.scanList.size}")
        println("scan result : ${TransCodeUtils.scanList}")

        println("==============================================")

//        // 生成英文
//        val englishStringBuilder = StringBuilder()
//        handler.map.keys.forEach {
//            val translateEN = ITranslateAPI.instance.translateEN(it)
//            val englishItem = TransCodeUtils.generateXMLItem(handler.map[it]!!,translateEN )
//            englishStringBuilder.append(englishItem).append("\n")
//        }
//
//        println("English item :\n $englishStringBuilder")


    }

    private fun checkModule(modulePath: String, checkCode: Boolean, checkXML: Boolean, xmlKeyMap: LinkedHashMap<String, String>) {
        val dir = File("$modulePath/src/main/java")

        val xmlDir = File("$modulePath/src/main/res/layout")

        if (checkXML && xmlDir.exists()) {
            // check xml
            xmlDir.listFiles()!!.filter { it.name.endsWith("xml") }.forEach {
                println("check xml file ${it.absolutePath}")
                val data = it.readText()
                val result = TransCodeUtils.translateOriginXML(data, xmlKeyMap)

                FileWriter(it).apply {
                    write(result)
                    close()
                }
            }
        }

        val filterFile = arrayListOf("")

        if (checkCode) {
            // check code
            TransCodeUtils.traverseFolder(dir) {
                val isJava = it.name.endsWith("java")
                println("check code file ${it.absolutePath} is java :$isJava")

                if (it.name in filterFile) {
                    println("skip file ${it.absolutePath}")
                    return@traverseFolder
                }

                if (!isJava && !it.name.endsWith("kt")) {
                    println("skip file ${it.absolutePath}")
                    return@traverseFolder
                }


                val data = it.readText()

                val result = TransCodeUtils.transOriginJavaCode(data, xmlKeyMap, isJava)

                FileWriter(it).apply {
                    write(result)
                    close()
                }
            }
        }
    }

    /**
     * 解析预设的key值
     */
    private fun initPreSetKey(keyParsers: Array<IPresetKeyParser>, map: MutableMap<String, String>) {

        keyParsers.forEach {
            it.parseKey(map)
        }

    }

}
