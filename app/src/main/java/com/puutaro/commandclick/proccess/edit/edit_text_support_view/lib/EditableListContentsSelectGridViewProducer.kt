package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.ImageAdapter
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getElsbMap
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getLimitNum
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getListPath
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.ListContentsSelectSpinnerViewProducer.getSelectJsPath
import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.lib.SelectJsExecutor
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.ReadText
import java.io.File


object EditableListContentsSelectGridViewProducer {

    private var alertDialog: AlertDialog? = null
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
        val listContentsFilePath = getListPath(
            elcbMap,
        )

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)
        val gridButtonView = Button(context)
        gridButtonView.text = gridButtonLabel
        ButtonSetter.set(
            context,
            gridButtonView
        )
        gridButtonView.setOnClickListener {
            buttonView ->
            val buttonContext = buttonView.context
            val editableSpinnerList = ReadText(
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }

            val gridView =
                GridView(buttonContext)

            gridView.numColumns = 2
            val adapter = ImageAdapter(
                buttonContext,
            )

            val searchText = EditText(context)
            makeSearchEditText(
                adapter,
                searchText,
                editableSpinnerList.joinToString("\n"),
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
            linearLayoutForSearch.addView(searchText)
            linearLayoutForTotal.addView(linearLayoutForListView)
            linearLayoutForTotal.addView(linearLayoutForSearch)


            adapter.addAll(editableSpinnerList.toMutableList())
            gridView.adapter = adapter
            setGridViewItemClickListener(
                currentFragment,
                insertEditText,
                searchText,
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
                }
            })
        }


        gridButtonView.layoutParams = linearParamsForGridButton
        return gridButtonView
    }

    private fun setGridViewItemClickListener(
        currentFragment: Fragment,
        insertEditText: EditText,
        searchText: EditText,
        gridView: GridView,
        adapter: ImageAdapter,
        elcbMap: Map<String, String>?,
    ){
        val listContentsFilePath = getListPath(
            elcbMap,
        )
        val listLimit = getLimitNum(
            elcbMap,
            defaultListLimit,
        )

        val selectJsPath = getSelectJsPath(
            elcbMap
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
            alertDialog?.dismiss()
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

    private fun makeSearchEditText(
        imageAdapter:  ImageAdapter,
        searchText: EditText,
        listCon: String,
    ) {
        val linearLayoutParamForSearchText = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )
        linearLayoutParamForSearchText.topMargin = 20
        linearLayoutParamForSearchText.bottomMargin = 20
        searchText.layoutParams = linearLayoutParamForSearchText
        searchText.inputType = InputType.TYPE_CLASS_TEXT
        searchText.background = null
        searchText.hint = "search"
        searchText.setPadding(30, 10, 20, 10)
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