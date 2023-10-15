package com.puutaro.commandclick.activity_lib.event.lib.edit

import com.puutaro.commandclick.activity.MainActivity
import com.puutaro.commandclick.activity_lib.event.PageSearchToolbarHandler
import com.puutaro.commandclick.common.variable.variant.PageSearchToolbarButtonVariant
import com.puutaro.commandclick.proccess.EditLongPressType

object ExecOnLongPressPlayOrEditButton {
    fun handle(
        activity: MainActivity,
        editLongPressType: EditLongPressType,
        tag: String?,
        searchText: String,
        pageSearchToolbarButtonVariant: PageSearchToolbarButtonVariant?
    ){
        when(editLongPressType){
            EditLongPressType.PAGE_SEARCH -> {
                if(
                    pageSearchToolbarButtonVariant == null
                ) return
                PageSearchToolbarHandler.handle(
                    activity,
                    pageSearchToolbarButtonVariant,
                    tag,
                    searchText,

                    )
            }
            EditLongPressType.WEB_SEARCH -> {

            }
            else -> {}
        }
    }
}