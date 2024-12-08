package com.hjl.translatecode.translate

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hjl.translatecode.HttpGet
import java.io.UnsupportedEncodingException
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class BaiduTranslateImpl(private val appid: String, private val securityKey: String) : ITranslateAPI {

    companion object {
        // 语种文档 https://open.fanyigou.com/filecore.html#page11
        private const val TRANS_API_HOST = "http://api.fanyi.baidu.com/api/trans/vip/translate"

        // todo:替换为自己的api
        private const val APP_ID = "xxx"
        private const val SECURITY_KEY = "xxx"
        var instance: BaiduTranslateImpl? = null
            get() {
                if (field == null) field = BaiduTranslateImpl(APP_ID, SECURITY_KEY)
                return field
            }
            private set
    }


    private fun getTransResult(query: String?, from: String, to: String): String? {
        val params = buildParams(query, from, to)
        return HttpGet[TRANS_API_HOST, params]
    }

    private fun buildParams(query: String?, from: String, to: String): Map<String?, String?> {
        val params: MutableMap<String?, String?> = HashMap()
        params["q"] = query
        params["from"] = from
        params["to"] = to
        params["appid"] = appid

        // 随机数
        val salt = System.currentTimeMillis().toString()
        params["salt"] = salt

        // 签名
        val src = appid + query + salt + securityKey // 加密前的原文
        params["sign"] = MD5.md5(src)
        return params
    }

    override fun translateEN(input: String): String {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        val result = getTransResult(input, "auto", "en")
        val json = String(result!!.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
        val transResult = jsonObject.getAsJsonArray("trans_result")[0]
        val resultAsJsonObject = transResult.asJsonObject

//        System.out.println(chinese + "== > " + dst);
        return resultAsJsonObject["dst"].asString
    }

    override fun translateCN(input: String): String {
        try {
            // api限制，直接加个延迟
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        val result = getTransResult(input, "en", "zh")
        val json = String(result!!.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
        val transResult = jsonObject.getAsJsonArray("trans_result")[0]
        val resultAsJsonObject = transResult.asJsonObject

//        System.out.println(chinese + "== > " + dst);
        return resultAsJsonObject["dst"].asString
    }

    override fun translate(input: String?, type: String): String {
        try {
            Thread.sleep(1000)
        } catch (e: InterruptedException) {
            throw RuntimeException(e)
        }
        val result = getTransResult(input, "auto", "type")
        val json = String(result!!.toByteArray(StandardCharsets.UTF_8), StandardCharsets.UTF_8)
        val jsonObject = Gson().fromJson(json, JsonObject::class.java)
        val transResult = jsonObject.getAsJsonArray("trans_result")[0]
        val resultAsJsonObject = transResult.asJsonObject

        return resultAsJsonObject["dst"].asString
    }

    object MD5 {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        private val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
                'e', 'f')

        /**
         * 获得一个字符串的MD5值
         *
         * @param input 输入的字符串
         * @return 输入字符串的MD5值
         */
        fun md5(input: String?): String? {
            return if (input == null) null else try {
                // 拿到一个MD5转换器（如果想要SHA1参数换成”SHA1”）
                val messageDigest = MessageDigest.getInstance("MD5")
                // 输入的字符串转换成字节数组
                val inputByteArray = input.toByteArray(charset("utf-8"))
                // inputByteArray是输入字符串转换得到的字节数组
                messageDigest.update(inputByteArray)
                // 转换并返回结果，也是字节数组，包含16个元素
                val resultByteArray = messageDigest.digest()
                // 字符数组转换成字符串返回
                byteArrayToHex(resultByteArray)
            } catch (e: NoSuchAlgorithmException) {
                null
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException(e)
            }
        }

        private fun byteArrayToHex(byteArray: ByteArray): String {
            // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
            val resultCharArray = CharArray(byteArray.size * 2)
            // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
            var index = 0
            for (b in byteArray) {
                resultCharArray[index++] = hexDigits[b.toInt() ushr 4 and 0xf]
                resultCharArray[index++] = hexDigits[b.toInt() and 0xf]
            }

            // 字符数组组合成字符串返回
            return String(resultCharArray)
        }
    }


}
