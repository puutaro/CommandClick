package com.puutaro.commandclick.fragment_lib.command_index_fragment.list_view_lib.internet_button

import android.R
import android.content.Context
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.puutaro.commandclick.common.variable.UsePath
import com.puutaro.commandclick.common.variable.WebUrlVariables
import com.puutaro.commandclick.fragment.CommandIndexFragment
import com.puutaro.commandclick.fragment_lib.command_index_fragment.common.UrlTexter
import com.puutaro.commandclick.fragment_lib.command_index_fragment.variable.AutoCompleteThreshold
import com.puutaro.commandclick.util.Keyboard
import com.puutaro.commandclick.util.ReadText
import java.net.URLDecoder


class AutoCompleteEditTexter(
    private val cmdIndexFragment: CommandIndexFragment,
) {

    private val tabReplaceStr = "\t"

    private val context = cmdIndexFragment.context as Context
    private val binding = cmdIndexFragment.binding
    private val cmdSearchEditText = binding.cmdSearchEditText
    private val queryLimitStrLength = 50


    companion object {
        fun setAdapter(
            context: Context?,
            cmdSearchEditText: AutoCompleteTextView,
            currentAppDirPath: String,
            threshold: Int
        ) {
            if(context == null) return
            cmdSearchEditText.setAdapter(
                makeUrlComAdapter(
                    context,
                    currentAppDirPath,
                    threshold
                )
            )
            cmdSearchEditText.setThreshold(threshold)
        }

        private fun makeUrlComAdapter(
            context: Context,
            currentAppDirPath: String,
            threshold: Int
        ): ArrayAdapter<String> {
            return ArrayAdapter(
                context,
                R.layout.simple_list_item_1,
                makeCompleteListSource(
                    currentAppDirPath,
                    threshold
                )
            )
        }

        fun makeCompleteListSource(
            currentAppDirPath: String,
            threshold: Int,
            takeListNum: Int = 200,
        ):List<String> {
            val usedTitle = mutableSetOf<String>()
            val usedUrl = mutableSetOf<String>()
            return when(threshold) {
                AutoCompleteThreshold.ON.num -> {
                    return ReadText(
                        currentAppDirPath,
                        UsePath.cmdclickUrlHistoryFileName
                    ).txetToList()
                        .distinct()
                        .take(takeListNum)
                        .filter {
                            makeUrlHistoryList(
                                it,
                                usedTitle,
                                usedUrl,
                            )
                        }
                }
                AutoCompleteThreshold.OFF.num -> {
                    emptyList()
                }
                else -> {
                    emptyList()
                }
            }
        }
    }


    fun setItemClickListner(){
        cmdSearchEditText.setOnItemClickListener { parent, _, position, _ ->
            val selectedUrlSource = parent.getItemAtPosition(position) as String
            val selectedUrl = selectedUrlSource.split(tabReplaceStr).lastOrNull()
            val queryUrl = WebUrlVariables.queryUrl

            if (
                selectedUrl?.startsWith(queryUrl) != true
            ) {
                execUrlLaunch(selectedUrl)
                return@setOnItemClickListener
            }
            val decodedSelectedUrl =
                URLDecoder.decode(
                    selectedUrl.removePrefix(queryUrl),
                    "utf-8"
                )
            if(
                decodedSelectedUrl.length < queryLimitStrLength
            ) {
                cmdSearchEditText.setText(
                    decodedSelectedUrl
                )
                return@setOnItemClickListener
            }

            cmdSearchEditText.clearFocus()
            Keyboard.hiddenKeyboardForFragment(
                cmdIndexFragment
            )
            execUrlLaunch(selectedUrl)
        }
    }

    private fun execUrlLaunch(
        selectedUrl: String?
    ){
        cmdSearchEditText.clearFocus()
        Keyboard.hiddenKeyboardForFragment(
            cmdIndexFragment
        )
        UrlTexter.launch(
            context,
            cmdSearchEditText,
            selectedUrl
        )
    }

}

internal fun makeUrlHistoryList(
    historySourceRow: String,
    usedTitle: MutableSet<String>,
    usedUrl: MutableSet<String>,
): Boolean {
    val historySourceRowList = historySourceRow
        .split("\t")
    val duliEntryTitle = historySourceRowList
        .firstOrNull()
        ?: return false
    val duliEntryUrl = historySourceRowList
        .getOrNull(1)
        ?: return false
    return if(
        usedTitle.contains(duliEntryTitle)
        || usedUrl.contains(duliEntryUrl)
    ) {
        false
    } else {
        usedTitle.add(duliEntryTitle)
        usedUrl.add(duliEntryUrl)
        true
    }
}