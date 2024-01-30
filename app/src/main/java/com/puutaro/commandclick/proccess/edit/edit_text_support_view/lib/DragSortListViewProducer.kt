package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.DragSortRecyclerAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.proccess.edit.lib.ButtonSetter
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.list.ListTool
import java.io.File


object DragSortListViewProducer {

    private var dragSortDialogObj: Dialog? = null
    private const val dragSortButtonLabel = "DST"

    fun make(
        editFragment: EditFragment,
        editParameters: EditParameters,
        currentComponentIndex: Int,
        weight: Float,
    ): Button {
        val currentFragment = editParameters.currentFragment
        val context = editParameters.context
        val currentId = editParameters.currentId
        val linearParamsForDragSortListView = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.MATCH_PARENT,
        )
        linearParamsForDragSortListView.weight = weight
        val curSetValMap = editParameters.setVariableMap
        val elsbMap = ListContentsSelectSpinnerViewProducer.getElsbMap(
            editParameters,
            currentComponentIndex
        )
        val listContentsFilePath = elsbMap?.get(
            ListContentsSelectSpinnerViewProducer.ListContentsEditKey.listPath.name
        ) ?: String()

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)

        val dragSortListViewButtonView = Button(context)
        dragSortListViewButtonView.layoutParams = linearParamsForDragSortListView
        dragSortListViewButtonView.id = currentId + EditTextSupportViewId.EDITABLE_GRID.id
        dragSortListViewButtonView.tag = "gridEdit${currentId + EditTextSupportViewId.EDITABLE_GRID.id}"
        dragSortListViewButtonView.text = dragSortButtonLabel
        ButtonSetter.set(
            context,
            dragSortListViewButtonView,
            mapOf()
        )
        dragSortListViewButtonView.setOnClickListener {
                buttonView ->
            val buttonContext = buttonView.context
            dragSortDialogObj = Dialog(
                buttonContext
            )
            dragSortDialogObj?.setContentView(
                R.layout.drag_sort_dialog_layout
            )
            val titleTextView =
                dragSortDialogObj?.findViewById<AppCompatTextView>(
                    R.id.drag_sort_dialog_title
                )
            titleTextView?.text = "Sort by drag, or remove by swipe"
            setDragSortRecyclerView(
                currentFragment,
                listContentsFilePath,
            )
            dragSortDialogObj?.setOnCancelListener {
                dragSortDialogObj?.dismiss()
            }
            val cancelButton =
                dragSortDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.drag_sort_dialog_cancel
                )
            cancelButton?.setOnClickListener {
                dragSortDialogObj?.dismiss()
            }
            val okButton =
                dragSortDialogObj?.findViewById<AppCompatImageButton>(
                    R.id.drag_sort_dialog_ok
                )
            okButton?.setOnClickListener {
                val dragSortRecyclerView = dragSortDialogObj?.findViewById<RecyclerView>(
                    R.id.drag_sort_dialog_recycler_view
                )
                val dragSortAdapter =
                    dragSortRecyclerView?.adapter as? DragSortRecyclerAdapter
                dragSortDialogObj?.dismiss()
                dragSortAdapter?.let {
                    FileSystems.writeFile(
                        parentDir,
                        listFileName,
                        dragSortAdapter.dratSortList.joinToString("\n")
                    )
                }
            }
            dragSortDialogObj?.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            dragSortDialogObj?.window?.setGravity(Gravity.BOTTOM)
            dragSortDialogObj?.show()
        }
        return dragSortListViewButtonView
    }

    private fun setDragSortRecyclerView(
        currentFragment: Fragment,
        listContentsFilePath: String,
    ) {
        val context = currentFragment.context
            ?: return
        val dragSortList =
            makeDragSortList(
                listContentsFilePath
            )
        val recyclerView = dragSortDialogObj?.findViewById<RecyclerView>(
            R.id.drag_sort_dialog_recycler_view
        ) ?: return
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager
            = PreLoadLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val dragSortRecyclerAdapter = DragSortRecyclerAdapter(
            dragSortList.toMutableList()
        )
        recyclerView.adapter = dragSortRecyclerAdapter

        val itemDecoration: ItemDecoration =
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        recyclerView.addItemDecoration(itemDecoration)
        setItemTouchHelper(
            context,
            recyclerView,
            dragSortRecyclerAdapter,
        )

    }

    private fun makeDragSortList(
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

    private fun setItemTouchHelper(
        context: Context,
        recyclerView: RecyclerView,
        dragSortRecyclerAdapter: DragSortRecyclerAdapter,
    ){
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {

                override fun onMove(recyclerView: RecyclerView,
                                    viewHolder: RecyclerView.ViewHolder,
                                    target: RecyclerView.ViewHolder): Boolean {
                    val adapter = recyclerView.adapter as DragSortRecyclerAdapter
                    val from = viewHolder.bindingAdapterPosition
                    val to = target.bindingAdapterPosition
                    adapter.notifyItemMoved(from, to)
                    ListTool.switchList(
                        dragSortRecyclerAdapter.dratSortList,
                        from,
                        to,
                    )
                    return true
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    if(
                        direction != ItemTouchHelper.LEFT
                    ) return
                    val position = viewHolder.layoutPosition
                    dragSortRecyclerAdapter.notifyItemRemoved(position)
                    dragSortRecyclerAdapter.dratSortList.removeAt(position)
                }

                override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                    super.onSelectedChanged(viewHolder, actionState)

                    if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                        viewHolder?.itemView?.alpha = 0.5f
                    }
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)

                    viewHolder.itemView.alpha = 1.0f
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

}