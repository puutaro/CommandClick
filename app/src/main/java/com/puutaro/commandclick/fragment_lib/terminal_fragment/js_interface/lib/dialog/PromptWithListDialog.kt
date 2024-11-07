package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextWatcher
import android.text.style.CharacterStyle
import android.text.style.RelativeSizeSpan
import android.text.style.ReplacementSpan
import android.text.style.UpdateAppearance
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.card.MaterialCardView
import com.puutaro.commandclick.R
import com.puutaro.commandclick.common.variable.path.UsePath
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.FannelIcons
import com.puutaro.commandclick.component.adapter.PromptListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ButtonImageCreator
import com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog.PromptWithListDialog.StatisticsTool.makeStatisticsMapList
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.LogSystems
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.image_tools.BitmapTool.ImageTransformer
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime


class PromptWithListDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
)  {
    private var returnValue = String()
    private var onDialog = false

    companion object {
        private var promptDialogObj: Dialog? = null
        private const val listPrefix = "list"
        private const val listDirName = "${listPrefix}Text"
        private const val listTxtSuffix = ".txt"
        private const val sectionSeparator = ','
        private const val keySeparator = '|'
        const val valueSeparator = '?'
        const val switchOn = "ON"
        const val switchOff = "OFF"
        const val onSystemkeyOpenModeStr = "onSystemKeyOpenMode"

        fun exit(){
            StatisticsTool.exit()
        }
        enum class PromptWithTextMapKey {
            list,
            editText,
//            background,
            title,
            extra,
        }

        enum class PromptTitleKey {
            maxLines,
        }

        enum class PromptEditTextKey {
            default,
            hint,
            shellPath,
            fannelPath,
            repValCon,
            disableListBind,
            visible,
            onFocus,
        }
        enum class PromptListVars {
            saveTag,
            concatFilePathList,
            concatList,
            onInsertByClick,
            onDismissByClick,
            visible,
            limit,
            disableUpdate,
            focusItemTitles,
        }

        enum class PromptExtraKey {
            removeFilePaths,
            onKeyOpenMode,
        }


//        object PromptBackground {
//            enum class Key {
//                type
//            }
//
//            enum class Type{
//                transparent
//            }
//        }

        object PromptMapList {

            const val promptListSeparator = '\t'

            enum class PromptListKey(
                val key: String
            ){
                TITLE("title"),
                ICON("icon")
            }

            fun makePromptMapList(
                fannelName: String,
                setReplaceVariablesMap: Map<String, String>?,
                promptListFile: File?,
                promptListMap: Map<String, String>,
                filterString: String,
                listLimit: Int?,
            ): MutableList<Map<String, String?>> {
                val mainMapListSrc = when(promptListFile == null) {
                    true -> emptyList()
                    else -> ReadText(
                        promptListFile.absolutePath
                    ).readText().let {
                        SetReplaceVariabler.execReplaceByReplaceVariables(
                            it,
                            setReplaceVariablesMap,
                            fannelName,
                        )
                    }.split("\n").map { line ->
                        makeMap(line)
                    }
                }
                val extraMapListFromFileSrc = makeExtraMapList(
                    fannelName,
                    setReplaceVariablesMap,
                    promptListMap.get(PromptListVars.concatFilePathList.name)?.let {
                        QuoteTool.splitBySurroundedIgnore(
                            it,
                            valueSeparator
                        )
                    }
                )
                val titleKey = PromptListKey.TITLE.key
                val concatMapListFromFile = concatTwoMapList(
                    mainMapListSrc,
                    extraMapListFromFileSrc
                )
                val extraMapList = makeExtraMapListFromCon(
                    promptListMap.get(PromptListVars.concatList.name)?.let {
                        QuoteTool.trimBothEdgeQuote(it)
                    }
                )
                val promptMapList = concatTwoMapList(
                    concatMapListFromFile,
                    extraMapList
                )
//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lpromptList.txt").absolutePath,
//                    listOf(
//                        "mainMapListSrc: ${mainMapListSrc}",
//                        "extraMapListFromFileSrc: ${extraMapListFromFileSrc}",
//                        "concatMapListFromFile: ${concatMapListFromFile}",
//                        "extraMapList: ${extraMapList}",
//                        "promptMapList: ${promptMapList}",
//                    ).joinToString("\n\n----------\n\n")
//                )
                return when (filterString.isEmpty()) {
                    true -> promptMapList
                    else -> promptMapList.distinct().filter { lineMap ->
                        val title = lineMap.get(titleKey)
                            ?: return@filter true
                        Regex(
                            filterString
                                .lowercase()
                                .replace("\n", "")
                        ).containsMatchIn(
                            title.lowercase()
                        )
                    }
                }.let { listSrc ->
                    when (listLimit == null) {
                        true -> listSrc
                        else -> listSrc.take(listLimit)
                    }.filter {
                        lineMap ->
                        !lineMap.get(
                            PromptListKey.TITLE.key
                        ).isNullOrEmpty()
                    }.toMutableList()
                }
            }

            private fun concatTwoMapList(
                baseMapListSrc: List<Map<String, String?>>,
                addMapList: List<Map<String, String?>>
            ): List<Map<String, String?>> {
                val titleKey = PromptListKey.TITLE.key
                val iconKey = PromptListKey.ICON.key
                val baseMapList = baseMapListSrc.filter {
                        baseLineMap ->
                    val baseTitle = baseLineMap.get(titleKey)
                    val baseIconStr = baseLineMap.get(iconKey)
                        ?: String()
                    !addMapList.any {
                            addMapListFromFileSrcMap ->
                        val addTitleFromFile = addMapListFromFileSrcMap.get(titleKey)
                        val isTitleEqual =
                            baseTitle == addTitleFromFile
                        val addIconStr = addMapListFromFileSrcMap.get(iconKey)
                            ?: String()
                        val isIconEqual =
                            baseIconStr == addIconStr
                        isTitleEqual && !isIconEqual
                    }
                }

                val concatMapList = baseMapList + addMapList.filter {
                        addLineMap ->
                    val addTitle = addLineMap.get(titleKey)
                    !baseMapList.any { baseMap ->
                        val baseTitle = baseMap.get(titleKey)
                        val isTitleEqual =
                            baseTitle == addTitle
                        isTitleEqual
                    }
                }
                return concatMapList
            }

            private fun makeExtraMapList(
                fannelName: String,
                setReplaceVariablesMap: Map<String, String>?,
                listConcatFilePathList: List<String>?,
            ): List<Map<String, String?>> {
                if (
                    listConcatFilePathList.isNullOrEmpty()
                ) return emptyList()
                return listConcatFilePathList.map {
                    ReadText(it).readText().let {
                        SetReplaceVariabler.execReplaceByReplaceVariables(
                            it,
                            setReplaceVariablesMap,
                            fannelName,
                        )
                    }.split("\n").map makeMap@ {
                            line ->
                        makeMap(line)
                    }
                }.flatten().filter { it.isNotEmpty() }
            }

            private fun makeExtraMapListFromCon(
                concatList: String?,
            ): List<Map<String, String?>> {
                if (
                    concatList.isNullOrEmpty()
                ) return emptyList()
                return concatList.split(valueSeparator).map {
                        line ->
                    makeMap(line)
                }.filter { it.isNotEmpty() }

            }

            private fun makeMap(line: String): Map<String, String?> {
                val titleToIconList = line.split(promptListSeparator)
                if(
                    titleToIconList.isEmpty()
                ) return emptyMap()
                val title = titleToIconList.first()
                val icon = titleToIconList.getOrNull(1)
                return mapOf(
                    PromptListKey.TITLE.key to title,
                    PromptListKey.ICON.key to icon
                )
            }
        }
    }

    fun create(
        fannelPath: String,
        title: String,
        promptConfigMapConSrc: String,
    ): String {
        val fannelFile = File(fannelPath)
        if(
            !fannelFile.isFile
        ) {
            LogSystems.stdSErr("invalid fannel path: ${fannelFile.absolutePath}")
            return String()
        }
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        onDialog = true
        returnValue = String()
        val setReplaceVariablesMap = SetReplaceVariabler.makeSetReplaceVariableMapFromSubFannel(
            terminalFragment.context,
            fannelPath
        )
        val promptConfigMapCon = SetReplaceVariabler.execReplaceByReplaceVariables(
            promptConfigMapConSrc,
            setReplaceVariablesMap,
            fannelFile.name
        )
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        terminalFragment,
                        fannelPath,
                        setReplaceVariablesMap,
                        title,
                        promptConfigMapCon,
                    )
                } catch (e: Exception){
                    LogSystems.stdErr(
                        terminalFragment.context,
                        e.toString()
                    )
                    simpleExitDialog()
                }
            }
            withContext(Dispatchers.IO) {
                for (i in 1..60 * 5) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnValue
    }


    private suspend fun execCreate(
        terminalFragment: TerminalFragment,
        fannelPath: String,
        setReplaceVariablesMap: Map<String, String>?,
        title: String,
        promptConfigMapCon: String,
    ) {
        val execCreateStart = LocalDateTime.now()
        val context = terminalFragment.context
            ?: return Unit.also {
                simpleExitDialog()
            }

        val promptConfigMap =
            CmdClickMap.createMap(
                promptConfigMapCon,
                sectionSeparator,
            ).toMap()
        val promptExtraMap = promptConfigMap.get(PromptWithTextMapKey.extra.name)?.let {
                CmdClickMap.createMap(
                    it,
                    keySeparator
                ).toMap()
            }
//        val promptBkMap = promptConfigMap.get(PromptWithTextMapKey.background.name)?.let {
//            CmdClickMap.createMap(
//                it,
//                keySeparator
//            ).toMap()
//        }
//        val isTransparent = true
//        promptBkMap?.get(
//            PromptBackground.Key.type.name
//        ) == PromptBackground.Type.transparent.name
        val isWhiteBackground = true
//        let {
//            val randEndNum = 5
//            (1..randEndNum).random() % randEndNum == 0
//        }
//            when(isTransparent) {
//                true -> false
//                else -> {
//                    val randEndNum = 5
//                    (1..randEndNum).random() % randEndNum == 0
//                }
//            }
        promptDialogObj = Dialog(
                context,
                R.style.BottomSheetDialogThemeWithNoDimm
//            R.style.extraMenuDialogStyle,
//                R.style.BottomSheetDialogThemeWithLightDimm
            )
//        when(isTransparent) {
//            true -> Dialog(
//                context,
//                R.style.BottomSheetDialogThemeWithNoDimm
////            R.style.extraMenuDialogStyle,
////                R.style.BottomSheetDialogThemeWithLightDimm
//            )
//            else -> Dialog(
//                context,
////            R.style.extraMenuDialogStyle,
//                R.style.BottomSheetDialogTheme
//            )
//        }
        promptDialogObj?.setContentView(
            R.layout.prompt_list_dialog_layout
        )
        val holderConstraint =
            promptDialogObj?.findViewById<ConstraintLayout>(
                R.id.prompt_list_dialog_constraint
            ) ?: return Unit.also {
                simpleExitDialog()
            }
        val bkRelative =
            promptDialogObj?.findViewById<RelativeLayout>(
                R.id.prompt_list_dialog_bk_relative
            ) ?: return Unit.also {
                simpleExitDialog()
            }


        promptExtraMap?.get(PromptExtraKey.removeFilePaths.name)?.split(
            valueSeparator
        )?.forEach { removeFilePath ->
            FileSystems.removeFiles(
                removeFilePath,
            )
        }

        val titleMap = CmdClickMap.createMap(
                promptConfigMap.get(PromptWithTextMapKey.title.name),
                keySeparator
            ).toMap()
        val promptListView = promptDialogObj?.findViewById<RecyclerView>(
                R.id.prompt_list_dialog_list_view
            )  ?: return Unit.also {
            simpleExitDialog()
        }
        val firstTitleGradColorsStrList = firstTitleGradColorStrList(
                isWhiteBackground
            )
        val promptTitleStart = LocalDateTime.now()
        val promptListTitleView = makePromptTitle(
                promptDialogObj,
                title,
                firstTitleGradColorsStrList,
                titleMap,
            )
        val promptTitleEnd = LocalDateTime.now()
        val editTextMap = CmdClickMap.createMap(
                promptConfigMap.get(PromptWithTextMapKey.editText.name),
                keySeparator
            ).toMap()
        val promptListMap = CmdClickMap.createMap(
                promptConfigMap.get(PromptWithTextMapKey.list.name),
                keySeparator
            ).toMap()
        val disableUpdate = promptListMap.get(
                PromptListVars.disableUpdate.name
            ) == switchOn
        val fannelDirPath = CcPathTool.getMainFannelDirPath(fannelPath)
        val promptListFile = withContext(Dispatchers.IO) {
            val listDirPath = "${fannelDirPath}/${listDirName}"
            val saveTag = promptListMap.get(PromptListVars.saveTag.name)
            makeListTextFileName(
                saveTag,
            )?.let {
                File(listDirPath, it)
            }
        }

        val defaultText = withContext(Dispatchers.IO) {
            val defaultTextSrc = editTextMap.get(
                PromptEditTextKey.default.name
            ) ?: String()
            when (defaultTextSrc.isEmpty()) {
                true -> EditTextMakerForPromptList.makeTextByShell(
                    terminalFragment,
                    editTextMap
                )

                else -> defaultTextSrc
            }
        }
        val disableListBind = editTextMap.get(
            PromptEditTextKey.disableListBind.name
        ) == switchOn
        val editTextVisible = editTextMap.get(
            PromptEditTextKey.visible.name
        ) != switchOff
        val listLimit =
            promptListMap.get(PromptListVars.limit.name)?.let {
                try{
                    it.toInt()
                } catch (e: Exception){
                    null
                }
            }
        val focusItemTitlesList =
            promptListMap.get(PromptListVars.focusItemTitles.name)?.split(valueSeparator)

        val listVisible =
            promptListMap.get(PromptListVars.visible.name) != switchOff
        val filterText = when(
            !listVisible
                    || disableListBind
                    || !editTextVisible
        ){
            true -> String()
            else -> defaultText ?: String()
        }
        val promptlistStart = LocalDateTime.now()
        val promptList: MutableList<Map<String, String?>> = //mutableListOf()
            PromptMapList.makePromptMapList(
                File(fannelPath).name,
                setReplaceVariablesMap,
                promptListFile,
                promptListMap,
                filterText,
                listLimit,
            )
        val promptWindowSetStart = LocalDateTime.now()
        val bkImageView = promptDialogObj?.findViewById<AppCompatImageView>(
            R.id.prompt_list_dialog_list_bk_image
        ) ?: return Unit.also {
            simpleExitDialog()
        }
        promptDialogObj?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            setBackgroundDrawable(
                ColorDrawable(Color.TRANSPARENT)
            )
            setGravity(
                Gravity.BOTTOM
            )
        }
        val promptWindowSetEnd = LocalDateTime.now()
        promptDialogObj?.show()
        FileSystems.writeFile(
            File(UsePath.cmdclickDefaultAppDirPath, "promptTime.txt").absolutePath,
            listOf(
                "execCreateStart: ${execCreateStart}",
                "promptTitleStart: ${promptTitleStart}",
                "promptTitleEnd: ${promptTitleEnd}",
                "promptlistStart: ${promptlistStart}",
                "promptWindowSetStart: ${promptWindowSetStart}",
                "promptWindowSetEnd: ${promptWindowSetEnd}",
            ).joinToString("\n")
        )
        CoroutineScope(Dispatchers.Main).launch{
            when (
                isWhiteBackground
            ) {
                true -> bkImageView.setImageDrawable(
                    AppCompatResources.getDrawable(context, R.drawable.white_floor)
                )
                else -> {
                    val bitmap = withContext(Dispatchers.IO) {
                        val colorStrList = CmdClickColorStr.entries.map { it.str }
                        val colorIntArray = listOf(
                            colorStrList.random(),
                            colorStrList.random(),
                            colorStrList.random()
                        ).map {
                            Color.parseColor(it)
                        }.toIntArray()
                        BitmapTool.GradientBitmap.makeGradientBitmap2(
                            600,
                            1200,
                            colorIntArray,
                            BitmapTool.GradientBitmap.GradOrient.BOTH
                        )
                    }
                    bkImageView.apply {
                        setImageBitmap(bitmap)
                    }
                }
            }
            originalDimmEffect(
                bkImageView
            )
        }
        CoroutineScope(Dispatchers.Main).launch {
            val promptEditText = withContext(Dispatchers.Main) {
                EditTextMakerForPromptList.make(
                    promptDialogObj,
                    editTextMap,
                    filterText,
                    editTextVisible,
                )
            }
            val promptListAdapter = withContext(Dispatchers.Main) {
                PromptListAdapter(
                    context,
                    promptList,
                    isWhiteBackground,
                    focusItemTitlesList,
                )
            }
            withContext(Dispatchers.Main) {
                promptDialogObj?.setOnCancelListener {
                    exitDialog(
                        fannelDirPath,
                        holderConstraint,
                        bkRelative,
                        promptListView,
                        String(),
                        null,
                        promptListFile,
                        listLimit,
                        disableUpdate,
                    )
                }
            }
            withContext(Dispatchers.Main) {
                promptListView?.apply {
                    isVisible = listVisible
                    adapter = promptListAdapter
                    layoutManager = PreLoadLayoutManager(
                        context,
                        true,
                    )
                }
            }
            withContext(Dispatchers.Main) {
                editTextKeyListener(
                    fannelDirPath,
                    holderConstraint,
                    bkRelative,
                    promptListView,
                    promptEditText,
                    promptListFile,
                    listLimit,
                    disableUpdate
                )
            }
            withContext(Dispatchers.Main) {
                setPromptEditText(
                    File(fannelPath).name,
                    setReplaceVariablesMap,
                    promptEditText,
                    promptListFile,
                    promptListMap,
                    promptListView,
                    promptListAdapter,
                    disableListBind,
                    listLimit,
                )
            }
            withContext(Dispatchers.Main) {
                setItemClickListener(
                    fannelDirPath,
                    holderConstraint,
                    bkRelative,
                    promptListView,
                    promptListAdapter,
                    promptEditText,
                    promptListMap,
                    promptListFile,
                    listLimit,
                    disableUpdate,
                )
            }
//            withContext(Dispatchers.Main){
//                scrollToBottom(
//                    promptListView,
//                    promptListAdapter,
//                )
//            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            val statisticsTitleList = withContext(Dispatchers.IO) {
                makeStatisticsMapList(
                    fannelDirPath,
                    promptListFile?.name?.let {
                        CcPathTool.trimAllExtend(it)
                    },
                    promptList,
                )
            }
            withContext(Dispatchers.Main) {
                StatisticsTool.displayStatisticsBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    statisticsTitleList,
                    firstTitleGradColorsStrList,
                    isWhiteBackground,
                )
            }
        }
        CoroutineScope(Dispatchers.Main).launch {
            if(
                promptListView == null
                || promptListTitleView == null
                )
                return@launch
            val promptListAdapter = promptListView.adapter as? PromptListAdapter
            val promptMapList = promptListAdapter?.prompMapList
            val lastIndex = promptMapList?.lastIndex ?: 0
            promptListView.viewTreeObserver?.addOnGlobalLayoutListener(object :
                OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val viewTreeObserver = this
                    CoroutineScope(Dispatchers.Main).launch ajustPosition@ {
                        withContext(Dispatchers.Main) {
                            promptListView.viewTreeObserver.removeOnGlobalLayoutListener(
                                viewTreeObserver
                            )
                        }
                        val holder = withContext(Dispatchers.IO) {
                            for (i in 1..10) {
                                val holderSrc = 
                                    promptListView.findViewHolderForAdapterPosition(lastIndex) as? PromptListAdapter.PromptListViewHolder
                                if(
                                    holderSrc != null
                                    ) return@withContext holderSrc
                                delay(200)
//                                FileSystems.writeFile(
//                                    File(
//                                        UsePath.cmdclickDefaultAppDirPath,
//                                        "lPosit_dialog_getLstIndex2.txt"
//                                    ).absolutePath,
//                                    listOf(
//                                        "i: ${i}",
//                                        "lastIndex: ${lastIndex}",
//                                        "loadLastIndex: ${loadLastIndex}",
//                                    ).joinToString("\n")
//                                )
                                continue
                            }
                            null
                        } ?: return@ajustPosition
                        holder.itemView.viewTreeObserver.addOnGlobalLayoutListener(object :
                            OnGlobalLayoutListener {
                            override fun onGlobalLayout() {
                                val listener = this
                                CoroutineScope(Dispatchers.Main).launch execAjustPosition@ {
                                    withContext(Dispatchers.IO){
                                        delay(200)
                                    }
                                    withContext(Dispatchers.Main) {
                                        holder.itemView.viewTreeObserver.removeOnGlobalLayoutListener(
                                            listener
                                        )
                                    }
                                    val windowLocation = withContext(Dispatchers.IO) {
                                        val windowLocationSrc = IntArray(2)
                                        holder.promptListAdapterLinear.getLocationInWindow(windowLocationSrc)
                                        windowLocationSrc
                                    }

                                    val recyclerLastYPosition = withContext(Dispatchers.IO) {
                                        windowLocation[1]
                                    }
                                    val textY = withContext(Dispatchers.IO) {
                                        promptListTitleView.y + promptListTitleView.height
                                    }
                                    if(
                                        recyclerLastYPosition > textY
                                        ) return@execAjustPosition

                                    val searchCardView = withContext(Dispatchers.Main) {
                                        promptDialogObj?.findViewById<MaterialCardView>(
                                            R.id.prompt_list_dialog_search_edit_cardview
                                        )
                                    }
//                                    val searchCardViewVerticalMarginKeyOpenDp = withContext(Dispatchers.IO){
//                                        context.resources.getDimension(
//                                            R.dimen.prompt_list_dialog_vertical_search_cardview_key_open_margin
//                                        ).toInt()
//                                    }
                                    withContext(Dispatchers.Main) {
                                        promptListView.setAutofillHints(onSystemkeyOpenModeStr)
                                        KeyboardHandler.setToKeyOpen(
                                            promptListTitleView,
                                            promptListView,
//                                            searchCardViewVerticalMarginKeyOpenDp,
                                            searchCardView
                                        )
                                    }
//                                    FileSystems.writeFile(
//                                        File(
//                                            UsePath.cmdclickDefaultAppDirPath,
//                                            "lPosit_dialog.txt"
//                                        ).absolutePath,
//                                        listOf(
//                                            "title: ${title}",
//                                            "x :" + windowLocation[0] + " , y : " + windowLocation[1],
//                                            "textY: ${promptListTitleView.y + promptListTitleView.height}"
//                                        ).joinToString("\n")
//                                    )
                                }
                            }
                        })
                    }
                }
            })
        }
        KeyboardHandler.handle(
            terminalFragment,
            promptDialogObj,
            promptListTitleView,
            promptListView,
            promptExtraMap,
        )
    }

    private fun firstTitleGradColorStrList(
        isWhiteBackgrond: Boolean
    ): List<String> {
        val whiteColorStr = "#ffffff"
        val colorList = listOf(
            CmdClickColorStr.LIGHT_GREEN.str,
            CmdClickColorStr.THICK_AO.str,
            CmdClickColorStr.BLUE.str,
            CmdClickColorStr.SKERLET.str,
            CmdClickColorStr.YELLOW.str,
            CmdClickColorStr.WHITE_GREEN.str,
            CmdClickColorStr.GREEN.str,
            CmdClickColorStr.YELLOW_GREEN.str,
            CmdClickColorStr.BLACK_AO.str,
            CmdClickColorStr.WATER_BLUE.str,
            CmdClickColorStr.PURPLE.str,
            CmdClickColorStr.ORANGE.str,
            CmdClickColorStr.BROWN.str,
        )
        val color1 = colorList.random()
        val alreadyColorList: MutableList<String> = mutableListOf()
        alreadyColorList.add(color1)
        val color2 = let {
            if(
                !isWhiteBackgrond
            ) return@let whiteColorStr
            colorList.filter{
                it != color1
            }.random()
        }
        alreadyColorList.add(color2)
        val color3 = let {
            colorList.filter {
                !alreadyColorList.contains(it)
            }.random()
        }
        return listOf(
            color1,
            color2,
            whiteColorStr,
            color3
        ) // Define your gradient colors
    }

