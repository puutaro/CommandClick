package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.OnlyImageAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.state.SharePrefTool
import java.io.File

object EditableListContentsSelectOnlyImageGridViewProducer {

    private var alertDialog: AlertDialog? = null
    private val defaultListLimit = 100
    private val gridButtonLabel = "IGS"

    fun make (
        insertEditText: EditText,
        editParameters: EditParameters,
        currentComponentIndex: Int,
        weight: Float,
    ): Button {
        val currentFragment = editParameters.currentFragment
        val context = editParameters.context
        val linearParamsForGridButton = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForGridButton.weight = weight

        val elcbMap = ListContentsSelectSpinnerViewProducer.getElsbMap(
            editParameters,
            currentComponentIndex
        )
        val listContentsFilePath = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        FileSystems.createDirs(parentDir)
        val gridButtonView = Button(context)
        gridButtonView.text = gridButtonLabel
        ButtonSetter.set(
            context,
            gridButtonView,
            mapOf()
        )
        gridButtonView.setOnClickListener {
                buttonView ->
            val buttonContext = buttonView.context
            val editableSpinnerList = ReadText(
                listContentsFilePath
            ).textToList().filter {
                it.trim().isNotEmpty()
            }

            val gridView =
                GridView(buttonContext)

            gridView.numColumns = 2
            val adapter = OnlyImageAdapter(
                buttonContext,
            )

            val linearLayoutForTotal = LinearLayoutForTotal.make(
                context
            )
            val searchTextWeight = SearchTextLinearWeight.calculate(currentFragment)
            val listWeight = 1F - searchTextWeight
            val linearLayoutForListView = NestLinearLayout.make(
                context,
                listWeight
            )
            val linearLayoutForSearch = NestLinearLayout.make(
                context,
                searchTextWeight
            )
            linearLayoutForListView.addView(gridView)
            linearLayoutForTotal.addView(linearLayoutForListView)
            linearLayoutForTotal.addView(linearLayoutForSearch)


            adapter.addAll(editableSpinnerList.toMutableList())
            gridView.adapter = adapter
            setGridViewItemClickListener(
                currentFragment,
                editParameters,
                insertEditText,
                gridView,
                adapter,
                elcbMap,
            )

            alertDialog = AlertDialog.Builder(
                buttonContext
            )
                .setView(linearLayoutForTotal)
                .create()
            alertDialog?.window?.setGravity(Gravity.BOTTOM)
            alertDialog?.show()

            alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    alertDialog?.dismiss()
                    alertDialog = null
                }
            })
        }


        gridButtonView.layoutParams = linearParamsForGridButton
        return gridButtonView
    }

    private fun setGridViewItemClickListener(
        currentFragment: Fragment,
        editParameters: EditParameters,
        insertEditText: EditText,
        gridView: GridView,
        adapter: OnlyImageAdapter,
        elcbMap: Map<String, String>?,
    ){
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentAppDirPath = SharePrefTool.getCurrentAppDirPath(
            readSharePreffernceMap
        )
        val scriptName = SharePrefTool.getCurrentFannelName(
            readSharePreffernceMap
        )
        val listContentsFilePath = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()
        val listLimit = ListContentsSelectSpinnerViewProducer.getLimitNum(
            elcbMap,
            defaultListLimit,
        )

        val selectJsPath = ListContentsSelectSpinnerViewProducer.getSelectJsPath(
            elcbMap,
            currentAppDirPath,
            scriptName,
        )

        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            Keyboard.hiddenKeyboardForFragment(
                currentFragment
            )
            alertDialog?.dismiss()
            alertDialog = null
            val currentGridList = ReadText(
                listContentsFilePath
            ).textToList()
            val selectedItem = currentGridList.get(pos)

            val updateListContents =
                listOf(selectedItem) +
                        currentGridList.filter {
                            it != selectedItem
                        }
            FileSystems.writeFile(
                listContentsFilePath,
                updateListContents
                    .take(listLimit)
                    .joinToString("\n")
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
                selectedItem
            )
        }
    }
}