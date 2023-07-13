package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Canvas
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.edit.EditParameters
import com.puutaro.commandclick.component.adapter.DragSortRecyclerAdapter
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.edit_fragment.variable.EditTextSupportViewId
import com.puutaro.commandclick.util.FileSystems
import com.puutaro.commandclick.util.ReadText
import java.io.File


object DragSortListViewProducer {

    private val dataset: MutableList<String> = mutableListOf()
    private var alertDialog: AlertDialog? = null
    private val dragSortButtonLabel = "DST"

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
        val listContentsFilePath = ListContentsSelectSpinnerViewProducer.getListPath(
            elsbMap,
        )

        val fileObj = File(listContentsFilePath)
        val parentDir = fileObj.parent ?: String()
        val listFileName = fileObj.name
        FileSystems.createDirs(parentDir)

        val dragSortListViewButtonView = Button(context)
        dragSortListViewButtonView.layoutParams = linearParamsForDragSortListView
        dragSortListViewButtonView.id = currentId + EditTextSupportViewId.EDITABLE_GRID.id
        dragSortListViewButtonView.tag = "gridEdit${currentId + EditTextSupportViewId.EDITABLE_GRID.id}"
        dragSortListViewButtonView.text = dragSortButtonLabel
        dragSortListViewButtonView.setOnClickListener {
                buttonView ->
            Toast.makeText(
                context,
                "${elsbMap }\n${parentDir}/${listFileName}",
                Toast.LENGTH_LONG
            ).show()
            val buttonContext = buttonView.context
            val dragSortList = ReadText(
                parentDir,
                listFileName
            ).textToList().filter {
                it.trim().isNotEmpty()
            }
            val dragSortListView = createListView(
                buttonContext,
                dragSortList
            )
            alertDialog = AlertDialog.Builder(
                buttonContext
            )
                .setTitle("Sort by drag, or remove by swipe")
                .setView(dragSortListView)
                .setPositiveButton("OK", DialogInterface.OnClickListener {
                        dialog, which ->
                    FileSystems.writeFile(
                        parentDir,
                        listFileName,
                        dataset.joinToString("\n")
                    )
                })
                .setNegativeButton("NO", null)
                .show()
            alertDialog?.getButton(DialogInterface.BUTTON_POSITIVE)?.setTextColor(
                context?.getColor(android.R.color.black) as Int
            )
            alertDialog?.getButton(DialogInterface.BUTTON_NEGATIVE)?.setTextColor(
                context?.getColor(android.R.color.black) as Int
            )
            alertDialog?.window?.setGravity(Gravity.BOTTOM)
            alertDialog?.setOnCancelListener(object : DialogInterface.OnCancelListener {
                override fun onCancel(dialog: DialogInterface?) {
                    alertDialog?.dismiss()
                }
            })

        }
        return dragSortListViewButtonView
    }

    private fun createListView(
        context: Context,
        dragSortList: List<String>,
    ): RecyclerView {
        val recyclerView = RecyclerView(context)
        recyclerView.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        dataset.clear()
        dataset.addAll(dragSortList.toMutableList())
        val dragSortRecyclerAdapter = DragSortRecyclerAdapter(
            dataset
        )
//        dragSortRecyclerAdapter.itemClickListener = object : DragSortRecyclerAdapter.OnItemClickListener{
//            override fun onItemClick(holder: DragSortRecyclerAdapter.ViewHolder) {
//                alertDialog?.dismiss()
//                val _mesg = holder.textView.text
//                val _position = dataset.indexOf(_mesg)
//                Toast.makeText(
//                    context,
//                    "LongClick Pos=${_position} Mesg=\"${_mesg}\"",
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }

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
        return recyclerView
    }

    private fun setItemTouchHelper(
        context: Context,
        recyclerView: RecyclerView,
        dragSortRecyclerAdapter: DragSortRecyclerAdapter,
    ){
        var moveTimes = 0
        var fromPos = 0
        var toPos = 0
        var insertItem = String()
        val mIth = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
            ) {

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    when (isCurrentlyActive) {
                        true -> viewHolder.itemView.setBackgroundColor(
                            context.getColor(R.color.gray_out)
                        )
                        else -> {
                            activeStateFinishedHandler(
                                context,
                                viewHolder
                            )
                        }
                    }
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    if(
                        moveTimes == 0
                    ) {
                        fromPos = viewHolder.bindingAdapterPosition
                        insertItem = dataset[fromPos]
                    }
                    toPos = target.bindingAdapterPosition
                    moveTimes++
                    return true // true if moved, false otherwise
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    if(
                        direction != ItemTouchHelper.LEFT
                    ) return
                    val position = viewHolder.absoluteAdapterPosition
                    dataset.removeAt(position)
                    dragSortRecyclerAdapter.notifyDataSetChanged()

                }

                private fun activeStateFinishedHandler(
                    context: Context,
                    viewHolder: RecyclerView.ViewHolder,
                ){
                    viewHolder.itemView.setBackgroundColor(
                        context.getColor(R.color.white)
                    )
                    if(moveTimes > 0){
                        dataset.removeAt(fromPos)
                        dataset.add(toPos, insertItem)
                    }
                    moveTimes = 0
                    dragSortRecyclerAdapter.notifyDataSetChanged()
                }
            })
        mIth.attachToRecyclerView(recyclerView)
    }

}