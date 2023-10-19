package com.puutaro.commandclick.util

object CmdClickMap {
     fun createMap(
        mapEntryStr: String,
        separator: String
    ):List<Pair<String, String>> {
        return mapEntryStr.split(separator).map {
            CcScript.makeKeyValuePairFromSeparatedString(
                it,
                "="
            )
        }
    }

}