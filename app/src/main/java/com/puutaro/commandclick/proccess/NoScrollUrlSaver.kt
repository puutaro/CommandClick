package com.puutaro.commandclick.proccess

import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment.CommandIndexFragment

object NoScrollUrlSaver {
    fun save(
        fragment: androidx.fragment.app.Fragment,
        currentAppDirPath: String,
        fannelName: String,
    ){
        val context = fragment.context
        when(fragment){
            is CommandIndexFragment -> {
                val listener = context as? CommandIndexFragment.OnUpdateNoSaveUrlPathsListener
                listener?.onUpdateNoSaveUrlPaths(
                    currentAppDirPath,
                    fannelName,
                )
            }
            is EditFragment -> {
                val listener = context as? EditFragment.OnUpdateNoSaveUrlPathsListenerForEdit
                listener?.onUpdateNoSaveUrlPathsForEdit(
                    currentAppDirPath,
                    fannelName,
                )
            }
        }

    }
}