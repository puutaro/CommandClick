package com.puutaro.commandclick.proccess.edit.image_action

object ImageActionKeyManager {
    val landSeparator = ','
    val mainKeySeparator = '|'
    val subKeySepartor = '?'
    val valueSeparator = '&'

    enum class ImageActionsKey(
        val key: String
    ) {
        IMAGE_VAR("iVar"),
        IMAGE_ACTION_VAR("iAcVar"),
        IMAGE_RETURN("iReturn"),
    }

    enum class VarPrefix(
        val prefix: String
    ) {
        RUN("run"),
        RUN_ASYNC("${RUN.prefix}Async"),
        ASYNC("async"),
    }

    object BitmapVar {


        const val itPronoun = "it"

        fun matchBitmapVarName(
            bitmapVarName: String,
        ): Boolean {
            val bitmapVarRegex = Regex("^#[{][a-zA-Z0-9_]+[}]$")
            return bitmapVarRegex.matches(bitmapVarName)
                    && !bitmapVarName.startsWith(VarPrefix.RUN.prefix)
        }

        fun convertBitmapKey(bitmapVar: String): String {
            return bitmapVar
                .removePrefix("#{")
                .removeSuffix("}")
        }
    }

    object AwaitManager {
        private const val awaitSeparator = ','

        fun getAwaitVarNameList(awaitVarNameListCon: String): List<String> {
            return awaitVarNameListCon.split(awaitSeparator).map {
                it.trim()
            }.filter {
                it.isNotEmpty()
            }
        }
    }

    private enum class CommonPathKey(
        val key: String
    ) {
        IMPORT_PATH("importPath"),
    }

    enum class ImageSubKey(
        val key: String
    ) {
        IMAGE_VAR(ImageActionsKey.IMAGE_VAR.key),
        IMAGE_RETURN(ImageActionsKey.IMAGE_RETURN.key),
        FUNC("func"),
        ARGS("args"),
        ON_RETURN("onReturn"),
        I_IF("iIf"),
//        VALUE("value"),
        AWAIT("await"),
    }

    object ActionImportManager {

        enum class ActionImportKey(
            val key: String,
        ) {
            IMPORT_PATH(CommonPathKey.IMPORT_PATH.key),
            REPLACE("replace"),
            I_IF("iIf"),
            ARGS(ImageSubKey.ARGS.key),
            AWAIT("await"),

        }
    }

    object ImageReturnManager {

        enum class OutputReturn {
            OUTPUT_RETURN
        }

        enum class ImageReturnKey(
            val key: String,
        ) {
            I_IF(ImageSubKey.I_IF.key),
            ARGS(ImageSubKey.ARGS.key),
        }
    }

    enum class BreakSignal {
        EXIT_SIGNAL,
        RETURN_SIGNAL,
    }
}