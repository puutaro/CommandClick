package com.puutaro.commandclick.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class BroadcastReceiverUrl: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val someValue = intent.getStringExtra("url")
        val extras = intent.getExtras();
        val ulrStr = extras?.getString("url")
        Toast.makeText(
            context,
            ulrStr,
            Toast.LENGTH_SHORT
        ).show()
//            val handler: Handler = Handler(Looper.getMainLooper())
//            handler.post {
//                val listener = context as? CommandIndexFragment.OnQueryTextChangedListner
//                listener?.onQueryTextChanged(
//                    someValue as String,
//                )
//            }
//        binding.terminalFragment.loadUrl(someValue as String)
//            text.setText(someValue)
    }
}