//    private suspend fun scrollToBottom(
//        promptListView: RecyclerView?,
//        promptListAdapter: PromptListAdapter,
//    ){
//        delay(200)
//        promptListView?.scrollToPosition(promptListAdapter.itemCount - 1)
//    }

    private fun originalDimmEffect(
        imageView: AppCompatImageView?
    ){
        val goalAlpha = 0.8f
        val loopTimes = 2
        val plusAlpha = goalAlpha / loopTimes
        imageView?.apply {
            alpha = 0f
            CoroutineScope(Dispatchers.IO).launch {
                for (i in 1..loopTimes) {
                    withContext(Dispatchers.IO) {
                        delay(200)
                    }
                    withContext(Dispatchers.Main) {
                        alpha += plusAlpha
                    }
                }
            }
        }
    }

    private fun makePromptTitle(
        promptDialogObj: Dialog?,
        title: String,
        firstTitleGradColorsStrList: List<String>,
        titleMap: Map<String, String>?,
    ): OutlineTextView? {
        val maxLinesInt = titleMap?.get(
            PromptTitleKey.maxLines.name
        )?.let {
            try{
                it.toInt()
            } catch (e: Exception){
                null
            }
        } ?: Integer.MAX_VALUE
        return promptDialogObj?.findViewById<OutlineTextView>(
            R.id.prompt_list_dialog_list_title
        )?.apply {
            val titleStr = title.map {
                "${it}"
            }.joinToString(" ")
            val ss1 = SpannableString(titleStr)
            ss1.setSpan(
                RelativeSizeSpan(2f),
                0,
                1,
                0
            )
            val colors = firstTitleGradColorsStrList.map {
                Color.parseColor(it)
            }.toIntArray()
            val angle = (220..320).random() //45 // Set the gradient angle
//            FileSystems.updateFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "lgradient.txt").absolutePath,
//                listOf(
//                    "positions: ${positions.map { it }.joinToString("| ")}, angle: ${angle}, color1: ${color1}, color2: ${color2}"
//                ).joinToString("\n")
//            )
            val gradientSpan = LinearGradientSpan(colors, null, angle)
            ss1.setSpan(gradientSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            val radius = 10f
            val dx = 10f
            val dy = 10f
            val shadowColor = Color.WHITE
            val shadowSpan = ShadowSpan(radius, dx, dy, shadowColor)
            ss1.setSpan(shadowSpan, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)

            text = ss1
            setFillColor(R.color.ao)
            setStrokeColor(CmdClickColor.WHITE.id)
            outlineWidthSrc = 3
            maxLines = maxLinesInt
                viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    // Remove listener because we don't want this called before _every_ frame
                    viewTreeObserver?.removeOnPreDrawListener(this)
                    minHeight = height + lineHeight
                    return true // true because we don't want to skip this frame
                }
            })
        }
    }

    private object KeyboardHandler {

        fun handle(
            terminalFragment: TerminalFragment,
            promptDialogObj: Dialog?,
            promptListTitleView: AppCompatTextView?,
            promptListView: RecyclerView?,
            promptExtraMap: Map<String, String>?,
        ){
            val context = terminalFragment.context
                ?: return
            val searchCardView = promptDialogObj?.findViewById<MaterialCardView>(
                R.id.prompt_list_dialog_search_edit_cardview
            )
//            val cancelImageView = promptDialogObj?.findViewById<AppCompatImageView>(
//                R.id.prompt_list_dialog_cancel
//            )
//            val okImageView = promptDialogObj?.findViewById<AppCompatImageView>(
//                R.id.prompt_list_dialog_ok
//            )
            val searchCardViewVerticalMarginNormalDp = context.resources.getDimension(
                R.dimen.prompt_list_dialog_vertical_search_cardview_margin
            ).toInt()
//            okImageView?.isVisible = editTextVisible
//            val searchCardViewVerticalMarginKeyOpenDp = context.resources.getDimension(
//                R.dimen.prompt_list_dialog_vertical_search_cardview_key_open_margin
//            ).toInt()
            val isKeyOpenMode = promptExtraMap?.get(
                PromptExtraKey.onKeyOpenMode.name
            ) == switchOn
            if(isKeyOpenMode){
                setToKeyOpen(
                    promptListTitleView,
                    promptListView,
//                    searchCardViewVerticalMarginKeyOpenDp,
                    searchCardView,
//                    cancelImageView,
//                    okImageView,
                )
            }
            CoroutineScope(Dispatchers.Main).launch {
                withContext(Dispatchers.IO){
                    delay(1000)
                }
            }
            terminalFragment.activity?.let {
                KeyboardVisibilityEvent.setEventListener(
                    it,
                    terminalFragment.viewLifecycleOwner,
                    KeyboardVisibilityEventListener {
                            isOpen ->
                        if(
                            !terminalFragment.isVisible
                            || promptDialogObj?.isShowing != true
                        ) return@KeyboardVisibilityEventListener
                        when(isOpen){
                            true -> {
                                setToKeyOpen(
                                    promptListTitleView,
                                    promptListView,
//                                    searchCardViewVerticalMarginKeyOpenDp,
                                    searchCardView,
//                                    cancelImageView,
//                                    okImageView,
                                )
//                                promptListTitleView?.alpha = 0.4f
//                                val constraintLayout = promptListView?.layoutParams as? ConstraintLayout.LayoutParams
//                                constraintLayout?.apply {
//                                    topToTop = ConstraintSet.PARENT_ID
//                                    topToBottom = ConstraintLayout.LayoutParams.UNSET
//                                    bottomMargin = searchCardViewVerticalMarginKeyOpenDp
//                                }
//                                promptListView?.layoutParams = constraintLayout
//
//                                val cardConstraintLayout = searchCardView?.layoutParams as? ConstraintLayout.LayoutParams
//                                cardConstraintLayout?.setMargins(
//                                    cardConstraintLayout.marginStart,
//                                    cardConstraintLayout.topMargin,
//                                    cardConstraintLayout.marginEnd,
//                                    0
//                                )
//                                searchCardView?.layoutParams = cardConstraintLayout
//                                cancelImageView?.isVisible = false
//                                okImageView?.isVisible = false

                            }
                            else -> {
                                val systemKeyOpenModeSign = promptListView?.autofillHints?.firstOrNull() ?: String()
                                if(
                                    isKeyOpenMode
                                    || systemKeyOpenModeSign == onSystemkeyOpenModeStr
                                    ) return@KeyboardVisibilityEventListener
                                promptListTitleView?.alpha = 1f
                                val constraintLayout = promptListView?.layoutParams as? ConstraintLayout.LayoutParams
                                constraintLayout?.apply {
                                    topToTop = ConstraintLayout.LayoutParams.UNSET
                                    topToBottom = R.id.prompt_list_dialog_list_title
                                    bottomMargin = searchCardViewVerticalMarginNormalDp
                                }
                                promptListView?.layoutParams = constraintLayout

                                val cardConstraintLayout = searchCardView?.layoutParams as? ConstraintLayout.LayoutParams
                                cardConstraintLayout?.setMargins(
                                    cardConstraintLayout.marginStart,
                                    cardConstraintLayout.topMargin,
                                    cardConstraintLayout.marginEnd,
                                    searchCardViewVerticalMarginNormalDp
                                )
//                                cancelImageView?.isVisible = true
//                                okImageView?.isVisible = true && editTextVisible
                            }
                        }
                    }
                )
            }
        }

        fun setToKeyOpen(
            promptListTitleView: AppCompatTextView?,
            promptListView: RecyclerView?,
//            searchCardViewVerticalMarginKeyOpenDp: Int,
            searchCardView: MaterialCardView?,
        ){
            promptListTitleView?.alpha = 0.4f
            val constraintLayout = promptListView?.layoutParams as? ConstraintLayout.LayoutParams
            constraintLayout?.apply {
                topToTop = ConstraintSet.PARENT_ID
                topToBottom = ConstraintLayout.LayoutParams.UNSET
//                bottomMargin = searchCardViewVerticalMarginKeyOpenDp
            }
            promptListView?.layoutParams = constraintLayout

            val cardConstraintLayout = searchCardView?.layoutParams as? ConstraintLayout.LayoutParams
            cardConstraintLayout?.setMargins(
                cardConstraintLayout.marginStart,
                cardConstraintLayout.topMargin,
                cardConstraintLayout.marginEnd,
                0
            )
            searchCardView?.layoutParams = cardConstraintLayout
        }
    }


    private fun setPromptEditText(
        fannelName: String,
        setReplaceVariablesMap: Map<String, String>?,
        promptEditText: AppCompatEditText?,
        promptListFile: File?,
        editTextMap: Map<String, String>,
        promptListView: RecyclerView?,
        promptListAdapter: PromptListAdapter,
        disableListBind: Boolean,
        listLimit: Int?,
    ){
        if(promptEditText == null) return
        promptEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if(
                    !promptEditText.hasFocus()
                    || disableListBind
                ) return
                val updatePromptList = PromptMapList.makePromptMapList(
                    fannelName,
                    setReplaceVariablesMap,
                    promptListFile,
                    editTextMap,
                    promptEditText.text.toString(),
                    listLimit,
                )
                promptListAdapter.prompMapList.clear()
                promptListAdapter.prompMapList.addAll(updatePromptList)
                promptListAdapter.notifyDataSetChanged()
//                CoroutineScope(Dispatchers.Main).launch {
//                    scrollToBottom(
//                        promptListView,
//                        promptListAdapter,
//                    )
//                }
            }
        })
    }

    private fun editTextKeyListener(
        fannelDirPath: String,
        holderLinear: ConstraintLayout?,
        bkRelative: RelativeLayout?,
        promptListView: RecyclerView?,
        promptEditText: AppCompatEditText?,
        promptListFile: File?,
        listLimit: Int?,
        disableUpdate: Boolean,
    ){
        val promptListAdapter = promptListView?.adapter as? PromptListAdapter
        promptEditText?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (
                    event.action != KeyEvent.ACTION_DOWN
                    || keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                val currentInputEditable = promptEditText.text
                if(promptEditText.text.isNullOrEmpty()){
                    exitDialog(
                        fannelDirPath,
                        holderLinear,
                        bkRelative,
                        promptListView,
                        String(),
                        null,
                        promptListFile,
                        listLimit,
                        disableUpdate
                    )
                    return false
                }
                val titleKey = PromptMapList.PromptListKey.TITLE.key
                val iconKey = PromptMapList.PromptListKey.ICON.key
                val iconStr = promptListAdapter?.prompMapList?.firstOrNull {
                        lineMap ->
                    val curTitle = lineMap.get(
                        titleKey
                    )
                    curTitle == returnValue
                }?.get(
                    iconKey
                )
                exitDialog(
                    fannelDirPath,
                    holderLinear,
                    bkRelative,
                    promptListView,
                    currentInputEditable.toString(),
                    iconStr,
                    promptListFile,
                    listLimit,
                    disableUpdate
                )
                return false
            }
        })
    }

    private fun setItemClickListener(
        fannelDirPath: String,
        holderConstraint: ConstraintLayout?,
        bkRelative: RelativeLayout?,
        promptListView: RecyclerView?,
        promptListAdapter: PromptListAdapter,
        promptEditText: AppCompatEditText?,
        promptListMap: Map<String, String>,
        promptListFile: File?,
        listLimit: Int?,
        disableUpdate: Boolean,
    ){
        val onInsertByClick = promptListMap.get(
            PromptListVars.onInsertByClick.name
        ) == switchOn
        val onDismissByClick = promptListMap.get(
            PromptListVars.onDismissByClick.name
        ) == switchOn
        promptListAdapter.itemClickListener = object: PromptListAdapter.OnItemClickListener{
            override fun onItemClick(holder: PromptListAdapter.PromptListViewHolder) {
                val itemTitle = holder.itemMap.get(
                    PromptMapList.PromptListKey.TITLE.key
                ) ?: return
                val itemIconStr = holder.itemMap.get(
                    PromptMapList.PromptListKey.ICON.key
                )
                if(onInsertByClick) {
                    promptEditText?.setText(itemTitle)
                }
                if(
                    !onDismissByClick
                ) {
                    ColdListRegister.register(
                        fannelDirPath,
                        promptListFile,
                        listLimit,
                        disableUpdate,
                        itemTitle,
                        itemIconStr,
                    )
                    promptListAdapter.notifyDataSetChanged()
                    return
                }
                exitDialog(
                    fannelDirPath,
                    holderConstraint,
                    bkRelative,
                    promptListView,
                    itemTitle,
                    itemIconStr,
                    promptListFile,
                    listLimit,
                    disableUpdate,
                )
            }
        }
    }

    private fun simpleExitDialog(){
        exitDialog(
            String(),
            null,
            null,
            null,
            String(),
            String(),
            null,
            null,
            true,
        )
    }

    private fun exitDialog(
        fannelDirPath: String,
        holderConstraint: ConstraintLayout?,
        bkRelative: RelativeLayout?,
        promptListView: RecyclerView?,
        returnStr: String,
        iconStr: String?,
        promptListFile: File?,
        listLimit: Int?,
        disableUpdate: Boolean,
    ){
        promptListView?.layoutManager = null
        promptListView?.adapter = null
        promptListView?.recycledViewPool?.clear()
        promptListView?.removeAllViews()
        bkRelative?.removeAllViews()
        holderConstraint?.removeAllViews()
        returnValue = returnStr
        ColdListRegister.register(
            fannelDirPath,
            promptListFile,
            listLimit,
            disableUpdate,
            returnValue,
            iconStr,
        )
        promptDialogObj?.dismiss()
        promptDialogObj = null
        onDialog = false
    }


    private object StatisticsTool {

        const val statisticsName = "statistics"
        const val statisticsMapSeparator = ','

        enum class StatisticsKey(
            val key: String
        ){
            TITLE("title"),
            DATETIME("datetime"),
        }

        fun exit(){
            DotBk.exit()
        }

        suspend fun displayStatisticsBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            statisticsTitleList: List<String?>,
            firstTitleGradColorsStrList: List<String>,
            isWhiteBackground: Boolean,
        ){
//            DotBk.handle(
//                terminalFragmentRef,
//                promptDialogObj,
//                statisticsTitleList,
//                fannelDirPath,
//                saveTagName,
//                isWhiteBackground
//            )
//            return
            val handleRnd = (1..8).random()
            when(true) {
                (handleRnd <= 2) -> makeMonocroBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    statisticsTitleList,
//
                )
                (handleRnd <= 4) -> makeWebBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    statisticsTitleList,
//
                )
                (handleRnd <= 6) -> DotBk.handle(
                    terminalFragmentRef,
                    promptDialogObj,
                    statisticsTitleList,
                    firstTitleGradColorsStrList,
                    isWhiteBackground,
                )
                else -> makePieBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    statisticsTitleList,
                )
            }
        }

        private object DotBk {


            private var imageSetMakeJob: Job? = null

            fun exit(){
                imageSetMakeJob?.cancel()
            }

            suspend fun handle(
                terminalFragmentRef: WeakReference<TerminalFragment>,
                promptDialogObj: Dialog?,
                statisticsTitleList: List<String?>,
                firstTitleGradColorsStrList: List<String>,
                isWhiteBackground: Boolean,
            ){
                val context = terminalFragmentRef.get()?.context
                val imageSetTriple = makeImageSet(
                    context,
                    statisticsTitleList,
                )

                setImageSet(
                    terminalFragmentRef,
                    promptDialogObj,
                    imageSetTriple,
                    firstTitleGradColorsStrList,
                    isWhiteBackground,
                )
            }

            suspend fun setImageSet(
                terminalFragmentRef: WeakReference<TerminalFragment>,
                promptDialogObj: Dialog?,
                imageSetTriple: Triple<List<Bitmap?>, Bitmap, List<Bitmap?>>?,
                firstTitleGradColorsStrList: List<String>,
                isWhiteBackground: Boolean,
            ) {
                if(
                    imageSetTriple == null
                ) return
                val terminalFragment =
                    terminalFragmentRef.get()
                        ?: return
                val context = terminalFragment.context
                    ?: return
//                val isAlreadySet =  isDotImageCreate(
//                    fannelDirPath,
//                    saveTagName,
//                )
//                val statisticsDotImageDirPath = getStatisticsDotImageDir(
//                    fannelDirPath,
//                    saveTagName
//                ).absolutePath
                val firstDotStormBitmapList = imageSetTriple.first
//                when (isAlreadySet) {
//                    false -> PromptListImageSet.firstDotStormImagePathList.map {
//                        AssetsFileManager.assetsByteArray(
//                            context,
//                            it
//                        )?.let {
//                            BitmapFactory.decodeByteArray(
//                                it,
//                                0,
//                                it.size
//                            )
//                        }
//                    }

//                    else -> PromptListImageSet.getFirstDotStormPathList(
//                        statisticsDotImageDirPath
//                    ).map {
//                        BitmapTool.convertFileToBitmap(
//                            it
//                        )
//                    }
//                }
                val mainImageBitmap = imageSetTriple.second
//                when (isAlreadySet) {
//                    false ->
//                        AssetsFileManager.assetsByteArray(
//                            context,
//                            PromptListImageSet.mainImagePath
//                        )?.let {
//                            BitmapFactory.decodeByteArray(
//                                it,
//                                0,
//                                it.size
//                            )
//                        }
//
//                    else -> PromptListImageSet.getMainImagePath(
//                        statisticsDotImageDirPath
//                    ).let {
//                        BitmapTool.convertFileToBitmap(
//                            it
//                        )
//                    }
//                }
                val strImageBitmapList = imageSetTriple.third
//                when (isAlreadySet) {
//                    false -> PromptListImageSet.strImagePathList.map {
//                        AssetsFileManager.assetsByteArray(
//                            context,
//                            it
//                        )?.let {
//                            BitmapFactory.decodeByteArray(
//                                it,
//                                0,
//                                it.size
//                            )
//                        }
//                    }
//
//                    else -> PromptListImageSet.getStrImagePathList(
//                        statisticsDotImageDirPath
//                    ).map {
//                        BitmapTool.convertFileToBitmap(
//                            it
//                        )
//                    }
//                }

                val imageColorStrList = listOf(
                    CmdClickColorStr.LIGHT_GREEN.str,
                    CmdClickColorStr.ANDROID_GREEN.str,
                    CmdClickColorStr.YELLOW_GREEN.str,
                    CmdClickColorStr.GREEN.str,
//                    CmdClickColorStr.THICK_GREEN.str,
                    CmdClickColorStr.DARK_GREEN.str,
//                    CmdClickColorStr.GOLD_YELLOW.str,
                    CmdClickColorStr.WATER_BLUE.str,
//                    CmdClickColorStr.THICK_AO.str,
//                    CmdClickColorStr.BLACK_AO.str,
                    CmdClickColorStr.BLUE.str,
                    CmdClickColorStr.BLUE_DARK_PURPLE.str,
                    CmdClickColorStr.NAVY.str,
                    CmdClickColorStr.PURPLE.str,
                    CmdClickColorStr.ORANGE.str,
//                    CmdClickColorStr.BROWN.str,
//                    CmdClickColorStr.DARK_BROWN.str,
//                    CmdClickColorStr.YELLOW.str,
//                    CmdClickColorStr.SKERLET.str,
                    "#000000",
//                    "#ffffff",
                    CmdClickColorStr.SKERLET.str,
                )
                val darkColorList = listOf(
                    CmdClickColorStr.ANDROID_GREEN.str,
                    CmdClickColorStr.YELLOW_GREEN.str,
                    CmdClickColorStr.GREEN.str,
//                    CmdClickColorStr.THICK_GREEN.str,
                    CmdClickColorStr.DARK_GREEN.str,
//                    CmdClickColorStr.GOLD_YELLOW.str,
                    CmdClickColorStr.THICK_AO.str,
//                    CmdClickColorStr.BLACK_AO.str,
                    CmdClickColorStr.BLUE.str,
                    CmdClickColorStr.BLUE_DARK_PURPLE.str,
                    CmdClickColorStr.NAVY.str,
                    CmdClickColorStr.PURPLE.str,
//                    CmdClickColorStr.BROWN.str,
//                    CmdClickColorStr.DARK_BROWN.str,
                    "#000000",
                )
                val dotStormColorListForWhiteBackground = listOf(
                    CmdClickColorStr.GREEN.str,
//                    CmdClickColorStr.THICK_GREEN.str,
                    CmdClickColorStr.DARK_GREEN.str,
//                    CmdClickColorStr.GOLD_YELLOW.str,
                    CmdClickColorStr.THICK_AO.str,
//                    CmdClickColorStr.BLACK_AO.str,
                    CmdClickColorStr.BLUE.str,
                    CmdClickColorStr.BLUE_DARK_PURPLE.str,
                    CmdClickColorStr.NAVY.str,
                    CmdClickColorStr.PURPLE.str,
                    CmdClickColorStr.ORANGE.str,
                    CmdClickColorStr.BROWN.str,
                    CmdClickColorStr.DARK_BROWN.str,
                    CmdClickColorStr.SKERLET.str,
                    "#000000",
                )
                val frontBkImageColorStr = when(isWhiteBackground) {
                    true -> dotStormColorListForWhiteBackground
                        // darkColorList.random()
                    else -> imageColorStrList
                }.let {
                    frontBkImageColorStrListSrc ->
                    filterByFirstGradColorList(
                        frontBkImageColorStrListSrc,
                        firstTitleGradColorsStrList
                    )
                }.random()
                val isLightColorToBack = darkColorList.contains(frontBkImageColorStr)
                val backBkImageColorStr = imageColorStrList.filter {
                    if (isLightColorToBack) {
                        return@filter !darkColorList.contains(it)
                    }
                    true
                }.filter {
                    it != frontBkImageColorStr
                }.let {
                        colorStrList ->
                    filterByFirstGradColorList(
                        colorStrList,
                        firstTitleGradColorsStrList
                    )
                }.random()
                promptDialogObj?.findViewById<FrameLayout>(
                    R.id.prompt_list_dialog_list_extra_fore_container
                )?.apply {
                    val goalMinusAlpha = 1f
                    val loopTimes = 5
                    val minusAlpha = goalMinusAlpha / loopTimes
                    alpha = 1f
                    isVisible = true
                    CoroutineScope(Dispatchers.IO).launch {
                        withContext(Dispatchers.IO){
                            delay(200)
                        }
                        for (i in 1..loopTimes) {
                            withContext(Dispatchers.IO) {
                                delay(50)
                            }
                            withContext(Dispatchers.Main) {
                                alpha -= minusAlpha
                            }
                        }
                    }
                }
                val forImageViewBitmapToResIdList = listOf(
                    firstDotStormBitmapList[0] to R.id.prompt_list_dialog_list_extra_bk_fore_image3,
                    firstDotStormBitmapList[1] to R.id.prompt_list_dialog_list_extra_bk_fore_image2,
                )
                forImageViewBitmapToResIdList.forEachIndexed { index, bitmapToResId ->
                    val bitmap = bitmapToResId.first
                    val imageViewId = bitmapToResId.second
                    promptDialogObj?.findViewById<AppCompatImageView>(
                        imageViewId
                    )?.apply {
                        setColorFilter(
                            Color.parseColor(frontBkImageColorStr)
                        )
                        setImageBitmap(bitmap)
                        isVisible = true
                        val duration = when (index) {
                            0 -> 200L
                            1 -> 700L
                            else -> 700L
                        }
                        YoYo.with(Techniques.FadeOut)
                            .duration(duration)
                            .repeat(0)
                            .playOn(this@apply)
                    }
                }
                promptDialogObj?.findViewById<AppCompatImageView>(
                    R.id.prompt_list_dialog_list_bk_extra_back_image1
                )?.apply {
                    imageTintList = null
                    setColorFilter(
                        Color.parseColor(backBkImageColorStr)
                    )
                    scaleType = ImageView.ScaleType.FIT_XY
                    isVisible = true
                    setImageBitmap(
                        mainImageBitmap
                    )
                }
                promptDialogObj?.findViewById<AppCompatImageView>(
                    R.id.prompt_list_dialog_list_bk_extra_back_image2
                )?.apply {
                    imageTintList = null
                    setColorFilter(
                        Color.parseColor(frontBkImageColorStr)
                    )
                    scaleType = ImageView.ScaleType.FIT_XY
                    isVisible = true
                    val animationDrawable = AnimationDrawable()
                    strImageBitmapList.forEach {
                        animationDrawable.addFrame(
                            BitmapDrawable(context.resources, it),
                            600
                        )
                    }
                    animationDrawable.isOneShot = false
                    setImageDrawable(animationDrawable)
                    animationDrawable.start()
                }
                promptDialogObj?.findViewById<FrameLayout>(
                    R.id.prompt_list_dialog_list_extra_bk_container
                )?.apply {
                    isVisible = true
                    val loopTimes = 2
                    CoroutineScope(Dispatchers.IO).launch {
                        for (i in 1..loopTimes) {
                            withContext(Dispatchers.IO) {
                                delay(100)
                            }
                            withContext(Dispatchers.Main) {
                                val plusAlpha = when(i){
                                    1 -> 0.2f
                                    else -> 0.3f
                                }
                                alpha += plusAlpha
                            }
                        }
                    }
                }
            }

            private fun filterByFirstGradColorList(
                colorStrList: List<String>,
                firstTitleGradColorsStrList: List<String>
            ): List<String> {
                val filteredFirstTitleGradColorsStrList = colorStrList.filter {
                    firstTitleGradColorsStrList.contains(it)
                }
                if(
                    filteredFirstTitleGradColorsStrList.isNotEmpty()
                ) return filteredFirstTitleGradColorsStrList
                return colorStrList
            }

            suspend fun makeImageSet(
                context: Context?,
                statisticsTitleList: List<String?>,
            ): Triple<
                List<Bitmap?>,
                Bitmap,
                List<Bitmap?>,
            >?  {
                if(
                    context == null
                    ) return null
                val decentTextToFreqList = statisticsTitleList.groupBy { it }
                    .mapValues { it.value.size }
                    .filterKeys { !it.isNullOrEmpty() }
                    .toList()
                    .sortedByDescending { (_, value) -> value }
                val frequencyMapList =
                    decentTextToFreqList.toMap()
                val textList = frequencyMapList.map { freqMap ->
                    val text = freqMap.key
                        ?: return@map emptyList<String>()
                    val freq = freqMap.value
                    (0..freq).map { text }
                }.flatten()

                val cutPeaceLength = 32
                val srcOneSide = cutPeaceLength * 8
                val curRepeatNum = let {
                    val baseRepeatNum = 2500
                    val baseCutPeace = 32f
                    (baseRepeatNum * cutPeaceLength * 2.5) / baseCutPeace
                }
                val textBitmapList = (1..10).map {
                    textList.random()
                }.joinToString(" ").let { titlesTextSrc ->
                    val repeatNum = (curRepeatNum / titlesTextSrc.length).toInt()
                    titlesTextSrc.repeat(repeatNum)
                }.let { titlesText ->
                    val srcTextBitmapSideLength =
                        (srcOneSide * 1.1).toFloat()
                    val concurrencyLimitForMakeTextBitmap = 3
                    val semaphoreForMakeTextBitmap = Semaphore(concurrencyLimitForMakeTextBitmap)
                    val channelForMakeTextBitmap =
                        Channel<Pair<Int, Bitmap?>>(concurrencyLimitForMakeTextBitmap)
                    val indexToTextBitmapList: MutableList<Pair<Int, Bitmap?>> = mutableListOf()
                    withContext(Dispatchers.IO) {
                        val jobList = (1..concurrencyLimitForMakeTextBitmap).map { index ->
                            async {
                                semaphoreForMakeTextBitmap.withPermit {
                                    val textBitmap = BitmapTool.DrawText.drawTextToBitmapByRandom(
                                        titlesText,
                                        srcTextBitmapSideLength,
                                        srcTextBitmapSideLength,
                                        (8..10).random().toFloat(),
//                                        (15..20).random().toFloat(),
                                        Color.BLACK
                                    ).let {
                                        ImageTransformer.cutCenter2(
                                            it,
                                            srcOneSide,
                                            srcOneSide
                                        )
                                    }
                                    channelForMakeTextBitmap.send(Pair(index, textBitmap))
                                }
                            }
                        }
                        jobList.forEach { it.await() }
                        channelForMakeTextBitmap.close()
                        for (indexToBitmap in channelForMakeTextBitmap) {
                            indexToTextBitmapList.add(indexToBitmap)
                        }
                        indexToTextBitmapList.sortBy { it.first }
                        indexToTextBitmapList.map {
                            it.second
                        }.let {
                            it + it[1]
                        }
                    }
                }

                val textBitmapListToMaskSquare = makeDrawableBitmapListToMaskSquare(
                    textBitmapList,
                    srcOneSide,
                    cutPeaceLength
                )
                val firstDotStormBitmapList = textBitmapListToMaskSquare.first
                val maskSquare = textBitmapListToMaskSquare.second
                    ?: return null
                val padding = srcOneSide * 0.2
                val marginOneSide = (srcOneSide - padding).toInt()
                val useFannelIconList = listOf(
                    FannelIcons.CORSAIR,
                    FannelIcons.SKULL,
                    FannelIcons.SKULL_HORIZON,

                )
                val noUseFannelIconList = listOf(
                    FannelIcons.JANOMITI2
                )
                val fannelIcon = FannelIcons.entries.filter {
                    !noUseFannelIconList.contains(it)
                }.random()
                //useFannelIconList.random()
                val ordinalySizeIconList = listOf(
                    FannelIcons.CROCODILE,
                    FannelIcons.LABRADOR,
                    FannelIcons.JANOMITI,
                    FannelIcons.JANOMITI2,
                    FannelIcons.CHECKER,
                )
                val srcMainImageDrawable = AppCompatResources.getDrawable(
                    context,
                    fannelIcon.id
                ) ?: return null
                val srcMainImageBitmap = when(
                    ordinalySizeIconList.contains(fannelIcon)
                ) {
                    true -> srcMainImageDrawable.toBitmap(
                        srcOneSide,
                        srcOneSide
                    )
                    else -> {
                        val srcMainImageBitmapWithMargin = srcMainImageDrawable.toBitmap(
                            marginOneSide,
                            marginOneSide
                        )
                        ImageTransformer.addPadding(
                            srcMainImageBitmapWithMargin,
                            padding.toInt(),
                            padding.toInt()
                        ).let {
                            BitmapTool.resizeByMaxHeight(
                                it,
                                srcOneSide.toDouble()
                            )
                        }
                    }
                }
//                    .let {
//                    when(
//                        (1..3).random() % 2 == 0
//                    ){
//                        true -> {
//                            ImageTransformer.exchangeTransparentToBlack(
//                                it
//                            )
//                        }
//                        else -> it
//                    }
//                }
                val mainImageBitmap = let {
                    val reversedSquareBitmap = ImageTransformer.exchangeTransparentToBlack(
                        maskSquare,
                    )
                val maskedMainImageBitmap = ImageTransformer.maskImageByTransparent(
                    srcMainImageBitmap,
                    reversedSquareBitmap,
                )
                maskedMainImageBitmap
                }
                val concurrencyLimitForCenterImage = textBitmapList.size
                val semaphoreForTextBk = Semaphore(10)
                val channelForTextBk = Channel<Pair<Int, Bitmap?>>(concurrencyLimitForCenterImage)
                val indexToTextBkBitmapList: MutableList<Pair<Int, Bitmap?>> = mutableListOf()
                val shrinkNumb = 10 //(0..7).random()
                val strImageBitmapList =
                    withContext(Dispatchers.IO) {
                        val jobList = textBitmapList.mapIndexed { index, textBitmap ->
                            async {
                                semaphoreForTextBk.withPermit {
                                    if (
                                        textBitmap == null
                                    ) return@withPermit null
                                    val textBkBitmap =
                                        ImageTransformer.maskImageByTransparent(
                                            textBitmap,
                                            srcMainImageBitmap,
                                        ).let { drawableBitmap ->
                                            val shrinkOneSideLength = srcOneSide - shrinkNumb
                                            ImageTransformer.cutCenter2(
                                                drawableBitmap,
                                                shrinkOneSideLength,
                                                shrinkOneSideLength,
                                            )
                                        }
                                    channelForTextBk.send(Pair(index, textBkBitmap))
                                }
                            }
                        }
                        jobList.forEach { it.await() }
                        channelForTextBk.close()
                        for (indexToBitmap in channelForTextBk) {
                            indexToTextBkBitmapList.add(indexToBitmap)
                        }
                        indexToTextBkBitmapList.sortBy { it.first }
                        indexToTextBkBitmapList.map {
                            it.second
                        }
                    }
                return Triple(
                    firstDotStormBitmapList,
                    mainImageBitmap,
                    strImageBitmapList
                )
            }

            private suspend fun makeDrawableBitmapListToMaskSquare(
                textBitmapList: List<Bitmap?>,
                srcOneSide: Int,
                peaceLength: Int,
            ): Pair<List<Bitmap?>, Bitmap?> {
                val textBitmapListSize = textBitmapList.size
                val startX = 4
                val endX = 10
                val ajustNum = 2
                val concurrencyLimit = textBitmapListSize
                val semaphore = Semaphore(concurrencyLimit)
                val channelForSquareList = Channel<Pair<Int, Bitmap?>>(textBitmapListSize)
                val maskIndexToSquareList: MutableList<Pair<Int, Bitmap?>> = mutableListOf()
                withContext(Dispatchers.IO) {
//                    (1..textBitmapListSize)
                    val jobList = listOf(2, 3).map { order ->
                        async {
                            semaphore.withPermit {
                                val incline = ((endX - startX) / textBitmapListSize.toFloat())
                                val currentRndEnd = (endX - incline * order).toInt() - ajustNum
                                val rndList = (1..currentRndEnd)
                                val maskSquare = BitmapTool.DotArt.maskSquareMaker(
                                    srcOneSide,
                                    peaceLength,
                                    rndList,
                                    2
                                )
                                channelForSquareList.send(Pair(order, maskSquare))
                            }
                        }
                    }
                    jobList.forEach { it.await() }
                    channelForSquareList.close()
                    for(indexToBitmap in channelForSquareList){
                        maskIndexToSquareList.add(indexToBitmap)
                    }
                }
                maskIndexToSquareList.sortBy {
                    val index = it.first
                    index
                }
                val maskSquareList = maskIndexToSquareList.map {
                    val bitmap = it.second
                    bitmap
                }
//                    .take(4).let {
//                    listOf(
//                        it[1],
//                        it[2],
//                    )
//                }
//                FileSystems.updateFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "lmaskSquareList0.txt").absolutePath,
//                    listOf(
//                        "LocalDateTime: ${LocalDateTime.now()}",
//                        "srcOneSide: ${srcOneSide}",
//                        "peaceLength: ${peaceLength}",
//                        "maskSquareListSize: ${maskSquareList.size}",
//                        "isNull:: ${maskSquareList.any {
//                            it == null }
//                        }",
//                    ).joinToString("\n")
//                )
                val concurrencyLimitForMask = maskSquareList.size
                val semaphoreForMask = Semaphore(concurrencyLimitForMask)
                val channelForMask = Channel<Pair<Int, Bitmap?>>(concurrencyLimitForMask)
                val indexToMaskTextList: MutableList<Pair<Int, Bitmap?>> = mutableListOf()
                val maskTextList = withContext(Dispatchers.IO) {
                    val jobList = maskSquareList.mapIndexed { index, maskSquareSrc ->
                        if (
                            maskSquareSrc == null
                        ) return@mapIndexed null
                        async {
                            semaphoreForMask.withPermit {
                                val curTextBitmap = textBitmapList.getOrNull(index)
                                    ?: return@withPermit
                                val maskedBitmap1 = ImageTransformer.maskImageByTransparent(
                                    curTextBitmap,
                                    maskSquareSrc,
                                )
                                channelForMask.send(Pair(index, maskedBitmap1))
                            }
                        }
                    }
                    jobList.forEach { it?.await() }
                    channelForMask.close()
                    for(indexToBitmap in channelForMask){
                        indexToMaskTextList.add(indexToBitmap)
                    }
                    indexToMaskTextList.sortBy { it.first }
                    indexToMaskTextList.map {
                        it.second
                    }
                }
                return maskTextList to maskSquareList.last()
            }
        }

        private suspend fun makeMonocroBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            statisticsTitleList: List<String?>,
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightInt = withContext(Dispatchers.Main) screenHeightInt@ {
                    for(i in 1..5) {
                        val screenHeightIntSrc = bkRelative.measuredHeight - dialogMargin
                        if(screenHeightIntSrc <= 0) {
                            delay(100)
                            continue
                        }
                        return@screenHeightInt screenHeightIntSrc
                    }
                    0
                }.let {
                    if(it <= 0) return@withContext
                    it
                }
                val screenWidthInt = withContext(Dispatchers.Main) screenWidthInt@ {
                    for(i in 1..5) {
                        val screenWidthIntSrc = bkRelative.measuredWidth - dialogMargin
                        if(screenWidthIntSrc <= 0) {
                            delay(100)
                            continue
                        }
                        return@screenWidthInt screenWidthIntSrc
                    }
                    0
                }.let {
                    if(it <= 0) return@withContext
                    it
                }
                val decentTextToFreqList = statisticsTitleList.groupBy { it }
                    .mapValues { it.value.size }
                    .filterKeys { !it.isNullOrEmpty() }
                    .toList()
                    .sortedByDescending { (_, value) -> value }
                val frequencyMapList =
                    decentTextToFreqList.toMap()
                val decentTextList = decentTextToFreqList.map {
                    it.first
                }.filter {
                    !it.isNullOrEmpty()
                }
                val textList = frequencyMapList.map {
                    freqMap ->
                    val text = freqMap.key
                        ?: return@map emptyList<String>()
                    val freq = freqMap.value
                    (0..freq).map { text }
                }.flatten()
                val screenWidth = screenWidthInt + 500
                val oneSideLengthDiff = (screenWidth) / decentTextList.size
                val textToOneSideLengthMap = decentTextList.mapIndexed {
                        index, text ->
                    val startInt = screenWidth - oneSideLengthDiff * (index + 1)
                    val endInt = screenWidth - oneSideLengthDiff * index
                    text to (startInt..endInt)
                }.toMap()
                val rotateAngleRndList = (-270..270)
                val alphaRndList = (100..300)
