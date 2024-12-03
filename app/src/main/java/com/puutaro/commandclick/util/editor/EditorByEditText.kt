package com.puutaro.commandclick.util.editor

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.util.file.FileSystems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.util.LinkedList

object EditorByEditText {

    private var editorDialog: Dialog? = null

    fun byEditText(
        fragment: Fragment,
        dirPath: String,
        fileName: String,
        firstCon: String,
        broadcastIntent: Intent? = null
    ){
        val context = fragment.context
            ?: return
        val activity = fragment.activity
        editDialogStartProcess(fragment)
        editorDialog = Dialog(
            context
        )
        editorDialog?.setContentView(
            R.layout.editor_by_edit_text
        )
        val confirmTitleTextView =
            editorDialog?.findViewById<AppCompatTextView>(
                R.id.editor_by_edit_text_dialog_title
            )
        confirmTitleTextView?.text = "Edit: ${fileName}"
        val editorContentEditableView =
            editorDialog?.findViewById<AppCompatEditText>(
                R.id.editor_by_edit_text_dialog_edit_view
            )
        editorContentEditableView?.setText(firstCon)
        val textWatcher = UndoTextWatcher(
            fragment.context,
            editorDialog,
            firstCon
        )
        redoRedoButtonListener(
            textWatcher,
        )
        cancelButtonListener(fragment)
        saveButtonListener(
            fragment,
            textWatcher,
            dirPath,
            fileName,
            broadcastIntent
        )
        activity?.let {
            KeyboardVisibilityEvent.setEventListener(
                it,
                fragment.viewLifecycleOwner,
                KeyboardVisibilityEventListener {
                    isOpen ->
                    if (
                        editorDialog?.isShowing != true
                    ) return@KeyboardVisibilityEventListener
                    confirmTitleTextView?.isVisible = !isOpen
                }
            )
//            KeyboardVisibilityEvent.setEventListener(it) { isOpen ->
//
//                if (editorDialog?.isShowing != true) return@setEventListener
//                confirmTitleTextView?.isVisible = !isOpen
//            }
        }
        editorDialog?.setOnCancelListener {
            editDialogDismissProcess(fragment)
        }
        editorDialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
//        editorDialog?.window?.setGravity(
//            Gravity.BOTTOM
//        )
        editorDialog?.show()

    }

    private fun redoRedoButtonListener(
        textWatcher: UndoTextWatcher,
    ){
        val editUndoButton =
            this.editorDialog?.findViewById<AppCompatImageButton>(
                R.id.editor_by_edit_text_dialog_undo
            )
        val editRedoButton =
            this.editorDialog?.findViewById<AppCompatImageButton>(
                R.id.editor_by_edit_text_dialog_redo
            )
        val editorContentEditableView =
            this.editorDialog?.findViewById<AppCompatEditText>(
                R.id.editor_by_edit_text_dialog_edit_view
            )
        if(
            editorContentEditableView == null
            || editUndoButton == null
            || editRedoButton == null
        ) return
        editorContentEditableView.addTextChangedListener(textWatcher) // undo・redo機能を付けたTextWatcherをEditTextにセットする
        editUndoButton.setOnClickListener {
            val editText = editorContentEditableView.text
                ?: return@setOnClickListener
            textWatcher.undo(editText) // undoボタンを押してundoする
        }
        editRedoButton.setOnClickListener {
            val editText = editorContentEditableView.text
                ?: return@setOnClickListener
            textWatcher.redo(editText) // redoボタンを押してredoする
        }
    }


    private fun cancelButtonListener(
        fragment: Fragment,
    ){
        val confirmCancelButton =
            editorDialog?.findViewById<AppCompatImageButton>(
                R.id.editor_by_edit_text_dialog_cancel
            )
        confirmCancelButton?.setOnClickListener {
            editDialogDismissProcess(fragment)
        }
    }

    private fun saveButtonListener(
        fragment: Fragment,
        textWatcher: UndoTextWatcher,
        dirPath: String,
        fileName: String,
        broadcastIntent: Intent?
    ){
        val context = fragment.context
        val editView =
            editorDialog?.findViewById<AppCompatEditText>(
                R.id.editor_by_edit_text_dialog_edit_view
            )
        val saveButton =
            editorDialog?.findViewById<AppCompatImageButton>(
                R.id.editor_by_edit_text_dialog_save
            )
        textWatcher.saveButtonEnable = false
        saveButton?.setOnClickListener {
            val editableText = editView?.text
                ?: return@setOnClickListener
            FileSystems.writeFile(
                File(
                    dirPath,
                    fileName
                ).absolutePath,
                editableText.toString()
            )
            saveButton.imageTintList = fragment.context?.getColorStateList(R.color.gray_out)
            textWatcher.saveButtonEnable = false
            broadcastIntent?.let {
                context?.sendBroadcast(broadcastIntent)
            }
        }
    }

