package com.hjl.translatecode

import com.hjl.translatecode.filter.LogCodeFilter
import com.hjl.translatecode.filter.NoteCodeFilter
import com.hjl.translatecode.translate.ITranslateAPI
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File


object TransCodeUtils {

    var outputLog = true
    var scanOnly = true
    val scanList = HashSet<String>()

    // 代码匹配过滤器
    val codeFilter = listOf(NoteCodeFilter(), LogCodeFilter())

    // todo: 根据项目 返回对应的生成的字符串模版
    private fun getGenerateStringTemplate(xmlKey: String?): String {
        return "BaseApplication.getApplication().getString(R.string.$xmlKey)"
    }

    // todo: 根据项目 返回对应自动导入的包
    fun getImportString(): Array<String> {
        return arrayOf("import com.hjl.commonlib.base.BaseApplication", "import com.hjl.commonlib.R")
    }

    fun transOriginJavaCode(data: String, dataMap: MutableMap<String, String>, isJava: Boolean): String {
        return transOriginJavaCode(data, getStringInLine(data, isJava), dataMap, isJava)
    }


    fun transOriginJavaCode(data: String, chineseInLine: Set<String>, keyMap: MutableMap<String, String>, isJava: Boolean): String {
        if (chineseInLine.isEmpty()) return data

        // 所有的中文字符
        println(chineseInLine)
        scanList.addAll(chineseInLine)
        if (scanOnly) return data

        val transApi = ITranslateAPI.instance

        // get xml key
        println("start trans key ")
        for (string in chineseInLine) {
            if (keyMap.containsKey(string)) continue

            if (string.trim() == "%s") continue

            val (formatStr, params) = convertToStringFormat(string)

            if (!isJava && params.isNotEmpty()) {
                val en = transApi.translateEN(formatStr)
                val result = generateStringItemKey(en, keyMap.values)

                print("   $formatStr -> $en    ,  ")
                keyMap[formatStr] = result
            } else {
                val en = transApi.translateEN(string)
                val result = generateStringItemKey(en, keyMap.values)

                print("   $string -> $en    ,  ")
                keyMap[string] = result
            }


        }
        println("end trans key ")

        println("build xml key end,result : ${keyMap.size}")


        var result = data
        for (string in chineseInLine) {

            val (formatStr, params) = convertToStringFormat(string)

            if (!isJava && params.isNotEmpty()) {
                val xmlKey = keyMap[formatStr]
                if (xmlKey.isNullOrEmpty()) {
                    println("error ======>  $formatStr ")
                }

                val newValue = StringBuilder("String.format(").append(getGenerateStringTemplate(xmlKey))
                params.forEach {
                    newValue.append(",$it")
                }
                newValue.append(")")

                result = result.replace("\"" + string + "\"", newValue.toString())
                println("replace $string with kotlin template")
            } else {
                val xmlKey = keyMap[string]!!
                result = result.replace("\"" + string + "\"", getGenerateStringTemplate(xmlKey))
                println("replace $string")
            }

        }

        // 自动导包
        val firstLine = result.split("\n")[0]
        var appendImport = firstLine
        getImportString().forEach {

            if (!result.contains(it)) {
                appendImport += if (isJava) {
                    "\n$it;"
                } else {
                    "\n$it"
                }
            }

        }

        result = result.replace(firstLine, appendImport)

        return result
    }

    /**
     * 转换xml文本
     */
    fun translateOriginXML(data: String, xmlKeyMap: MutableMap<String, String>): String {

        val chineseInLine = getStringInLine(data, true)

        if (chineseInLine.isEmpty()) return data

        println(chineseInLine)

        scanList.addAll(chineseInLine)
        if (scanOnly) return data

        val transApi = ITranslateAPI.instance

        // get xml key
        println("start trans key ")
        for (key in chineseInLine) {
            if (xmlKeyMap.containsKey(key)) continue

            val en = transApi.translateEN(key)
            val result = generateStringItemKey(en, xmlKeyMap.values)

            print("  $key -> $en   , ")
            xmlKeyMap[key] = result
        }

        println("")

        var result = data
        for (key in chineseInLine) {
            result = result.replace("\"" + key + "\"", "\"@string/${xmlKeyMap[key]}\"")
            println("relace $key")
        }
//        println(result)

        return result
    }