//                    (100..400)
                val repeatTimes = (7..10).random()
                val textSizeEnd = 11
                val textSizeDiff = textSizeEnd / decentTextList.size
                val textToFloatMap = decentTextList.mapIndexed {
                        index, text ->
                    text to (textSizeEnd - textSizeDiff * index).toFloat()
                }.toMap()

                val fixRotationAngle = rotateAngleRndList.random().toFloat()

                val colorIntArray = listOf(
                    CmdClickColorStr.DARK_GREEN.str,
                    CmdClickColorStr.DARK_GREEN.str
                ).map {
                    Color.parseColor(it)
                }.toIntArray()
                val bitmap = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    100,
                    100,
                    colorIntArray,
                    BitmapTool.GradientBitmap.GradOrient.BOTH
                )
                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                (0..repeatTimes).forEachIndexed {
                        index, _ ->
                    withContext(Dispatchers.Main) createText@{
                        val inflater = LayoutInflater.from(context)
                        val buttonLayout = inflater.inflate(
                            R.layout.prompt_list_bk_another_component,
                            null
                        ) as FrameLayout
                        val curText = textList.shuffled().first()
                        buttonLayout.apply {
                            val oneSideLength = try {
                                textToOneSideLengthMap.get(curText)?.random()
                            } catch (e: Exception){ null } ?: screenWidthInt
//                            oneSideLengthRndList.random()
                            val relativeParam = RelativeLayout.LayoutParams(
                                oneSideLength,
                                oneSideLength,
                            )
                            layoutParams = relativeParam
                            alpha = alphaRndList.random().toFloat() / 1000
                            rotation = fixRotationAngle
                            val putXyPair = let {
                                var putXyPairSrc: Pair<Float, Float> = Pair(0f, 0f)
                                for(i in 1..5 ){
                                    putXyPairSrc = Pair(
                                        (-oneSideLength..screenWidthInt).random().toFloat(),
                                        (-oneSideLength..screenHeightInt).random().toFloat()
                                    )
                                    val isBottomLeft = putXyPairSrc.first > (screenWidthInt / 2)
                                            || putXyPairSrc.second > screenHeightInt / 2
                                    if(
                                        !isBottomLeft
                                    ) return@let putXyPairSrc
                                    if (
                                        (1..3).random() == 1
                                    ) return@let putXyPairSrc
                                }
                                putXyPairSrc
                            }
                            x = putXyPair.first
                            y = putXyPair.second
                        }
                        bkRelative.addView(buttonLayout)

                        CoroutineScope(Dispatchers.Main).launch {
                            val textView =
                                buttonLayout.findViewById<AppCompatTextView>(R.id.prompt_list_bk_another_caption)

                            textView.apply {
                                text = curText
                                typeface = Typeface.DEFAULT
                                textToFloatMap.get(curText)?.let {
                                    textSize = it
                                }
                            }

                            buttonLayout.findViewById<AppCompatImageView>(R.id.prompt_list_bk_another_layout_image)
                                .apply {
                                    imageTintList = AppCompatResources.getColorStateList(
                                        context,
                                        R.color.black
                                    )
                                    setImageBitmap(bitmap)

                                    Glide
                                        .with(context)
                                        .load(bitmap)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .thumbnail(requestBuilder)
                                        .into(this)
                                }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
//                    delay(200)
                    val iconId = FannelIcons.entries.map { it.id }.shuffled().first()
                    val shuujiImage = AppCompatImageView(context).apply {
                        val relativeParam = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ).apply {
                            bottomMargin = 2
                            leftMargin = 2
                        }
                        layoutParams = relativeParam
                        imageTintList = AppCompatResources.getColorStateList(context, R.color.black)
                        setImageResource(iconId)
                        scaleType = ImageView.ScaleType.FIT_XY
                        alpha = 1f
                    }
                    val shuujiImageWhiteShadow2 = AppCompatImageView(context).apply {
                        val relativeParam = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ).apply {
                            bottomMargin = 0
                            leftMargin = 0
                        }
                        layoutParams = relativeParam
                        imageTintList =
                            AppCompatResources.getColorStateList(context, R.color.white)
                        setImageResource(iconId)
                        scaleType = ImageView.ScaleType.FIT_XY
                        alpha = 1f
                    }
                    val shuujiImageWhiteShadow3 = AppCompatImageView(context).apply {
                        val relativeParam = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT,
                        ).apply {
                            bottomMargin = 4
                            leftMargin = 4
                        }
                        layoutParams = relativeParam
                        imageTintList =
                            AppCompatResources.getColorStateList(context, R.color.white)
                        setImageResource(iconId)
                        scaleType = ImageView.ScaleType.FIT_XY
                        alpha = 1f
                    }
                    val bkFrameLayout = FrameLayout(context).apply {
                        val relativeParam = RelativeLayout.LayoutParams(
                            (screenWidthInt * 3) / 4,
                            (screenHeightInt * 3) / 4,
                        ).apply {
                            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                            addRule(RelativeLayout.ALIGN_PARENT_END)
                        }
                        layoutParams = relativeParam
                        alpha = 0.7f
                    }
                    bkFrameLayout.addView(shuujiImageWhiteShadow2)
                    bkFrameLayout.addView(shuujiImageWhiteShadow3)
                    bkFrameLayout.addView(shuujiImage)
