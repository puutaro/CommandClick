package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrLogo

object TitleImageAndViewSetter {

    private const val backstackCountSeparator = " "
    fun set(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ) {
        setTitleText(
            editFragment,
        )

        setTitleImage(
            editFragment,
            currentAppDirPath,
            currentScriptFileName
        )
    }

    private fun setTitleText(
        editFragment: EditFragment,
    ){
        val binding = editFragment.binding
        val editTextView = binding.editTextView
        editTextView.text = editFragment.editBoxTitle
    }

    fun makeTitle(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ): String {
        val backstackOrder = makeBackstackCount(
            editFragment
        )
        return listOf(
            "(${backstackOrder})",
            "${UsePath.makeOmitPath(currentAppDirPath)}/${currentScriptFileName}"
        ).joinToString(backstackCountSeparator)
    }
    fun makeTitleForEditTitle(
        editFragment: EditFragment,
        title: String
    ): String {
        val backstackOrder = makeBackstackCount(
            editFragment
        )
        return listOf(
            "(${backstackOrder})",
            title
        ).joinToString(backstackCountSeparator)
    }

    private fun makeBackstackCount(
        editFragment: EditFragment
    ): Int {
        return editFragment
            .activity
            ?.supportFragmentManager
            ?.backStackEntryCount
            ?: 0
    }

    private fun setTitleImage(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ){
        val binding = editFragment.binding
        val editTitleImageView = binding.editTitleImage
        QrLogo(editFragment).setTitleQrLogo(
            editTitleImageView,
            currentAppDirPath,
            currentScriptFileName
        )
    }
}