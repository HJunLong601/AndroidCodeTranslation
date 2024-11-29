package com.hjl.translatecode.filter

class LogCodeFilter : ICodeStringFilter {
    override fun filter(string: String): Boolean {

        if (string.contains("Log") || string.contains("log")) {


            if (!string.contains("dialog") && !string.contains("Dialog")) {
                log("ignore line with log :  $string")
                return true
            }

        }

        if (string.contains("TAG")) {
            return true
        }

        return false
    }
}