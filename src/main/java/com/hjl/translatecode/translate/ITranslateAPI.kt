package com.hjl.translatecode.translate

interface ITranslateAPI {


    companion object {

        lateinit var instance: ITranslateAPI

    }

    fun translateCN(input: String): String

    fun translateEN(input: String): String

    fun translate(input: String?, type: String): String

}