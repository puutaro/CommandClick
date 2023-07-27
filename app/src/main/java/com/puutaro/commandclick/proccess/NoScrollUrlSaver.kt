package com.puutaro.commandclick.proccess

import androidx.fragment.app.Fragment
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment.EditFragment

object NoScrollUrlSaver {
    fun save(
        fragment: Fragment,
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