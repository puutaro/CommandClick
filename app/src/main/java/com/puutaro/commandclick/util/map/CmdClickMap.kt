package com.puutaro.commandclick.util.map

import com.puutaro.commandclick.util.CcScript

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