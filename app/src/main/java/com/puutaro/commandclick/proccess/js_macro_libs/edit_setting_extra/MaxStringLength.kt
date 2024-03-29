package com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra

object MaxStringLength {
    fun cut (
        srcStr: String,
        defaultCutLength: Int,
        cutLength: String?
    ): String {
        if (
            cutLength.isNullOrEmpty()
        ) return srcStr.take(defaultCutLength)
        val maxTakeLength = try {
            cutLength.toInt()
        } catch (e: Exception) {
            defaultCutLength
        }
        return srcStr.take(maxTakeLength)
    }
}