//                    delay(100)
                    bkRelative.addView(bkFrameLayout)
                }

            }
        }

        private suspend fun makeWebBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            statisticsTitleList: List<String?>,
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightInt = withContext(Dispatchers.Main) screenHeightInt@ {
                        for(i in 1..5) {
                            val screenHeightIntSrc = bkRelative.measuredHeight - dialogMargin
                            if(screenHeightIntSrc <= 0) {
                                delay(100)
                                continue
                            }
                            return@screenHeightInt screenHeightIntSrc
                        }
                        0
                }.let {
                    if(it <= 0) return@withContext
                    it
                }
                val screenWidthInt =
                    withContext(Dispatchers.Main) screenWidthInt@ {
                        for(i in 1..5) {
                            val screenWidthIntSrc = bkRelative.measuredWidth - dialogMargin
                            if(screenWidthIntSrc <= 0) {
                                delay(100)
                                continue
                            }
                            return@screenWidthInt screenWidthIntSrc
                        }
                        0
                    }.let {
                        if(it <= 0) return@withContext
                        it
                    }
                val decentTextToFreqList = statisticsTitleList.groupBy { it }
                    .mapValues { it.value.size }
                    .filterKeys { !it.isNullOrEmpty() }
                    .toList()
                    .sortedByDescending { (_, value) -> value }
                val frequencyMapList =
                    decentTextToFreqList.toMap()
                val decentTextList = decentTextToFreqList.map {
                    it.first
                }.filter {
                    !it.isNullOrEmpty()
                }
                val textList = frequencyMapList.map {
                        freqMap ->
                    val text = freqMap.key
                        ?: return@map emptyList<String>()
                    val freq = freqMap.value
                    (0..freq).map { text }
                }.flatten()

                val srcColorList = listOf(
                    CmdClickColorStr.LIGHT_GREEN,
                    CmdClickColorStr.WHITE_GREEN,
                    CmdClickColorStr.ANDROID_GREEN,
                    CmdClickColorStr.YELLOW_GREEN,
                    CmdClickColorStr.GREEN,
//                    CmdClickColorStr.THICK_GREEN,
//                    CmdClickColorStr.DARK_GREEN,
//                    CmdClickColorStr.CARKI,
//                    CmdClickColorStr.GOLD_YELLOW,
                    CmdClickColorStr.WATER_BLUE,
                    CmdClickColorStr.WHITE_BLUE,
//                    CmdClickColorStr.THICK_AO,
//                    CmdClickColorStr.BLACK_AO,
                    CmdClickColorStr.BLUE,
                    CmdClickColorStr.WHITE_BLUE_PURPLE,
//                    CmdClickColorStr.BLUE_DARK_PURPLE,
//                    CmdClickColorStr.NAVY,
                    CmdClickColorStr.PURPLE,
                    CmdClickColorStr.ORANGE,
//                    CmdClickColorStr.BROWN,
//                    CmdClickColorStr.DARK_BROWN,
                    CmdClickColorStr.YELLOW,
                    CmdClickColorStr.SKERLET,
                )
                val colorStrList = srcColorList.map {
                    it.str
                }
                val colorIdList = CmdClickColor.entries.map {
                    it.id
                }
                val textColorIdMap = frequencyMapList.map {
                    val text = it.key ?: String()
                    val colorId = colorIdList.shuffled().first()
                    text to colorId
                }.filter {
                    it.first.isNotEmpty()
                }.toMap()
                val capturePartPngDirPathList = ButtonImageCreator.makeCapturePartPngDirPathList()
                val colorStr = colorStrList.random()
                val colorIntArray = listOf(
                    colorStr,
                    colorStr,
                ).map {
                    Color.parseColor(it)
                }.toIntArray()
                val defaultBkBitmap = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    300,
                    600,
                    colorIntArray,
                    BitmapTool.GradientBitmap.GradOrient.BOTH
                )

                val textSrcBkBitmapMap = frequencyMapList.map {
                    val text = it.key ?: String()
                    val capturePngPath = capturePartPngDirPathList.shuffled().firstOrNull()?.let { dirPath ->
                        if (
                            dirPath.isEmpty()
                        ) return@let null
                        FileSystems.sortedFiles(dirPath).shuffled().firstOrNull()
                            ?.let fileList@{
                                if (
                                    it.isEmpty()
                                ) return@fileList null
                                File(dirPath, it).absolutePath
                            }
                    }
                    if(capturePngPath.isNullOrEmpty()) return@map text to defaultBkBitmap
                    val srcBitmap = BitmapTool.convertFileToBitmap(capturePngPath)
                        ?: return@map text to defaultBkBitmap
                    text to srcBitmap
                }.filter {
                    it.first.isNotEmpty()
                }.toMap()
                val bitmapCutWidth = 100
                val textBkBitmapMap = textSrcBkBitmapMap.map {
                    val text = it.key
                    val srcBitmap = it.value

                    val cutBitmap = BitmapTool.ImageTransformer.cut(
                        srcBitmap,
                        bitmapCutWidth,
                        (bitmapCutWidth * 1.73).toInt()
                    )
                    text to cutBitmap
                }.filter {
                    it.first.isNotEmpty()
                }.toMap()
                val screenWidth = (10 * screenWidthInt) / 10 + 500
                val oneSideLengthDiff = (screenWidth) / decentTextList.size
                val textToOneSideLengthMap = decentTextList.mapIndexed {
                        index, text ->
                    val startInt = screenWidth - oneSideLengthDiff * (index + 1)
                    val endInt = screenWidth - oneSideLengthDiff * index
                    text to (startInt..endInt)
                }.toMap()
                val rotateAngleRndList = (-270..270)
                val alphaRndList = (100..300)
                    //(100..400)
                val repeatTimes = (5..10).random()
                val textSizeEnd = 30
                val textSizeDiff = textSizeEnd / decentTextList.size
                val textToFloatMap = decentTextList.mapIndexed {
                        index, text ->
                    text to (textSizeEnd - textSizeDiff * index).toFloat()
                }.toMap()
                val fixRotationAngle = rotateAngleRndList.random().toFloat()


                val requestBuilder: RequestBuilder<Drawable> =
                    Glide.with(context)
                        .asDrawable()
                        .sizeMultiplier(0.1f)
                val plusRotateListSrc = listOf(-90f, 0f, 90f)
                val plusRotateList = when((1..3).random() == 3) {
                    false -> listOf(plusRotateListSrc.random())
                    else -> plusRotateListSrc
                }
                (0..repeatTimes).forEachIndexed {
                        index, _ ->
                    withContext(Dispatchers.Main) createText@{
                        val inflater = LayoutInflater.from(context)
                        val buttonLayout = inflater.inflate(
                            R.layout.web_bk_image_caption_layout,
                            null
                        ) as FrameLayout
                        val curText = textList.shuffled().first()
                        buttonLayout.apply {
                            val scaleWidth = try {
                                textToOneSideLengthMap.get(curText)?.random()
                            } catch (e: Exception){ null } ?: screenWidthInt
                            val scaleHeight = (scaleWidth * 1.73).toInt()
                            val relativeParam = RelativeLayout.LayoutParams(
                                scaleWidth,
                                scaleHeight,
                            )
                            layoutParams = relativeParam
                            alpha = alphaRndList.random().toFloat() / 1000
                            rotation = fixRotationAngle + plusRotateList.random()
                            val putXyPair = let {
                                var putXyPairSrc: Pair<Float, Float> = Pair(0f, 0f)
                                val shrinkRate = 10
                                for(i in 1..5 ){
                                    putXyPairSrc = Pair(
                                        (-( shrinkRate * scaleWidth) / 10..(10 * screenWidthInt) / 10).random().toFloat(),
                                        (-(shrinkRate * scaleHeight) / 10..(10 * screenHeightInt) / 10).random().toFloat()
                                    )
                                    val isBottomLeft = putXyPairSrc.first > (screenWidthInt / 2)
                                            || putXyPairSrc.second > screenHeightInt / 2
                                    if(
                                        !isBottomLeft
                                    ) return@let putXyPairSrc
                                    if (
                                        (1..3).random() == 1
                                    ) return@let putXyPairSrc
                                }
                                putXyPairSrc
                            }
                            x = putXyPair.first
                            y = putXyPair.second
                        }
                        bkRelative.addView(buttonLayout)

                        CoroutineScope(Dispatchers.Main).launch {
                            val textView =
                                buttonLayout.findViewById<OutlineTextView>(R.id.web_bk_image_caption_layout_caption)

                            textView.apply {
                                text = curText
                                typeface = Typeface.DEFAULT
                                textColorIdMap.get(curText)?.let {
                                    setFillColor(it)
                                }
                                setStrokeColor(R.color.black)
                                textToFloatMap.get(curText)?.let {
                                    textSize = it
                                }
                            }

                            buttonLayout.findViewById<AppCompatImageView>(R.id.web_bk_image_caption_layout_image)
                                .apply {
                                    imageTintList = null
                                    val isAnime = (1..3).random() == 3
                                    if(!isAnime) {
                                        val bkBitmap =
                                            textBkBitmapMap.get(curText) ?: defaultBkBitmap

                                        Glide
                                            .with(context)
                                            .load(bkBitmap)
                                            .transition(DrawableTransitionOptions.withCrossFade())
                                            .skipMemoryCache(true)
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .thumbnail(requestBuilder)
                                            .into(this)
                                        return@apply
                                    }
                                    val srcBkBitmap =
                                        textSrcBkBitmapMap.get(curText) ?: defaultBkBitmap
                                    val animationDrawable = AnimationDrawable()
                                    val cutHeight = (bitmapCutWidth * 1.73).toInt()
                                    val bitmapList = (1..3).map {
                                        BitmapTool.ImageTransformer.cut(
                                            srcBkBitmap,
                                            bitmapCutWidth,
                                            cutHeight
                                        )
                                    }
                                    val delay = (800..900).random()
                                    bitmapList.forEach {
                                        animationDrawable.addFrame(
                                            BitmapDrawable(context.resources, it),
                                            delay
                                        )
                                    }
                                    animationDrawable.isOneShot = false
                                    setImageDrawable(animationDrawable)
                                    animationDrawable.start()
                                }
                        }
                    }
                }
            }
        }


        private suspend fun makePieBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            statisticsTitleList: List<String?>,
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightInt = withContext(Dispatchers.Main) screenHeightInt@ {
                    for(i in 1..5) {
                        val screenHeightIntSrc = bkRelative.measuredHeight - dialogMargin
                        if(screenHeightIntSrc <= 0) {
                            delay(100)
                            continue
                        }
                        return@screenHeightInt screenHeightIntSrc
                    }
                    0
                }.let {
                    if(it <= 0) return@withContext
                    it
                }
                val screenWidthInt = withContext(Dispatchers.Main) screenWidthInt@ {
                    for(i in 1..5) {
                        val screenWidthIntSrc = bkRelative.measuredWidth - dialogMargin
                        if(screenWidthIntSrc <= 0) {
                            delay(100)
                            continue
                        }
                        return@screenWidthInt screenWidthIntSrc
                    }
                    0
                }.let {
                    if(it <= 0) return@withContext
                    it
                }

                val frequencyMapList = statisticsTitleList.groupBy { it }
                    .mapValues { it.value.size }
                    .filterKeys { !it.isNullOrEmpty() }
                    .toList()
                    .sortedByDescending { (_, value) -> value }
                    .toMap()

