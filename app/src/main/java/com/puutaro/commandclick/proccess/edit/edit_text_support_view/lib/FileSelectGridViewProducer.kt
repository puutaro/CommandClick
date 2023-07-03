package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer.getFcbMap
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer.getFilterType
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.util.BothEdgeQuote
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.SharePreffrenceMethod
import java.io.File

object FileSelectGridViewProducer {
    private val noExtend = "NoExtend"
    private val gridButtonLabel = "ISL"

    private var alertDialog: AlertDialog? = null

    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        weight: Float,
    ): Button {
        val currentFragment = editParameters.currentFragment
        val context = editParameters.context
        val currentId = editParameters.currentId
        val linearParamsForGrid = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForGrid.weight = weight
        val fcbMap = getFcbMap(
            editParameters
        )
        val filterDir = getSelectDirPath(
            fcbMap,
            editParameters,
        )
        val filterPrefix = getFilterPrefix(
            fcbMap,
        )
        val filterSuffix = getFilterSuffix(
            fcbMap,
        )
        val filterType = getFilterType(
            fcbMap,
        )

        val gridButtonView = Button(context)
        gridButtonView.id = currentId + EditTextSupportViewId.EDITABLE_GRID.id
        gridButtonView.tag = "gridEdit${currentId + EditTextSupportViewId.EDITABLE_GRID.id}"
        gridButtonView.text = gridButtonLabel

        gridButtonView.setOnClickListener {
                buttonView ->
            val buttonContext = buttonView.context
            val currentGridList = makeGridList(
                filterDir,
                filterPrefix,
                filterSuffix,
                filterType
            )

            val gridView =
                GridView(buttonContext)
            gridView.numColumns = 2
            val adapter = ImageAdapter(
                buttonContext,
            )
            adapter.addAll(
                currentGridList.toMutableList()
            )
            gridView.adapter = adapter
            setGridViewItemClickListener(
                currentFragment,
                insertEditText,
                gridView,
                adapter,
                editParameters,
                fcbMap,
            )

            alertDialog = AlertDialog.Builder(
                buttonContext
            )
                .setView(gridView)
                .create()
            alertDialog?.window?.setGravity(Gravity.BOTTOM)
            alertDialog?.show()

            alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    alertDialog?.dismiss()
                }
            })
        }
        gridButtonView.layoutParams = linearParamsForGrid
        return gridButtonView
    }

    private fun setGridViewItemClickListener(
        currentFragment: Fragment,
        insertEditText: EditText,
        gridView: GridView,
        adapter: ImageAdapter,
        editParameters: EditParameters,
        fcbMap: Map<String, String>?
    ){
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val filterDir = getSelectDirPath(
            fcbMap,
            editParameters,
        )
        val filterPrefix = getFilterPrefix(
            fcbMap,
        )
        val filterSuffix = getFilterSuffix(
            fcbMap,
        )
        val filterType = getFilterType(
            fcbMap,
        )
        val selectJsPath = getSelectJsPath(
            fcbMap
        )
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            alertDialog?.dismiss()
            val selectedItem = adapter.getItem(pos)
            if(
                currentAppDirPath != UsePath.cmdclickAppHistoryDirAdminPath
            ) {
                FileSystems.updateLastModified(
                    filterDir,
                    selectedItem
                )
            }
            val currentGridList = makeGridList(
                filterDir,
                filterPrefix,
                filterSuffix,
                filterType
            )
            val selectUpdatedGridList = listOf(
                selectedItem,
            ) + currentGridList.filter {
                it != selectedItem
            }
            adapter.clear()
            adapter.addAll(selectUpdatedGridList.toMutableList())
            adapter.notifyDataSetChanged()
            gridView.setSelection(0)
            insertEditText.setText(selectedItem)
            SelectJsExecutor.exec(
                currentFragment,
                selectJsPath,
                selectedItem,
            )
        }
    }

    private fun judgeBySuffix(
        targetStr: String,
        filterSuffix: String,
    ): Boolean{
        if(filterSuffix != noExtend) {
            return filterSuffix.split("&").any {
                targetStr.endsWith(it)
            }
        }
        return !Regex("\\..*$").containsMatchIn(targetStr)
    }

    private fun makeGridList(
        filterDir: String,
        filterPrefix: String,
        filterSuffix: String,
        filterType: String,
    ): List<String> {
        val sortedList = FileSystems.sortedFiles(
            filterDir,
            "on"
        )
        if (
            filterType != FilterFileType.dir.name
        ) return sortedList.filter {
            it.startsWith(filterPrefix)
                    && judgeBySuffix(it, filterSuffix)
                    && File("$filterDir/$it").isFile
        }
        return sortedList.filter {
            it.startsWith(filterPrefix)
                    && judgeBySuffix(it, filterSuffix)
                    && File("$filterDir/$it").isDirectory
        }
    }

    private fun getSelectDirPath(
        fcbMap: Map<String, String>?,
        editParameters: EditParameters,
    ): String {
        val currentAppDirPath = SharePreffrenceMethod.getReadSharePreffernceMap(
            editParameters.readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        return fcbMap?.get(
            FileSelectEditKey.dirPath.name
        )?.let {
            if (
                it.isEmpty()
            ) return@let currentAppDirPath
            it
        } ?: currentAppDirPath
    }

    private fun getFilterPrefix(
        fcbMap: Map<String, String>?,
    ): String {
        return fcbMap?.get(FileSelectEditKey.prefix.name)?.let {
            BothEdgeQuote.trim(it)
        } ?: String()
    }

    private fun getFilterSuffix(
        fcbMap: Map<String, String>?,
    ): String {
        return fcbMap?.get(FileSelectEditKey.suffix.name)?.let {
            BothEdgeQuote.trim(it)
        } ?: String()
    }

    private fun getSelectJsPath(
        fcbMap: Map<String, String>?,
    ): String {
        return fcbMap?.get(FileSelectEditKey.selectJs.name)?.let {
            val trimType = BothEdgeQuote.trim(it)
            if(
                trimType.isEmpty()
            ) return@let FilterFileType.file.name
            trimType
        } ?: FilterFileType.file.name
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