package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.R
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.FannelListVariable
import com.puutaro.commandclick.common.variable.LoggerTag
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.service.GitCloneService
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*


class InstallFromFannelRepo(
    private val cmdIndexFragment: CommandIndexFragment,
    private val currentAppDirPath: String,
    private val cmdListAdapter: ArrayAdapter<String>,
) {

    private val context = cmdIndexFragment.context
    private val binding = cmdIndexFragment.binding
    private val cmdListView = binding.cmdList
    private val searchTextLinearWeight = SearchTextLinearWeight.calculate(cmdIndexFragment)
    private val listLinearWeight = 1F - searchTextLinearWeight
    private val terminalViewModel: TerminalViewModel by cmdIndexFragment.activityViewModels()
    private val cmdclickFannelListSeparator = FannelListVariable.cmdclickFannelListSeparator
    private val blankListMark = "let's sync by long click"
    private val fannelDirSuffix = UsePath.fannelDirSuffix
    private var fannelListUpdateJob: Job? = null
    private val fannelListAdapter = ArrayAdapter(
        (context as Context),
        R.layout.simple_list_item_1,
        makeFannelListForListView()
    )

    fun install(){
        if(context == null) return
        FileSystems.createDirs(UsePath.cmdclickFannelListDirPath)
        val linearLayoutForTotal = LinearLayoutForTotal.make(
            context
        )
        val linearLayoutForListView = NestLinearLayout.make(
            context,
            listLinearWeight
        )
        val linearLayoutForSearch = NestLinearLayout.make(
            context,
            searchTextLinearWeight
        )

        val fannelListView = ListView(context)
        val searchText = EditText(context)
//        val fannelListAdapter = ArrayAdapter(
//            context,
//            R.layout.simple_list_item_1,
//            makeFannelListForListView()
//        )
        fannelListAdapter.clear()
        fannelListAdapter.addAll(makeFannelListForListView().toMutableList())
        fannelListAdapter.notifyDataSetChanged()
        fannelListView.adapter = fannelListAdapter
        fannelListView.setSelection(
            fannelListAdapter.count
        )
        makeSearchEditText(
            fannelListView,
            fannelListAdapter,
            searchText
        )

        linearLayoutForListView.addView(fannelListView)
        linearLayoutForSearch.addView(searchText)
        linearLayoutForTotal.addView(linearLayoutForListView)
        linearLayoutForTotal.addView(linearLayoutForSearch)

        val alertDialog = cmdIndexFragment.fannelInstallDialog
            ?: return
        alertDialog.setView(linearLayoutForTotal)
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
                fannelListUpdateJob?.cancel()
            }
        })
        alertDialog.window?.setGravity(Gravity.BOTTOM);
        alertDialog.show()
        invokeItemSetClickListenerForFannel(
            alertDialog,
            fannelListView,
            searchText
        )

        invokeItemSetLongClickListenerForFannel(
            fannelListView,
        )

        updateRealTimeFannelList(fannelListView)
    }


    private fun updateRealTimeFannelList(
        fannelListView: ListView
    ){
        var firstFannelListSource =  ReadText(
            UsePath.cmdclickFannelListDirPath,
            UsePath.fannelListMemoryName,
        ).readText()
        var secondFannelListSource: String
        fannelListUpdateJob?.cancel()
        fannelListUpdateJob = CoroutineScope(Dispatchers.IO).launch {
            while(true) {
                secondFannelListSource = withContext(Dispatchers.IO) {
                    delay(100)
                    ReadText(
                        UsePath.cmdclickFannelListDirPath,
                        UsePath.fannelListMemoryName,
                    ).readText()
                }
                if (
                    firstFannelListSource == secondFannelListSource
                ) continue
                firstFannelListSource = secondFannelListSource
                withContext(Dispatchers.Main) {
                    updateFannelListView(
                        fannelListView,
                        makeFannelListForListView(),
                        fannelListAdapter
                    )
                }
            }
        }
    }

    private fun invokeItemSetClickListenerForFannel(
        alertDialog: AlertDialog,
        fannelListView: ListView,
        searchText: EditText,
    ) {
        fannelListView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog.dismiss()
            cmdIndexFragment.repoCloneJob?.cancel()
            cmdIndexFragment.repoCloneProgressJob?.cancel()
            terminalViewModel.onDialog = false
            fannelListUpdateJob?.cancel()
            val updateFannelsList = makeFannelListForListView()
            val selectedFannel = updateFannelsList.filter {
                Regex(
                    searchText.text
                        .toString()
                        .lowercase()
                ).containsMatchIn(
                    it.lowercase()
                )
            }
                .getOrNull(pos)
                ?.split("\n")
                ?.firstOrNull()
                ?: return@setOnItemClickListener
            if(selectedFannel == blankListMark) return@setOnItemClickListener
            FileSystems.copyFile(
                "${UsePath.cmdclickFannelItselfDirPath}/${selectedFannel}",
                "${currentAppDirPath}/${selectedFannel}"
            )
            val selectedFannelName =
                selectedFannel
                    .removeSuffix(CommandClickShellScript.JS_FILE_SUFFIX)
                    .removeSuffix(CommandClickShellScript.SHELL_FILE_SUFFIX)
            val fannelDir = selectedFannelName + fannelDirSuffix
            FileSystems.copyDirectory(
                "${UsePath.cmdclickFannelItselfDirPath}/${fannelDir}",
                "${currentAppDirPath}/${fannelDir}"
            )
            CommandListManager.execListUpdate(
                currentAppDirPath,
                cmdListAdapter,
                cmdListView,
            )
            Toast.makeText(
                cmdIndexFragment.context,
                "install ok: ${selectedFannelName}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun invokeItemSetLongClickListenerForFannel(
        fannelListView: ListView,
    ) {
        fannelListView.setOnItemLongClickListener {
                parent, listSelectedView, pos, id
            ->
            val popup = PopupMenu(context, listSelectedView)
            val inflater = popup.menuInflater
            inflater.inflate(
                com.puutaro.commandclick.R.menu.history_admin_menu,
                popup.menu
            )
            popup.menu.add(
                FannelMenuEnums.SYNC.groupId,
                FannelMenuEnums.SYNC.itemId,
                FannelMenuEnums.SYNC.order,
                FannelMenuEnums.SYNC.itemName,

                )
            popup.setOnMenuItemClickListener {
                    menuItem ->
                gitCloneAndMakeFannelList()
                true
            }
            popup.show()
            true
        }
    }

    private fun makeSearchEditText(
        fannelListView: ListView,
        fannelListAdapter: ArrayAdapter<String>,
        searchText: EditText
    ){
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.background = null
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.hint = "search"
        searchText.setPadding(30, 10, 20, 10)
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(!searchText.hasFocus()) return
                val updateFannelList = makeFannelListForListView()

                val filteredFannelList = updateFannelList.filter {
                    Regex(
                        s.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                CommandListManager.execListUpdateByEditText(
                    filteredFannelList,
                    fannelListAdapter,
                    fannelListView
                )
                fannelListView.setSelection(
                    fannelListAdapter.count
                )
            }
        })
    }

    private fun gitCloneAndMakeFannelList(){
        if(context == null) return
        val intent = Intent(cmdIndexFragment.activity, GitCloneService::class.java)
        context.startService(intent)
    }

    private fun updateFannelListView(
        fannelListView: ListView,
        updatedFannelList: List<String>,
        fannelListAdapter: ArrayAdapter<String>
    ){
        try {
            if (
                !fannelListView.isVisible
            ) return
            CommandListManager.execListUpdateByEditText(
                updatedFannelList,
                fannelListAdapter,
                fannelListView
            )
            if (
                !fannelListView.isVisible
            ) return
            fannelListView.setSelection(
                fannelListAdapter.count
            )
        } catch (e: Exception){
            Log.e(LoggerTag.fannnelListUpdateErr,"cannot update fannel list view")
        }
    }

    private fun makeFannelListForListView(): List<String> {
        val fannelListSource =  ReadText(
            UsePath.cmdclickFannelListDirPath,
            UsePath.fannelListMemoryName,
        ).readText().split(cmdclickFannelListSeparator)
        return if(
            fannelListSource.isNotEmpty()
            && !fannelListSource
                .firstOrNull()
                ?.trim()
                .isNullOrEmpty()
        ) {
            fannelListSource
        } else mutableListOf(blankListMark)
    }
}

private const val mainMenuGroupId = 100000

private enum class FannelMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    SYNC(mainMenuGroupId, 100100, 1, "sync"),
}