//表示する色を設定
                val value: ArrayList<PieEntry> = ArrayList()
                frequencyMapList.forEach { frequencyMap ->
                    value.add(
                        PieEntry(
                            frequencyMap.value.toFloat(),
                            frequencyMap.key
                        )
                    )
                }
                val srcColorList = listOf(
                    CmdClickColorStr.LIGHT_GREEN,
//                    CmdClickColorStr.WHITE_GREEN,
                    CmdClickColorStr.ANDROID_GREEN,
//                    CmdClickColorStr.YELLOW_GREEN,
                    CmdClickColorStr.GREEN,
//                    CmdClickColorStr.THICK_GREEN,
//                    CmdClickColorStr.DARK_GREEN,
//                    CmdClickColorStr.CARKI,
//                    CmdClickColorStr.GOLD_YELLOW,
                    CmdClickColorStr.WATER_BLUE,
//                    CmdClickColorStr.WHITE_BLUE,
//                    CmdClickColorStr.THICK_AO,
//                    CmdClickColorStr.BLACK_AO,
                    CmdClickColorStr.BLUE,
//                    CmdClickColorStr.WHITE_BLUE_PURPLE,
//                    CmdClickColorStr.BLUE_DARK_PURPLE,
//                    CmdClickColorStr.NAVY,
                    CmdClickColorStr.PURPLE,
                    CmdClickColorStr.ORANGE,
//                    CmdClickColorStr.BROWN,
//                    CmdClickColorStr.DARK_BROWN,
//                    CmdClickColorStr.YELLOW,
                    CmdClickColorStr.SKERLET,
                )
                val color: ArrayList<Int> = ArrayList()
                val colorStrList = srcColorList.map {
                    it.str
                }
