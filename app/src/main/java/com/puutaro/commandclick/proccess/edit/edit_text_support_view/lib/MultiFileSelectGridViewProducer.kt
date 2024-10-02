package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.component.adapter.MultiSelectImageAdapter
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.FilterPathTool
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.str.QuoteTool
import java.io.File

object MultiFileSelectGridViewProducer {
    private val noExtend = "NoExtend"
    private val gridButtonLabel = "MGS"

    private var alertDialog: AlertDialog? = null

//    fun make (
//        fragment: Fragment,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Button {
//        val context = fragment.context
//        val currentId = editParameters.currentId
//        val linearParamsForGrid = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForGrid.weight = weight
//        val fcbMap = FileSelectSpinnerViewProducer.getFcbMap(
//            editParameters,
//            currentComponentIndex
//        )
//        val filterDir = getSelectDirPath(
//            fcbMap,
////            editParameters,
//        )
//        val filterPrefixListCon = getFilterPrefix(
//            fcbMap,
//        )
//        val filterSuffixListCon = getFilterSuffix(
//            fcbMap,
//        )
//        val filterType = FileSelectSpinnerViewProducer.getFilterType(
//            fcbMap,
//        )
//
//        val gridButtonView = Button(context)
//        gridButtonView.id = currentId + EditTextSupportViewId.EDITABLE_GRID.id
//        gridButtonView.tag = "gridEdit${currentId + EditTextSupportViewId.EDITABLE_GRID.id}"
//        gridButtonView.text = gridButtonLabel
//        ButtonSetter.set(
//            context,
//            gridButtonView,
//            mapOf()
//        )
//
//        gridButtonView.setOnClickListener {
//                buttonView ->
//            val buttonContext = buttonView.context
//            val currentGridList = makeGridList(
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                filterType
//            )
//
//            val gridView =
//                GridView(buttonContext)
//            gridView.numColumns = 2
//            gridView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
//            val multiSelectImageAdapter = MultiSelectImageAdapter(
//                buttonContext,
//            )
//            multiSelectImageAdapter.addAll(
//                currentGridList.toMutableList()
//            )
//            gridView.adapter = multiSelectImageAdapter
//            val editTextSelectedList = insertEditText.text.split(",")
//            val editTextSelectedListByFullPath = currentGridList.filter {
//                editTextSelectedList.contains(
//                    File(it).name
//                )
//            }
//            multiSelectImageAdapter
//                .selectedItemList
//                .addAll(editTextSelectedListByFullPath)
//            multiSelectImageAdapter.notifyDataSetChanged()
//
//            val linearLayoutForTotal = LinearLayoutForTotal.make(
//                context
//            )
//            val searchTextWeight = SearchTextLinearWeight.calculate(fragment.activity)
//            val listWeight = 1F - searchTextWeight
//            val linearLayoutForListView = NestLinearLayout.make(
//                context,
//                listWeight
//            )
//            val linearLayoutForSearch = NestLinearLayout.make(
//                context,
//                searchTextWeight
//            )
//            linearLayoutForListView.addView(gridView)
//            linearLayoutForTotal.addView(linearLayoutForListView)
//            linearLayoutForTotal.addView(linearLayoutForSearch)
//
//            setGridViewItemClickListener(
//                fragment,
//                gridView,
//            )
//
//            alertDialog = AlertDialog.Builder(
//                buttonContext
//            )
//                .setView(linearLayoutForTotal)
//                .setNegativeButton("NO", DialogInterface.OnClickListener{
//                        dialog, which ->
//                    alertDialog?.dismiss()
//                    alertDialog = null
//                })
//                .setPositiveButton("OK", DialogInterface.OnClickListener{
//                        dialog, which ->
//                    alertDialog?.dismiss()
//                    alertDialog = null
//                    val selectedItems = multiSelectImageAdapter.selectedItemList.map {
//                        File(it).name
//                    }.joinToString(",")
//                    insertEditText.setText(selectedItems)
//                })
//                .show()
//            alertDialog?.window?.setGravity(Gravity.BOTTOM)
//
//            alertDialog?.setOnCancelListener(
//                object : DialogInterface.OnCancelListener {
//                    override fun onCancel(dialog: DialogInterface?) {
//                        alertDialog?.dismiss()
//                        alertDialog = null
//                    }
//                })
//            alertDialog?.getButton(
//                DialogInterface.BUTTON_POSITIVE
//            )?.setTextColor(
//                context?.getColor(android.R.color.black) as Int
//            )
//            alertDialog?.getButton(
//                DialogInterface.BUTTON_NEGATIVE
//            )?.setTextColor(
//                context?.getColor(android.R.color.black) as Int
//            )
//        }
//        gridButtonView.layoutParams = linearParamsForGrid
//        return gridButtonView
//    }

