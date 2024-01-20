package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.settings.SharePrefferenceSetting
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getElsbMap
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getLimitNum
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getSelectJsPath
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.ReadText
import com.puutaro.commandclick.util.state.SharePreferenceMethod
import java.io.File


object EditableListContentsSelectGridViewProducer {

    private var gridDialogObj: Dialog? = null
    private val defaultListLimit = 100
    private val gridButtonLabel = "GSL"

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

        val elcbMap = getElsbMap(
            editParameters,
            currentComponentIndex
        )
        val listContentsFilePath = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()
        val gridButtonView = Button(context)
        gridButtonView.text = gridButtonLabel
        ButtonSetter.set(
            context,
            gridButtonView
        )
        gridButtonView.setOnClickListener {
            buttonView ->
            val buttonContext = buttonView.context
            gridDialogObj = Dialog(
                buttonContext
            )
            gridDialogObj?.setContentView(
                com.puutaro.commandclick.R.layout.grid_dialog_layout
            )
            setGridListView(
                currentFragment,
                editParameters,
                insertEditText,
                listContentsFilePath,
                elcbMap
            )
            gridDialogObj?.setOnCancelListener {
                gridDialogObj?.dismiss()
            }
            gridDialogObj?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gridDialogObj?.window?.setGravity(Gravity.BOTTOM)
            gridDialogObj?.show()
        }
        gridButtonView.layoutParams = linearParamsForGridButton
        return gridButtonView
    }

    private fun setGridListView(
        currentFragment: Fragment,
        editParameters: EditParameters,
        insertEditText: EditText,
        listContentsFilePath: String,
        elcbMap: Map<String, String>?
    ) {
        val context = currentFragment.context
            ?: return
        val listDialogSearchEditText = gridDialogObj?.findViewById<AppCompatEditText>(
            com.puutaro.commandclick.R.id.grid_dialog_search_edit_text
        ) ?: return
        listDialogSearchEditText.hint = "search selectable list"
        val editableSpinnerList =
            makeGridList(listContentsFilePath)
        val gridView =
            gridDialogObj?.findViewById<GridView>(
                com.puutaro.commandclick.R.id.grid_dialog_grid_view
            ) ?: return
        val imageAdapter = ImageAdapter(
            context,
        )
        imageAdapter.clear()
        imageAdapter.addAll(
            editableSpinnerList.toMutableList()
        )
        gridView.adapter = imageAdapter
        makeSearchEditText(
            gridView,
            listDialogSearchEditText,
            editableSpinnerList.joinToString("\n"),
        )
        setGridViewItemClickListener(
            currentFragment,
            editParameters,
            insertEditText,
            listDialogSearchEditText,
            gridView,
            elcbMap,
        )
    }

    private fun makeGridList(
        listContentsFilePath: String,
    ): List<String> {
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)
        return ReadText(
            parentDir,
            listFileName
        ).textToList().filter {
            it.trim().isNotEmpty()
        }
    }

    private fun setGridViewItemClickListener(
        currentFragment: Fragment,
        editParameters: EditParameters,
        insertEditText: EditText,
        searchText: EditText,
        gridView: GridView,
        elcbMap: Map<String, String>?,
    ){
        val readSharePreffernceMap = editParameters.readSharePreffernceMap
        val currentAppDirPath = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_app_dir
        )
        val scriptName = SharePreferenceMethod.getReadSharePreffernceMap(
            readSharePreffernceMap,
            SharePrefferenceSetting.current_fannel_name
        )
        val listContentsFilePath = elcbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()
        val listLimit = getLimitNum(
            elcbMap,
            defaultListLimit,
        )

        val selectJsPath = getSelectJsPath(
            elcbMap,
            currentAppDirPath,
            scriptName,

        )
        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name

        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            Keyboard.hiddenKeyboardForFragment(
                currentFragment
            )
            gridDialogObj?.dismiss()
            val currentGridList = ReadText(
                parentDir,
                listFileName
            ).textToList()
            val selectedItem = currentGridList.filter {
                Regex(
                    searchText.text.toString()
                        .lowercase()
                        .replace("\n", "")
                ).containsMatchIn(
                    it.lowercase()
                )
            }.filter {
                it.trim().isNotEmpty()
            }.get(pos)

            val updateListContents =
                listOf(selectedItem) +
                        currentGridList.filter {
                            it != selectedItem
                        }
            FileSystems.writeFile(
                parentDir,
                listFileName,
                updateListContents
                    .take(listLimit)
                    .joinToString("\n")
            )
            val selectUpdatedGridList = listOf(
                selectedItem,
            ) + currentGridList.filter {
                it != selectedItem
            }
            val imageAdapter = gridView.adapter as ImageAdapter
            imageAdapter.clear()
            imageAdapter.addAll(selectUpdatedGridList.toMutableList())
            imageAdapter.notifyDataSetChanged()
            gridView.setSelection(0)
            insertEditText.setText(selectedItem)
            SelectJsExecutor.exec(
                currentFragment,
                selectJsPath,
                selectedItem
            )
        }
    }

    private fun makeSearchEditText(
        fannelListGridView:  GridView,
        searchText: AppCompatEditText,
        listCon: String,
    ) {
        val imageAdapter =
            fannelListGridView.adapter as ImageAdapter
        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (!searchText.hasFocus()) return
                val filteredList = listCon.split("\n").filter {
                    Regex(
                        searchText.text.toString()
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        it.lowercase()
                    )
                }
                imageAdapter.clear()
                imageAdapter.addAll(filteredList.toMutableList())
                imageAdapter.notifyDataSetChanged()
            }
        })
    }
}