//                CmdClickColorStr.values()
                val colorList = mutableListOf<String>()
                frequencyMapList.forEach {
                    val colorStr = colorStrList.shuffled().first()
                    colorList.add(colorStr)
                    color.add(Color.parseColor(colorStr))
                }
                val oneSideLengthRndList = (300..(7 * screenWidthInt) / 4)
                val holeRadiasHolePercentRndList = (0..75)
                val rotateAngleRndList = (-270..270)
                val alphaRndList = (100..200) //(100..400)
                val repeatTimes = (10..15).random()
                val animationTriggerEndNum = 8
                val animationTriggerRndList = (1..animationTriggerEndNum)
                val xyDurationSrcList = (200..20000)
                (0..repeatTimes).forEach {
                    _ ->
                    //グラフのデータを設定

                    //chartに設定
                    val dataSet = PieDataSet(value, "sample")
//                    dataSet.isUsingSliceColorAsValueLineColor = true

//                    value.setValueTextColor(Color.BLACK);
//                    dataSet.setValueLinePart1OffsetPercentage(90.0f);
//                    dataSet.setValueLinePart1Length(1f);
//                    dataSet.setValueLinePart2Length(.2f);
//                    dataSet.valueTextColor = Color.BLACK;
//                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

                    dataSet.colors = color
                    dataSet.valueTextSize = 9f
                    withContext(Dispatchers.Main) createPie@{
                        val oneSideLength = oneSideLengthRndList.random()
                        val relativeParam = RelativeLayout.LayoutParams(
                            oneSideLength,
                            oneSideLength,
                        )
                        val pieChart = PieChart(context).apply {
                            layoutParams = relativeParam
                            x = (-oneSideLength..screenWidthInt).random().toFloat()
                            y = (-oneSideLength..screenHeightInt).random().toFloat()
                            description?.isEnabled = false
                            legend?.isEnabled = false
                            setTouchEnabled(false)
//                            val high = Highlight(highlightIndex, 0, 0)
//                            high.dataIndex = 0
//                            highlightValue(null)

                            isDrawHoleEnabled = true // 真ん中に穴を空けるかどうか
                            val holeRadiusFloat = holeRadiasHolePercentRndList.random().toFloat()
                            holeRadius = holeRadiusFloat //50f;       // 真ん中の穴の大きさ(%指定)
                            val transparentCircleRadiusFloat =
                                (holeRadiusFloat.toInt()..100).random().toFloat()
                            transparentCircleRadius = transparentCircleRadiusFloat //55f
                            val rotationAngleEnd = rotateAngleRndList.random().toFloat()
                            rotationAngle = rotationAngleEnd
//
                            //270F          // 開始位置の調整
                            isRotationEnabled = true

                            val alphaFloat = alphaRndList.random().toFloat() / 1000
                            alpha = alphaFloat

                            setEntryLabelTextSize(13f)
                            isSelected = false

                            data = PieData(dataSet)
                            // refresh
                        }
                        bkRelative.addView(pieChart)
                        CoroutineScope(Dispatchers.Main).launch {
                            withContext(Dispatchers.Main) {
                                pieChart.invalidate()
                            }
                            if(
                                animationTriggerRndList.random() < 7
                            ) return@launch
                            withContext(Dispatchers.Main) {
                                val durationMilliisX = xyDurationSrcList.random()
                                val durationMilliisY = xyDurationSrcList.random()
                                pieChart.animateXY(durationMilliisX, durationMilliisY)
                            }
                        }
                    }
                }
            }
        }

        fun makeStatisticsTextFile(
            fannelDirPath: String,
            saveTagName: String?,
        ): File {
            return when(saveTagName.isNullOrEmpty()) {
                true -> File("${fannelDirPath}/${statisticsName}/promptWithList.txt")
                else -> listOf(
                    fannelDirPath,
                    statisticsName,
                    saveTagName,
                    "promptWithList.txt",
                ).joinToString("/")
                    .replace(Regex("[/]+"), "/")
                    .let {
                        File(it)
                    }
            }
        }

        fun saveStatistics(
            fannelDirPath: String,
            saveTagName: String?,
            trimedReturnValue: String,
        ) {
            val statisticsFile = makeStatisticsTextFile(
                fannelDirPath,
                saveTagName,
            )
            val statisticsTitle = trimedReturnValue.replace(
                statisticsMapSeparator.toString(),
                String()
            )
            val insertLine = listOf(
                "${StatisticsKey.TITLE.key}=${statisticsTitle}",
                "${StatisticsKey.DATETIME.key}=${LocalDateTime.now()}",
            ).joinToString(statisticsMapSeparator.toString())
            val updateStatisticsCon = ReadText(
                statisticsFile.absolutePath
            ).textToList() + listOf(insertLine)
            val historyTakeNum = 500
            FileSystems.writeFile(
                statisticsFile.absolutePath,
                updateStatisticsCon
                    .takeLast(historyTakeNum)
                    .joinToString("\n")
            )
        }

        fun makeStatisticsMapList(
            fannelDirPath: String,
            saveTagName: String?,
            promptMapList: List<Map<String, String?>>,
        ): List<String?> {
            val statisticsTxtFile = makeStatisticsTextFile(
                fannelDirPath,
                saveTagName,
            )
            val statisticsMapList = ReadText(
                statisticsTxtFile.absolutePath
            ).textToList().filter{
                it.isNotEmpty()
            }.map {
                CmdClickMap.createMap(
                    it,
                    statisticsMapSeparator
                ).toMap()
            }
            val titleKey = StatisticsKey.TITLE.key
            val frequencyMapListSrc = statisticsMapList.map{
                    elMap ->
                elMap.get(titleKey)
            }
            val promptList = promptMapList.map {
                    lineMap ->
                lineMap.get(
                    PromptMapList.PromptListKey.TITLE.key
                )
            }
            val statistiscTitleList = frequencyMapListSrc + promptList
            return statistiscTitleList.ifEmpty {
                listOf(
                    "CC",
                    "cmdclick",
                    "CommandClick",
                )
            }
        }
    }

    private object ColdListRegister {


        fun register(
            fannelDirPath: String,
            promptListFile: File?,
            listLimit: Int?,
            disableUpdate: Boolean,
            returnValue: String,
            itemIconStr: String?,
        ) {
            val trimedReturnValue =
                returnValue.trim()
            if (
                trimedReturnValue.isEmpty()
            ) return
            StatisticsTool.saveStatistics(
                fannelDirPath,
                promptListFile?.name?.let {
                    CcPathTool.trimAllExtend(it)
                },
                trimedReturnValue,
            )
            if(
                promptListFile == null
                || disableUpdate
                ) return
            val updatePromptList =
                        makeNoEmptyList(
                            trimedReturnValue,
                            itemIconStr,
                            promptListFile,
                        ).distinct().let { listSrc ->
                            when (listLimit == null) {
                                true -> listSrc
                                else -> listSrc.take(listLimit)
                            }
                        }
           val lineMapCon = updatePromptList.map {
                   lineMap ->
               val title = lineMap.get(
                   PromptMapList.PromptListKey.TITLE.key
               ) ?: String()
               val iconStr = lineMap.get(
                   PromptMapList.PromptListKey.ICON.key
               ) ?: String()
               listOf(
                   title,
                   iconStr
               ).joinToString(PromptMapList.promptListSeparator.toString())
           }.joinToString("\n")
            FileSystems.writeFile(
                promptListFile.absolutePath,
                lineMapCon
            )
        }

        private fun makeNoEmptyList(
            trimedReturnValue: String,
            iconStr: String?,
            promptListFile: File?,
        ): List<Map<String, String>> {
            val titleKey = PromptMapList.PromptListKey.TITLE.key
            val iconKey = PromptMapList.PromptListKey.ICON.key
            val curMapList = when(promptListFile == null) {
                true -> emptyList()
                else -> ReadText(
                    promptListFile.absolutePath
                ).textToList().map { titleToIconStr ->
                    val titleToIconStrList = titleToIconStr.split(
                        PromptMapList.promptListSeparator,
                    )
                    val curTitle = titleToIconStrList.firstOrNull() ?: String()
                    val curConStr = titleToIconStrList.getOrNull(1) ?: String()
                    mapOf(
                        titleKey to curTitle,
                        iconKey to curConStr
                    )
                }
            }
            if(
                trimedReturnValue.isEmpty()
            ) return curMapList
            val selectedMap = when(
                iconStr.isNullOrEmpty()
            ){
                true -> mapOf(
                    titleKey to trimedReturnValue,
                )
                else -> mapOf(
                    titleKey to trimedReturnValue,
                    iconKey to iconStr
                )
            }
            val selectedTitle = selectedMap.get(titleKey)
//            FileSystems.writeFile(
//                File(UsePath.cmdclickDefaultAppDirPath, "list_makeNoEmptyList.txt").absolutePath,
//                listOf(
//                    "promptListFilePath: ${promptListFile.absolutePath}",
//                    "promptListFileCon: ${ReadText(
//                        promptListFile.absolutePath
//                    ).textToList()}",
//                    "trimedReturnValue: ${trimedReturnValue}",
//                    "curMapList: ${curMapList}",
//                    "curMap: ${selectedMap}",
//                ).joinToString("\n")
//            )
            return listOf(selectedMap) + curMapList.filter {
                lineMap ->
                val title = lineMap.get(titleKey)
                title != selectedTitle
            }
        }
    }

    private fun makeListTextFileName(
        variableName: String?,
    ): String? {
        if(
            variableName.isNullOrEmpty()
            ) return null
        val prefixUpperVariableName = variableName.replaceFirstChar { it.uppercase() }
        return "${listPrefix}${prefixUpperVariableName}${listTxtSuffix}"
    }

}

