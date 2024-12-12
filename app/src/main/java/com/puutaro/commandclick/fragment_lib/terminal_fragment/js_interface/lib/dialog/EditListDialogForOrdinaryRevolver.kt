package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.ref.WeakReference

class EditListDialogForOrdinaryRevolver(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
){

    private var editListDialogForSettingList: List<EditListDialogOrdinary> = listOf(
            EditListDialogOrdinary(
                terminalFragmentRef
            ),
            EditListDialogOrdinary(
                terminalFragmentRef
            ),
        )
    private var revolverJob: Job? = null


    fun show(
        fannelInfoCon: String,
        editListConfigPath: String,
    ){
        val showingEditListDialogForSetting = editListDialogForSettingList.first()
        if(
            showingEditListDialogForSetting.isAlreadyShow()
        ) return
        showingEditListDialogForSetting.create(
            fannelInfoCon,
            editListConfigPath,
        )
        revolverJob = CoroutineScope(Dispatchers.IO).launch{
            withContext(Dispatchers.IO){
                delay(1000)
            }
            withContext(Dispatchers.Main){
                editListDialogForSettingList = listOf(
                    EditListDialogOrdinary(
                        terminalFragmentRef
                    ),
                    showingEditListDialogForSetting
                )
            }
        }
    }

    fun getActiveEditListOrdinaryDialog(): Dialog? {
        val activeEditListOrdinaryClass =
            getActiveEditListOrdinalyClass()
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "ldialog.txt").absolutePath,
//            listOf(
//                "activeEditListOrdinaryClass: ${activeEditListOrdinaryClass == null}",
//                "activeEditListOrdinaryClass.isAlreadyShow(): ${activeEditListOrdinaryClass.isAlreadyShow()}",
//                "getActiveEditListOrdinalyClass().editListDialogOrdinary: ${getActiveEditListOrdinalyClass().editListDialogOrdinary == null}",
//            ).joinToString("\n")
//        )
        if(
            !activeEditListOrdinaryClass.isAlreadyShow()
            ) return null
        return getActiveEditListOrdinalyClass().editListDialogOrdinary
    }

    private fun getActiveEditListOrdinalyClass(): EditListDialogOrdinary {
        return editListDialogForSettingList.last()
    }

    fun destroy(
    ){
        revolverJob?.cancel()
        getActiveEditListOrdinalyClass().destroy()
        terminalFragmentRef.get()?.editListDialogForOrdinaryRevolver = null
    }

    suspend fun dismissForRevolver(
    ){
        withContext(Dispatchers.Main) {
            getActiveEditListOrdinalyClass().destroy()
        }
    }
}