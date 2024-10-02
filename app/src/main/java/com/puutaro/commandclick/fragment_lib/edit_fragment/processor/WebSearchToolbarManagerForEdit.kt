package com.puutaro.commandclick.fragment_lib.edit_fragment.processor

import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.widget.LinearLayoutCompat
import com.puutaro.commandclick.common.variable.variant.ReadLines
import com.puutaro.commandclick.fragment.EditFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button.GoogleSuggest
import com.puutaro.commandclick.proccess.js_macro_libs.toolbar_libs.EditToolbarSwitcher
import com.puutaro.commandclick.proccess.UrlTexter
import com.puutaro.commandclick.util.Keyboard

object WebSearchToolbarManagerForEdit {

    fun setKeyListener(
        cmdEditFragment: EditFragment
    ){

//        val binding = cmdEditFragment.binding
//        val webSearch = binding.webSearch
//        val cmdWebSearchEditText = webSearch.cmdWebSearchEditText
//        cmdWebSearchEditText.setOnKeyListener(object : View.OnKeyListener {
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
//                if (event.getAction() != KeyEvent.ACTION_DOWN ||
//                    keyCode != KeyEvent.KEYCODE_ENTER
//                ) return false
//                Keyboard.hiddenKeyboardForFragment(
//                    cmdEditFragment
//                )
//                if(
//                    cmdWebSearchEditText.text.isNullOrEmpty()
//                ) return false
//                UrlTexter.launch(
//                    cmdEditFragment,
//                    cmdWebSearchEditText,
//                    cmdWebSearchEditText.text.toString()
//                )
//                return false
//            }
//        })
    }

    fun setGoogleSuggest(
        cmdEditFragment: EditFragment
    ){
//        val binding = cmdEditFragment.binding
//        val webSearch = binding.webSearch
//        val cmdWebSearchEditText = webSearch.cmdWebSearchEditText
//        val googleSuggest = GoogleSuggest(
//            cmdEditFragment,
//            cmdWebSearchEditText
//        )
//        cmdWebSearchEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                if(
//                    !cmdWebSearchEditText.hasFocus()
//                ) return
//                val linearLayoutParam =
//                    cmdEditFragment.binding.editFragment.layoutParams as LinearLayoutCompat.LayoutParams
//                val editFragmentWeight = linearLayoutParam.weight
//                if(
//                    editFragmentWeight == ReadLines.LONGTH
//                ) {
//                    cmdWebSearchEditText.threshold = 100000
//                    return
//                }
//                try {
//                    googleSuggest.set(cmdWebSearchEditText.text)
//                } catch (e: Exception){
//                    print("pass")
//                }
//            }
//            override fun afterTextChanged(s: Editable?) {
//                if(!cmdWebSearchEditText.hasFocus()) return
//            }
//        })
    }

    fun setCancelListener(
        cmdEditFragment: EditFragment
    ){
//        val binding = cmdEditFragment.binding
//        val webSearch = binding.webSearch
//        val webSearchCancel = webSearch.webSearchCancel
//        webSearchCancel.setOnClickListener {
//                cancelView ->
//            EditToolbarSwitcher.switch(
//                cmdEditFragment,
//                EditLongPressType.NORMAL.jsMacro
//            )
//        }
    }
}