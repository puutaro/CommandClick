package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.proccess.qr.QrLogo

object TitleImageAndViewSetter {
    fun set(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ) {
        setTitleText(
            editFragment,
            currentAppDirPath,
            currentScriptFileName
        )

        setTitleImage(
            editFragment,
            currentAppDirPath,
            currentScriptFileName
        )
    }

    fun setTitleText(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ){
        val binding = editFragment.binding
        val editTextView = binding.editTextView
        editTextView.text = makeTitle(
            editFragment,
            currentAppDirPath,
            currentScriptFileName
        )
    }

    fun makeTitle(
        editFragment: EditFragment,
        currentAppDirPath: String,
        currentScriptFileName: String
    ): String {
        val backstackOrder =
            editFragment
                .activity
                ?.supportFragmentManager
                ?.getBackStackEntryCount()
                ?: 0
        val titleText = "(${backstackOrder}) " +
                "${UsePath.makeOmitPath(currentAppDirPath)}/${currentScriptFileName}"
        return titleText
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