package com.puutaro.commandclick.proccess.edit_list

import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent

object ParentReplace {
    fun makeReplaceParentInt(
        positionEntryStr: String?,
        tagIdMap: Map<String, Int>?,
    ): Int? {
        if(
            positionEntryStr.isNullOrEmpty()
        ) return null
        val unsetEnum = EditComponent.Template
            .ConstraintManager
            .ConstraintParameter.UNSET
        if(
            positionEntryStr == unsetEnum.str
        ) return unsetEnum.int
        val parentIdEnum = EditComponent.Template
            .ConstraintManager
            .ConstraintParameter.PARENT_ID
        if(
            positionEntryStr == parentIdEnum.str
        ) return parentIdEnum.int
        return tagIdMap?.get(positionEntryStr)
    }
}