    /**
     * 正则匹配字符串 包括kotlin的模版字符串
     *
     */
    fun convertToStringFormat(template: String): Pair<String, List<String>> {
        val params = mutableListOf<String>()
        // 修改正则表达式以匹配两种情况：
        // 1. ${xxx} 形式
        // 2. $xxx 形式（不带大括号）
        val pattern = Regex("""\$\{([^}]+)}|\$([a-zA-Z][a-zA-Z0-9.]*)""")

        val formatted = pattern.replace(template) { matchResult ->
            // groupValues[1] 匹配 ${xxx} 中的内容
            // groupValues[2] 匹配 $xxx 中的内容
            val param = (matchResult.groupValues[1].takeIf { it.isNotEmpty() }
                    ?: matchResult.groupValues[2]).trim()
            params.add(param)
            "%s"
        }

        return Pair(formatted, params)
    }

    /**
     * 获取展示的中文字符，排除日志等
     */
    fun getStringInLine(data: String, isJava: Boolean): HashSet<String> {

        val result = HashSet<String>()

        data.split("\n").forEach {


            codeFilter.firstOrNull { codeFilter ->
                codeFilter.filter(it)
            }?.let {
                return@forEach
            }

            // 定义正则表达式
            val doubleQuotedRegex = "\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*[\\u4e00-\\u9fff][^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"".toRegex()
            // 查找所有双引号字符串
            val doubleQuotedStrings = doubleQuotedRegex.findAll(it).map { it.value }.map { it.replace("\"", "") }
            result.addAll(doubleQuotedStrings)
//            result.addAll(doubleQuotedStrings.filter { !it.contains("$") })
        }

        return result
    }

    fun generateXMLItem(key: String, value: String) = "<string name=\"${key}\">${value}</string>"

    fun generateStringItemKey(name: String, existsKeys: Collection<String>): String {

        var result = name.replace("please", "pls")
                .replace(" ", "_")
                .replace("width", "w")
                .replace("height", "h")
                .replace("address", "addr")
                .replace("received", "rec")
                .replace("information", "info")
                .lowercase()

        result = extractChars(result)

        if (result.length >= 20) {
            result = generateAbbr(result)
        }


        if (result.length > 20) {
            result = result.substring(0, 19)
        }

        // colon
        if (name.endsWith("：") || name.endsWith(":")) {
            result = result.substring(0, result.length - 2) + "_cln"
        }

        // bracket
        if (name.contains("]") || name.contains("】")) {
            result += "_brckt"
        }

        // 存在相同的key则后面追加一个字符
        if (existsKeys.contains(result)) {
            ('a'..'z').forEach { c ->
                val newKey = "${result}_$c"
                if (!existsKeys.contains(newKey)) return newKey
            }
        }

        return result
    }

    /**
     * 根据英文翻译生成 string的key
     */
    fun generateAbbr(string: String): String {
        // 提取所有大写字母
        val upperChars = string.filter { it.isUpperCase() }

        // 提取所有非元音字母
        val nonVowelChars = string.filter { it !in "AEIOUaeiou" }

        // 拼接两个字符串
        val abbr = upperChars + nonVowelChars

        return abbr.toLowerCase()
    }

    fun extractChars(string: String): String {
        // 提取所有英文字符
        val englishChars = string.filter { it in 'A'..'Z' || it in 'a'..'z' || it == '_' }

        return englishChars
    }


    fun copyToClipboard(content: String?) {
        val selection = StringSelection(content)
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null)
    }

    fun traverseFolder(directory: File, action: (file: File) -> Unit) {
        // 确保是文件夹
        if (!directory.isDirectory) return

        // 获取文件夹中的所有文件和子文件夹
        val files = directory.listFiles() ?: return

        for (file in files) {
            if (file.isDirectory) {
                // 如果是文件夹，递归调用
                traverseFolder(file, action)
            } else {
                // 如果是文件，进行处理
                action.invoke(file)
            }
        }
    }

}