    private fun editDialogStartProcess(
        fragment: Fragment
    ){
        if(fragment !is EditFragment) return
        fragment.disableKeyboardFragmentChange = true
    }

    private fun editDialogDismissProcess(
        fragment: Fragment,
    ){
        editorDialog?.dismiss()
        editorDialog = null
        if(fragment !is EditFragment) return
        CoroutineScope(Dispatchers.Main).launch {
            withContext(Dispatchers.Main){
                delay(200)
                fragment.disableKeyboardFragmentChange = false
            }
        }
    }
}

private class EditEvent(// 置き換えられる文字列の開始位置
    private val beforePosition: Int,// 置き換えられる文字列
    private val before: CharSequence
) {
    private var afterPosition: Int = 0 // 置き換えた文字列の開始位置
    private var after: CharSequence = "" // 置き換えた文字列

    fun setAfter(afterPosition: Int, after: CharSequence) {
        this.afterPosition = afterPosition
        this.after = after
    }

    fun undo(editable: Editable) {
        editable.replace(afterPosition, afterPosition + after.length, before)
    }

    fun redo(editable: Editable) {
        editable.replace(beforePosition, beforePosition + before.length, after)
    }
}

private class UndoTextWatcher(
    private val context: Context?,
    editorDialog: Dialog?,
    firstCon: String,
) : TextWatcher {

    var undoing = false // undo・redo実行中かどうか
    var previousCon = firstCon
    val undos = LinkedList<EditEvent>() // undoリスト
    val redos = LinkedList<EditEvent>() // redoリスト
    val editUndoButton =
        editorDialog?.findViewById<AppCompatImageButton>(
            R.id.editor_by_edit_text_dialog_undo
        )
    val editRedoButton =
        editorDialog?.findViewById<AppCompatImageButton>(
            R.id.editor_by_edit_text_dialog_redo
        )
    val saveButton =
        editorDialog?.findViewById<AppCompatImageButton>(
            R.id.editor_by_edit_text_dialog_save
        )
    var saveButtonEnable = false

    fun redo(editable: Editable) {
        if (
            redos.isEmpty()
        ) return
        val event = redos.removeLast()
        undos.addLast(event)
        undoing = true
        try {
            event.redo(editable)
        } finally {
            undoing = false
        }
    }

    fun undo(editable: Editable) {
        if (undos.isEmpty()) return
        val event = undos.removeLast()
        redos.addLast(event)
        undoing = true
        try {
            event.undo(editable)
        } finally {
            undoing = false
        }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        // テキスト変更が起こる直前の処理（undo用の文字列保持など）
        if (undoing) return
        val event = EditEvent(start, s.subSequence(start, start + count)) // undoする文字列と位置を保持する
        undos.addLast(event)
        clearRedos()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        // テキスト変更が起きた直後の処理（undoしたときに置き換える箇所の取得・保持など）
        if (undoing) return
        val event = undos.getLast() // undoリストに最後に追加した要素
        event.setAfter(start, s.subSequence(start, start + count))
    }

    override fun afterTextChanged(s: Editable) {
        val hasUndos = !undos.isEmpty()
        when(hasUndos){
            false
            -> editUndoButton?.imageTintList = context?.getColorStateList(R.color.gray_out)
            else
            -> editUndoButton?.imageTintList = context?.getColorStateList(R.color.cmdclick_text_black)
        }
        val hasRedos = !redos.isEmpty()
        when(hasRedos){
            false
            -> editRedoButton?.imageTintList = context?.getColorStateList(R.color.gray_out)
            else
            -> editRedoButton?.imageTintList = context?.getColorStateList(R.color.cmdclick_text_black)
        }
        val currentCon = s.toString()
        val isDiffer = previousCon != currentCon
                && !saveButtonEnable
        if(isDiffer) {
            saveButton?.imageTintList = context?.getColorStateList(R.color.cmdclick_text_black)
            previousCon = currentCon
        }
    }

    private fun clearRedos() {
        while (!redos.isEmpty()) {
            redos.removeFirst()
        }
    }
}