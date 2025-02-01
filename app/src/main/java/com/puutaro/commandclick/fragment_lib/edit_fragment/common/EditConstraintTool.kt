package com.puutaro.commandclick.fragment_lib.edit_fragment.common

import android.content.Context
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.CheckTool
import com.puutaro.commandclick.component.adapter.EditConstraintListAdapter
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment_lib.edit_fragment.common.EditComponent.Template
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionAsyncCoroutine
import com.puutaro.commandclick.proccess.edit.setting_action.SettingActionManager
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList
import com.puutaro.commandclick.proccess.edit_list.config_settings.ListSettingsForEditList.LogErrLabel
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.str.PairListTool
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

//object EditConstraintTool {
//
//    object AlreadyUseTagListHandler {
//
//        suspend fun get(
//            alreadyUseTagList: MutableList<String>,
//            alreadyUseTagListMutex: Mutex,
//        ): List<String> {
//            return alreadyUseTagListMutex.withLock {
//                alreadyUseTagList
//            }.toList()
//
//        }
//    }
//
//
//    fun tagDuplicateErrHandler(
//        context: Context?,
//        tagJanre: Template.TagManager.TagGenre,
//        tagName: String,
//        alreadyUseTagList: List<String>,
//        mapListElInfo: String,
//        plusKeyToSubKeyConWhere: String,
//    ): String? {
////                        FileSystems.updateFile(
////                                File(UsePath.cmdclickDefaultAppDirPath, "stagDup.txt").absolutePath,
////                                listOf(
////                                        "alreadyUseTagList: ${alreadyUseTagList}",
////                                        "tagName: ${tagName}",
////                                ).joinToString("\n")
////                        )
//        if(
//            !alreadyUseTagList.contains(tagName)
//        ) return tagName
//        val tagKeyName =
//            Template.EditComponentKey.TAG.key
//        val spanTagGenre = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.lightBlue,
//            tagJanre.str
//        )
//        val spanTagNameKey = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errRedCode,
//            tagName
//        )
//        val spanWhereForLog = CheckTool.LogVisualManager.execMakeSpanTagHolder(
//            CheckTool.errBrown,
//            "${mapListElInfo} in ${plusKeyToSubKeyConWhere}"
//        )
//        val errSrcMessage =
//            "Forbidden to duplicate ${tagKeyName}: ${spanTagGenre}: ${spanTagNameKey}"
//        val errMessage =
//            "[${LogErrLabel.VIEW_LAYOUT.label}] ${errSrcMessage} about ${spanWhereForLog}"
//        LogSystems.broadErrLog(
//            context,
//            Jsoup.parse(errSrcMessage).text(),
//            errMessage
//        )
//        return null
//    }
//
//    suspend fun makeFrameVarNameToValueMap(
//        fragment: Fragment?,
//        fannelInfoMap: Map<String, String>,
//        setReplaceVariableMap: Map<String, String>?,
//        busyboxExecutor: BusyboxExecutor?,
//        settingActionAsyncCoroutine: SettingActionAsyncCoroutine,
//        editConstraintListAdapter: EditConstraintListAdapter?,
//        topLevelVarStrKeyNameList: List<String>?,
//        topVarNameToValueStrMap: Map<String, String?>?,
//        frameVarNameValueMap: Map<String, String>,
//        keyToSubKeyConWhere: String,
//        linearFrameKeyPairsListConSrc: String?,
//        srcTitle: String,
//        srcCon: String,
//        srcImage: String,
//        srcPosition: Int,
//    ):  Map<String, String> {
//        if (
//            linearFrameKeyPairsListConSrc.isNullOrEmpty()
//        ) return emptyMap()
//        return Template.ReplaceHolder.replaceHolder(
//            linearFrameKeyPairsListConSrc,
//            srcTitle,
//            srcCon,
//            srcImage,
//            srcPosition,
//        ).let {
//                linearFrameKeyPairsListConSrcWithReplace ->
//            if(
//                linearFrameKeyPairsListConSrcWithReplace.isNullOrEmpty()
//            ) return@let emptyMap()
//            val settingActionManager = SettingActionManager()
//            settingActionManager.exec(
//                fragment,
//                fannelInfoMap,
//                setReplaceVariableMap,
//                busyboxExecutor,
//                settingActionAsyncCoroutine,
//                topLevelVarStrKeyNameList,
//                (topVarNameToValueStrMap ?: emptyMap()) + frameVarNameValueMap,
//                linearFrameKeyPairsListConSrcWithReplace,
////                                        CmdClickMap.replace(
////                                                linearFrameKeyPairsListConSrcWithReplace,
////                                                frameVarNameValueMap,
////                                        )
//                keyToSubKeyConWhere,
//                editConstraintListAdapterArg = editConstraintListAdapter
//            ).let updateVarNameToValueMap@ {
//                if(
//                    it.isEmpty()
//                ) return@updateVarNameToValueMap emptyMap()
//                it
//            }
//        }
//    }
//
//    fun makeLinearFrameKeyPairsList(
//        linearFrameKeyPairsListCon: String?
//    ): List<Pair<String, String>> {
//        return CmdClickMap.createMap(
//            linearFrameKeyPairsListCon,
//            Template.typeSeparator
//        )
//    }
//
//    suspend fun makeContentsTagToKeyPairsList(
//        context: Context?,
//        contentsKeyValues: List<String>,
//        horizonVarNameToValueMap: Map<String, String>?,
//        srcTitle: String,
//        srcCon: String,
//        srcImage: String,
//        bindingAdapterPosition: Int,
//        mapListElInfo: String,
//    ):  List<
//            Pair<
//                    String,
//                    String?
//                    >
//            > {
//        return withContext(Dispatchers.IO) {
//            val jobList = contentsKeyValues.mapIndexed { index, contentsKeyPairsListConSrc ->
//                async {
//                    if (
//                        contentsKeyPairsListConSrc.isEmpty()
//                    ) return@async index to Pair(String(), null)
//                    val contentsKeyPairsListCon =
//                        Template.ReplaceHolder.replaceHolder(
//                            contentsKeyPairsListConSrc,
//                            srcTitle,
//                            srcCon,
//                            srcImage,
//                            bindingAdapterPosition,
//                        )?.let {
//                            CmdClickMap.replace(
//                                it,
//                                horizonVarNameToValueMap,
//                            )
//                        }
//                    val linearFrameKeyPairsList =
//                        makeLinearFrameKeyPairsList(
//                            contentsKeyPairsListCon,
//                        )
//                    val contentsTag =
//                        PairListTool.getValue(
//                            linearFrameKeyPairsList,
//                            Template.EditComponentKey.TAG.key,
//                        ) ?: String()
//                    when(
//                        contentsTag.isEmpty()
//                                && !contentsKeyPairsListCon.isNullOrEmpty()
//                    ) {
//                        true -> {
//                            ListSettingsForEditList.ViewLayoutCheck.isTagBlankErr(
//                                context,
//                                contentsTag,
//                                mapListElInfo,
//                                EditComponent.Template.TagManager.TagGenre.CONTENTS_TAG
//                            ).let {
//                                    isTagBlankErrJob ->
//                                if (!isTagBlankErrJob) return@let
//                                return@async index to Pair(
//                                    String(),
//                                    String()
//                                )
//                            }
//                        }
//                        else -> {}
//                    }
//                    index to Pair(
//                        contentsTag,
//                        contentsKeyPairsListCon
//                    )
//                }
//            }
//            jobList.awaitAll().sortedBy {
//                val index = it.first
//                index
//            }.map {
//                val linearFrameTagToKeyPairsList = it.second
//                linearFrameTagToKeyPairsList
//            }.filter {
//                it.first.isNotEmpty()
//                        && !it.second.isNullOrEmpty()
//            }
//        }
//    }
//
//    fun makeContentsFrameLayout(
//        context: Context,
//    ): FrameLayout {
//        val dp50 =
//            context.resources.getDimension(R.dimen.toolbar_layout_height)
//        val contentsLayout =
//            FrameLayout(context).apply {
//                layoutParams =
//                    ConstraintLayout.LayoutParams(
//                        0,
//                        dp50.toInt()
//                    )
//            }
//        val imageView =
//            AppCompatImageView(context).apply {
//                layoutParams =
//                    FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.MATCH_PARENT,
//                        FrameLayout.LayoutParams.MATCH_PARENT
//                    )
//            }
//        val textView =
//            OutlineTextView(context).apply {
//                layoutParams =
//                    FrameLayout.LayoutParams(
//                        FrameLayout.LayoutParams.WRAP_CONTENT,
//                        FrameLayout.LayoutParams.WRAP_CONTENT
//                    )
//            }
//        return contentsLayout.apply {
//            addView(imageView)
//            addView(textView)
//        }
//    }
//}