private object EditTextMakerForPromptList {
    fun make(
        promptDialogObj: Dialog?,
        editTextMap: Map<String, String>,
        setText: String?,
        visible: Boolean,
    ): AppCompatEditText? {
        val promptEditText =
            promptDialogObj?.findViewById<AppCompatEditText>(
                R.id.prompt_list_dialog_search_edit_text
            ) ?: return null
        promptEditText.setText(setText)
        editTextMap.get(
            PromptWithListDialog.Companion.PromptEditTextKey.hint.name
        )?.let {
            promptEditText.hint = it
        }
//        if(!visible) {
            promptDialogObj.findViewById<MaterialCardView>(
                R.id.prompt_list_dialog_search_edit_cardview
            )?.apply {
                isVisible = visible
            }
//            promptEditText.isVisible = false
//            return promptEditText
//        }
        if(!visible) {
            return promptEditText
        }
        val isFocus = editTextMap.get(
            PromptWithListDialog.Companion. PromptEditTextKey.onFocus.name
        ) == PromptWithListDialog.switchOn
        if(isFocus) {
            promptEditText.requestFocus()
        }
        return promptEditText
    }

    fun makeTextByShell(
        terminalFragment: TerminalFragment,
        editTextMap: Map<String, String>,
    ): String? {
        val context = terminalFragment.context
            ?: return null
        val mainOrSubFannelPath = editTextMap.get(
            PromptWithListDialog.Companion.PromptEditTextKey.fannelPath.name
        )
        val setReplaceVariableMap = when(
            mainOrSubFannelPath.isNullOrEmpty()
        ){
            true -> emptyMap()
            else -> SetReplaceVariabler
                .makeSetReplaceVariableMapFromSubFannel(
                    context,
                    mainOrSubFannelPath
                )
        }
        val fannelInfoMap = FannelInfoTool.getFannelInfoMap(
            terminalFragment,
            mainOrSubFannelPath
        )
//        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
//            fannelInfoMap
//        )
        val currentFannelName = FannelInfoTool.getCurrentFannelName(
            fannelInfoMap
        )

        val shellCon = editTextMap.get(
            PromptWithListDialog.Companion.PromptEditTextKey.shellPath.name
        )?.let {
            EditSettingExtraArgsTool.makeShellCon(editTextMap)
        }?.let {
            SetReplaceVariabler.execReplaceByReplaceVariables(
                it,
                setReplaceVariableMap,
//                currentAppDirPath,
                currentFannelName
            )
        } ?: return null
        val busyboxExecutor = BusyboxExecutor(
            context,
            UbuntuFiles(context),
        )
        val repValMap = editTextMap.get(
            PromptWithListDialog.Companion.PromptEditTextKey.repValCon.name
        ).let {
            CmdClickMap.createMap(
                it,
                '&'
            )
        }.toMap()
        return busyboxExecutor.getCmdOutput(
            shellCon,
            repValMap
        )
    }
}


class LinearGradientSpan(
    private val colors: IntArray,
    private val positions: FloatArray?,
    private val angle: Int
) :
    ReplacementSpan() {
    override fun getSize(
        paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        fm: FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        paint: Paint
    ) {
        val width = paint.measureText(text, start, end)
        val height = (bottom - top).toFloat()
        val radians = Math.toRadians(angle.toDouble())
        val gradientX = (Math.cos(radians) * width).toFloat()
        val gradientY = (Math.sin(radians) * height).toFloat()
        val gradient = LinearGradient(
            x,
            y.toFloat(),
            x + gradientX,
            y + gradientY,
            colors,
            positions,
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        canvas.drawText(text, start, end, x, y.toFloat(), paint)
    }
}

class ShadowSpan(
    private val radius: Float,
    private val dx: Float,
    private val dy: Float,
    private val shadowColor: Int
) :
    CharacterStyle(), UpdateAppearance {
    override fun updateDrawState(tp: TextPaint) {
        tp.setShadowLayer(radius, dx, dy, shadowColor)
    }
}
