package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.Gravity
import android.widget.AbsListView
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.MultiSelectImageAdapter
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.proccess.lib.LinearLayoutForTotal
import com.puutaro.commandclick.proccess.lib.NestLinearLayout
import com.puutaro.commandclick.proccess.lib.SearchTextLinearWeight
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.ReadText
import java.io.File

object EditableListContentsMultiSelectGridViewProducer {
    private var alertDialog: AlertDialog? = null
    private const val gridButtonLabel = "MGS"

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
        val listFileName = fileObj.name
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
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }

            val gridView =
                GridView(buttonContext)

            gridView.numColumns = 2
            gridView.choiceMode = AbsListView.CHOICE_MODE_MULTIPLE
            val multiSelectImageAdapter = MultiSelectImageAdapter(
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


            multiSelectImageAdapter.addAll(editableSpinnerList.toMutableList())
            gridView.adapter = multiSelectImageAdapter
            val editTextSelectedList =
                insertEditText
                    .text
                    .split(",")
                    .filter {
                        editableSpinnerList.contains(it)
                    }
            multiSelectImageAdapter
                .selectedItemList
                .addAll(editTextSelectedList)
            multiSelectImageAdapter.notifyDataSetChanged()

            setGridViewItemClickListener(
                currentFragment,
                gridView,
                multiSelectImageAdapter,
            )

            alertDialog = AlertDialog.Builder(
                buttonContext
            )
                .setView(linearLayoutForTotal)
                .setNegativeButton("NO", DialogInterface.OnClickListener{
                        dialog, which ->
                    alertDialog?.dismiss()
                })
                .setPositiveButton("OK", DialogInterface.OnClickListener{
                        dialog, which ->
                    alertDialog?.dismiss()
                    val selectedItems =
                        multiSelectImageAdapter
                            .selectedItemList
                            .joinToString(",")
                    insertEditText.setText(selectedItems)
                })
                .show()
            alertDialog?.window?.setGravity(Gravity.BOTTOM)
            alertDialog?.setOnCancelListener(
                object : DialogInterface.OnCancelListener {
                    override fun onCancel(dialog: DialogInterface?) {
                        alertDialog?.dismiss()
                    }
                })
            alertDialog?.getButton(
                DialogInterface.BUTTON_POSITIVE
            )?.setTextColor(
                context?.getColor(
                    android.R.color.black
                ) as Int
            )
            alertDialog?.getButton(
                DialogInterface.BUTTON_NEGATIVE
            )?.setTextColor(
                context?.getColor(
                    android.R.color.black
                ) as Int
            )
        }


        gridButtonView.layoutParams = linearParamsForGridButton
        return gridButtonView
    }

    private fun setGridViewItemClickListener(
        currentFragment: Fragment,
        gridView: GridView,
        multiSelectImageAdapter: MultiSelectImageAdapter,
    ){
        gridView.setOnItemClickListener {
                parent, View, pos, id
            ->
            Keyboard.hiddenKeyboardForFragment(
                currentFragment
            )
            multiSelectImageAdapter.onItemSelect(
                View,
                pos
            )
        }
    }
}
