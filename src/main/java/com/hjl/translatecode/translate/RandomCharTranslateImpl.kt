package com.hjl.translatecode.translate

import kotlin.random.Random

/**
 * 用于生成随机的string key字符串
 */
class RandomCharTranslateImpl : ITranslateAPI {
    override fun translateCN(input: String): String {
        return ""
    }

    override fun translateEN(input: String): String {
        return buildString {
            repeat(5) { append('a' + Random.nextInt(26)) }
            append('_')
            repeat(5) { append('a' + Random.nextInt(26)) }
            append('_')
            repeat(5) { append('a' + Random.nextInt(26)) }
        }
    }

    override fun translate(input: String?, type: String): String {
        return ""
    }

}

fun main() {
    println(RandomCharTranslateImpl().translateEN("测试输入的中文字符串"))
}