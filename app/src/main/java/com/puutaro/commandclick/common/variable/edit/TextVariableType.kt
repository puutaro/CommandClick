package com.puutaro.commandclick.common.variable.edit

object TypeVariable {
    private val textTypeList = arrayOf(
        EditTextSupportViewName.EDIT_TEXT_EMPHASIS.str,
        EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str,
        EditTextSupportViewName.PASSWORD.str,
    )
    private val textLabelTypeList = arrayOf(
        EditTextSupportViewName.HIDDEN_LABEL.str,
    )
    private val otherLabelTypeList = arrayOf(
//        EditTextSupportViewName.DIRECTORY_PICKER.str,
//        EditTextSupportViewName.FILE_PICKER.str,
        EditTextSupportViewName.COLOR.str,
        EditTextSupportViewName.DATE.str,
        EditTextSupportViewName.TIME.str,
        EditTextSupportViewName.LIST_INDEX.str,
    )
    val textAndLabelList =
        textTypeList +
                textLabelTypeList
    val noIndexTypeList
        = textTypeList +
            textLabelTypeList +
            otherLabelTypeList

    val variableTypeDefineListForMiniEdit = arrayOf(
        EditTextSupportViewName.VARIABLE_LABEL.str,
        EditTextSupportViewName.CHECK_BOX.str,
        EditTextSupportViewName.EDITABLE_CHECK_BOX.str,
        EditTextSupportViewName.EDITABLE_FILE_SELECT_BOX.str,
        EditTextSupportViewName.LIST_CONTENTS_SELECT_BOX.str,
        EditTextSupportViewName.EDITABLE_LIST_CONTENTS_SELECT_BOX.str,
        EditTextSupportViewName.NUM_INDE_CREMENTER.str,
        EditTextSupportViewName.EDIT_TEXT_EMPHASIS.str,
        EditTextSupportViewName.READ_ONLY_EDIT_TEXT.str,
        EditTextSupportViewName.HIDDEN_LABEL.str,
        EditTextSupportViewName.PASSWORD.str,
        EditTextSupportViewName.EDIT_TEXT_PROPERTY.str,
    )
}