package com.puutaro.commandclick.fragment_lib.command_index_fragment.setting_button

import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.github.syari.kgit.KGit
import com.puutaro.commandclick.common.variable.CommandClickShellScript
import com.puutaro.commandclick.common.variable.LoggerTag
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.CommandListManager
import com.puutaro.commandclick.proccess.ScriptFileDescription
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.view_model.activity.TerminalViewModel
import kotlinx.coroutines.*
import org.eclipse.jgit.lib.TextProgressMonitor
import java.io.File

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
    private val cmdclickFannelListSeparator = "CMDCLICK_FANNEL_LIST_SEPARATOR"
    var onDisplayProgress = false
    private val blankListMark = "let's sync by long click"
    private val fannelDirSuffix = "Dir"
    private val descriptionFirstLineLimit = 50
    private val monitorRoopLimit = 50
    private val monitoringDuration = 3000L

    fun install(){
        if(context == null) return
        onDisplayProgress = false
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
        val fannelListAdapter = ArrayAdapter(
            context,
            R.layout.simple_list_item_1,
            makeFannelListForListView()
        )
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

        val alertDialogBuilder = AlertDialog.Builder(
            context
        )
            .setTitle("Select from bellow fannels")
            .setView(linearLayoutForTotal)
        val alertDialog =
            alertDialogBuilder
                .create()
        alertDialog.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                terminalViewModel.onDialog = false
                cmdIndexFragment.repoCloneJob?.cancel()
                cmdIndexFragment.repoCloneProgressJob?.cancel()
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
            fannelListAdapter,
        )
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
        fannelListAdapter: ArrayAdapter<String>,
    ) {
        fannelListView.setOnItemLongClickListener {
                parent, listSelectedView, pos, id
            ->
            if(
                onDisplayProgress
            ) return@setOnItemLongClickListener false
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
                gitCloneAndMakeFannelList(
                    fannelListView,
                    fannelListAdapter
                )
                true
            }
            popup.show()
            true
        }
    }

    private fun makeFannelListMemoryContents(): List<String> {
        val cmdclickFannelItselfDirPath = UsePath.cmdclickFannelItselfDirPath
        if(
            !File(cmdclickFannelItselfDirPath).isDirectory
        ) return emptyList()
        val fannelsListSource = FileSystems.filterSuffixShellOrJsOrHtmlFiles(
            cmdclickFannelItselfDirPath,
        )
        return fannelsListSource.map {
            val descFirstLineSource = ScriptFileDescription.makeDescriptionContents(
                ReadText(
                    cmdclickFannelItselfDirPath,
                    it
                ).textToList(),
                it
            ).split('\n').firstOrNull()
            val descFirstLine = if(
                !descFirstLineSource.isNullOrEmpty()
                && descFirstLineSource.length > descriptionFirstLineLimit
            ) descFirstLineSource.substring(0, descriptionFirstLineLimit)
            else descFirstLineSource
            return@map if(descFirstLine.isNullOrEmpty()) it
            else {
                "$it\n\t\t- $descFirstLine"
            }
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

    private fun gitCloneAndMakeFannelList(
        fannelListView: ListView,
        fannelListAdapter: ArrayAdapter<String>
    ){
        if(context == null) return
        onDisplayProgress = true
        var progressBar = "sync.. #"
        cmdIndexFragment.repoCloneProgressJob?.cancel()
        cmdIndexFragment.repoCloneProgressJob = CoroutineScope(Dispatchers.IO).launch {
            for (cnt in 1..monitorRoopLimit)  {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        cmdIndexFragment.context,
                        progressBar,
                        Toast.LENGTH_SHORT
                    ).show()
                    progressBar += "#"
                }
                if(!onDisplayProgress) break
                withContext(Dispatchers.IO) {
                    delay(monitoringDuration)
                }
            }
        }

        cmdIndexFragment.repoCloneJob?.cancel()
        cmdIndexFragment.repoCloneJob = CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.IO) {
                val cmdclickFannelAppsDirPath = UsePath.cmdclickFannelAppsDirPath
                val repoFileObj = File(cmdclickFannelAppsDirPath)
                FileSystems.removeDir(cmdclickFannelAppsDirPath)
                FileSystems.createDirs(cmdclickFannelAppsDirPath)
                execGitClone(repoFileObj)
            }
            withContext(Dispatchers.IO){
                delay(100)
                FileSystems.writeFile(
                    UsePath.cmdclickFannelListDirPath,
                    UsePath.fannelListMemoryName,
                    makeFannelListMemoryContents().joinToString(cmdclickFannelListSeparator)
                )
            }
            withContext(Dispatchers.Main) {
                onDisplayProgress = false
                cmdIndexFragment.repoCloneProgressJob?.cancel()
                val updatedFannelList = makeFannelListForListView()
                updateFannelListView(
                    fannelListView,
                    updatedFannelList,
                    fannelListAdapter
                )
                Toast.makeText(
                    cmdIndexFragment.context,
                    "sync ok",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
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

private fun execGitClone(
    repoFileObj: File
){
    var git: KGit? = null
    try {
        git = KGit.cloneRepository {
            setURI(WebUrlVariables.commandClickRepositoryUrl)
            setDirectory(repoFileObj)
            setTimeout(60)
            setProgressMonitor(TextProgressMonitor())
        }
        git.close()
    } catch (e: Exception) {
        git?.close()
        return
    }
}


private val mainMenuGroupId = 100000

private enum class FannelMenuEnums(
    val groupId: Int,
    val itemId: Int,
    val order: Int,
    val itemName: String
) {
    SYNC(mainMenuGroupId, 100100, 1, "sync"),
}