    private fun setGridViewItemClickListener(
        editFragment: Fragment,
        gridView: GridView,
    ){
        Keyboard.hiddenKeyboardForFragment(
            editFragment
        )
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            val multiSelectImageAdapter =
                gridView.adapter as MultiSelectImageAdapter
            multiSelectImageAdapter.onItemSelect(
                View,
                pos
            )
        }
    }

    private fun judgeBySuffix(
        targetStr: String,
        filterSuffix: String,
    ): Boolean{
        if(filterSuffix != noExtend) {
            return QuoteTool.splitBySurroundedIgnore(
                filterSuffix,
                '&'
            )
//            filterSuffix.split("&")
                .any {
                targetStr.endsWith(it)
            }
        }
        return !Regex("\\..*$").containsMatchIn(targetStr)
    }

    private fun makeGridList(
        filterDir: String,
        filterPrefixListCon: String,
        filterSuffixListCon: String,
        filterType: String,
    ): List<String> {
        val sortedList = FileSystems.sortedFiles(
            filterDir,
            "on"
        )
        val isFile = filterType != FilterFileType.dir.name
        return when(isFile){
            true -> {
                sortedList.filter {
                    FilterPathTool.isFilterByFile(
                        it,
                        filterDir,
                        filterPrefixListCon,
                        filterSuffixListCon,
                        true,
                        "&"
                    )
//                    it.startsWith(filterPrefixListCon)
//                            && judgeBySuffix(it, filterSuffixListCon)
//                            && File("$filterDir/$it").isFile
                }.map {
                    "$filterDir/$it"
                }
            }
            false -> sortedList.filter {
                FilterPathTool.isFilterByDir(
                    it,
                    filterDir,
                    filterPrefixListCon,
                    filterSuffixListCon,
                    true,
                    "&"
                )
//                it.startsWith(filterPrefixListCon)
//                        && judgeBySuffix(it, filterSuffixListCon)
//                        && File("$filterDir/$it").isDirectory
            }.map {
                "$filterDir/$it"
            }
        }
    }

    private fun getSelectDirPath(
        fcbMap: Map<String, String>?,
//        editParameters: EditParameters,
    ): String {
        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            editParameters.fannelInfoMap
//        )
        return fcbMap?.get(
            FileSelectEditKey.dirPath.name
        )?.let {
            if (
                it.isEmpty()
            ) return@let cmdclickDefaultAppDirPath
            it
        } ?: cmdclickDefaultAppDirPath
    }

    private fun getFilterPrefix(
        fcbMap: Map<String, String>?,
    ): String {
        return fcbMap?.get(FileSelectEditKey.prefix.name)?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    private fun getFilterSuffix(
        fcbMap: Map<String, String>?,
    ): String {
        return fcbMap?.get(FileSelectEditKey.suffix.name)?.let {
            QuoteTool.trimBothEdgeQuote(it)
        } ?: String()
    }

    private enum class FileSelectEditKey {
        dirPath,
        prefix,
        suffix,
        type,
        selectJs
    }

    private enum class FilterFileType {
        file,
        dir,
    }
}