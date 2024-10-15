package com.puutaro.commandclick.fragment_lib.terminal_fragment.js_interface.lib.dialog

import android.app.Dialog
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Paint.FontMetricsInt
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.BitmapDrawable
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
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.puutaro.commandclick.R
import com.puutaro.commandclick.activity_lib.event.lib.terminal.ExecSetToolbarButtonImage
import com.puutaro.commandclick.common.variable.res.CmdClickColor
import com.puutaro.commandclick.common.variable.res.CmdClickColorStr
import com.puutaro.commandclick.common.variable.res.CmdClickIcons
import com.puutaro.commandclick.common.variable.res.FannelIcons
import com.puutaro.commandclick.component.adapter.PromptListAdapter
import com.puutaro.commandclick.custom_manager.PreLoadLayoutManager
import com.puutaro.commandclick.custom_view.OutlineTextView
import com.puutaro.commandclick.fragment.TerminalFragment
import com.puutaro.commandclick.fragment_lib.terminal_fragment.ButtonImageCreator
import com.puutaro.commandclick.proccess.edit.lib.SetReplaceVariabler
import com.puutaro.commandclick.proccess.js_macro_libs.edit_setting_extra.EditSettingExtraArgsTool
import com.puutaro.commandclick.proccess.ubuntu.BusyboxExecutor
import com.puutaro.commandclick.proccess.ubuntu.UbuntuFiles
import com.puutaro.commandclick.util.CcPathTool
import com.puutaro.commandclick.util.file.FileSystems
import com.puutaro.commandclick.util.file.ReadText
import com.puutaro.commandclick.util.image_tools.BitmapTool
import com.puutaro.commandclick.util.map.CmdClickMap
import com.puutaro.commandclick.util.state.FannelInfoTool
import com.puutaro.commandclick.util.str.QuoteTool
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.io.File
import java.lang.ref.WeakReference
import java.time.LocalDateTime


class PromptWithListDialog(
    private val terminalFragmentRef: WeakReference<TerminalFragment>
) {
    private var returnValue = String()
    private var promptDialogObj: Dialog? = null

    companion object {
        private val listPrefix = "list"
        private val listDirName = "${listPrefix}Text"
        private val listTxtSuffix = ".txt"
        private val mapSeparator = ','
        private val firstSeparator = '|'
        const val secondSeparator = '?'
        private var onDialog = false
        private val switchOn = "ON"
        private val switchOff = "OFF"
    }

    fun create(
        fannelPath: String,
        title: String,
        listOrDefoTxtVars: String,
    ): String {
        val fannelFile = File(fannelPath)
        if(
            !fannelFile.isFile
        ) return String()
        val terminalFragment = terminalFragmentRef.get()
            ?: return String()
        onDialog = true
        returnValue = String()
        runBlocking {
            withContext(Dispatchers.Main) {
                try {
                    execCreate(
                        terminalFragment,
                        fannelPath,
                        title,
                        listOrDefoTxtVars,
                    )
                } catch (e: Exception){
                    Log.e(this.javaClass.name, e.toString())
                }
            }
            withContext(Dispatchers.IO) {
                while (true) {
                    delay(100)
                    if (
                        !onDialog
                    ) break
                }
            }
        }
        return returnValue
    }


    private fun execCreate(
        terminalFragment: TerminalFragment,
        fannelPath: String,
        title: String,
        listOrDefoTxtVars: String,
    ) {
        val context = terminalFragment.context
            ?: return

        promptDialogObj = Dialog(
            context,
            R.style.BottomSheetDialogTheme
        )
        val randEndNum = 5
        val isWhiteBackgrond =
            (1..randEndNum).random() % randEndNum == 0
        promptDialogObj?.setContentView(
            R.layout.prompt_list_dialog_layout
        )
        val holderConstraint = promptDialogObj?.findViewById<ConstraintLayout>(
            R.id.prompt_list_dialog_constraint
        )
        val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
            R.id.prompt_list_dialog_bk_relative
        ) ?: return
        val promptListTitleView = promptDialogObj?.findViewById<OutlineTextView>(
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
            val whiteColorStr = "#ffffff"
            val color1 = colorList.random()
            val color2 = runBlocking {
                if(
                    !isWhiteBackgrond
                ) return@runBlocking whiteColorStr
                var color2Str = String()
                for(i in 1..5) {
                    color2Str = colorList.random()
                    if (
                        color2Str != color1
                    ) return@runBlocking color2Str
                }
                color2Str
            }
            val alreadyColorList = listOf(
                color1,
                color2,
            )
            val color3 = runBlocking {
                var color3Str = String()
                for(i in 1..5) {
                    color3Str = colorList.random()
                    if (
                        !alreadyColorList.contains(color3Str)
                    ) return@runBlocking color3Str
                }
                color3Str
            }
            val colors = intArrayOf(
                Color.parseColor(color1),
                Color.parseColor(color2),
                Color.parseColor(whiteColorStr),
                Color.parseColor(color3)
            ) // Define your gradient colors

//            val positions = floatArrayOf(0.5f, 0.1f) // Define color positions

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
        }
        val promptListTotalMap = CmdClickMap.createMap(
            listOrDefoTxtVars,
            mapSeparator,
        ).toMap()
        val editTextMap = CmdClickMap.createMap(
            promptListTotalMap.get(PromptWithTextMapKey.editText.name),
            firstSeparator
        ).toMap()
        val promptListMap = CmdClickMap.createMap(
            promptListTotalMap.get(PromptWithTextMapKey.list.name),
            firstSeparator
        ).toMap()
        val fannelDirPath = CcPathTool.getMainFannelDirPath(fannelPath)
        val listDirPath = "${fannelDirPath}/${listDirName}"

        val variableName = promptListMap.get(PromptListVars.variableName.name)
        val listTxtName = makeListTextFileName(
            variableName,
        )
        val promptListFile =
            File(listDirPath, listTxtName)

        val setTextSrc = editTextMap.get(
            PromptEditTextKey.default.name
        ) ?: String()
        val setText = when(setTextSrc.isEmpty()){
            true -> EditTextMakerForPromptList.makeTextByShell(
                terminalFragment,
                editTextMap
            )
            else -> setTextSrc
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
        val filterText = when(disableListBind || !editTextVisible){
            true -> String()
            else -> setText ?: String()
        }
        val promptList =
            PromptList.makePromptList(
                promptListFile,
                promptListMap,
                filterText,
                listLimit,
            )
        val promptListView = promptDialogObj?.findViewById<RecyclerView>(
            R.id.prompt_list_dialog_list_view
        )
        CoroutineScope(Dispatchers.Main).launch {
            val promptEditText = withContext(Dispatchers.Main) {
                EditTextMakerForPromptList.make(
                    promptDialogObj,
                    editTextMap,
                    setText,
                    editTextVisible,
                )
            }
            withContext(Dispatchers.Main) {
                val promptCancelButton = promptDialogObj?.findViewById<AppCompatImageView>(
                    R.id.prompt_list_dialog_cancel
                )

                promptCancelButton?.apply {
                    ExecSetToolbarButtonImage.setImageButton(
                        this,
                        CmdClickIcons.CANCEL
                    )
                    setOnClickListener {
                        returnValue = String()
                        promptDialogObj?.dismiss()
                        promptDialogObj = null
                        onDialog = false
                    }
                }
            }
            CoroutineScope(Dispatchers.Main).launch{

                withContext(Dispatchers.Main) {
                    if (
                        isWhiteBackgrond
                    ) return@withContext

                    val bkImageView = promptDialogObj?.findViewById<AppCompatImageView>(
                        R.id.prompt_list_dialog_list_bk_image
                    ) ?: return@withContext
                    val bitmap = withContext(Dispatchers.IO) {
                        val colorStrList = CmdClickColorStr.values().map { it.str }
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
                            colorIntArray
                        )
                    }
                    bkImageView.setImageBitmap(bitmap)
                }
            }
            val promptListAdapter = withContext(Dispatchers.Main) {
                PromptListAdapter(
                    context,
                    promptList,
                    isWhiteBackgrond
                )
            }
            val listVisible =
                withContext(Dispatchers.IO) {
                    promptListMap.get(PromptListVars.visible.name) != switchOff
                }
            withContext(Dispatchers.Main) {
                promptDialogObj?.setOnCancelListener {
                    exitDialog(
                        fannelDirPath,
                        holderConstraint,
                        bkRelative,
                        promptListView,
                        String(),
                        promptListFile,
                        listLimit,
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
                promptDialogObj?.findViewById<AppCompatImageView>(
                    R.id.prompt_list_dialog_ok
                )?.apply {
                    ExecSetToolbarButtonImage.setImageButton(
                        this,
                        CmdClickIcons.OK
                    )
                    setOnClickListener {
                        val inputEditable = promptEditText?.text
                        if (
                            inputEditable.isNullOrEmpty()
                        ) {
                            exitDialog(
                                fannelDirPath,
                                holderConstraint,
                                bkRelative,
                                promptListView,
                                String(),
                                promptListFile,
                                listLimit,
                            )
                            return@setOnClickListener
                        } else returnValue = inputEditable.toString()
                        exitDialog(
                            fannelDirPath,
                            holderConstraint,
                            bkRelative,
                            promptListView,
                            inputEditable.toString(),
                            promptListFile,
                            listLimit,
                        )
                    }
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
                )
            }
            withContext(Dispatchers.Main) {
                setPromptEditText(
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
                )
            }
//            withContext(Dispatchers.Main){
//                scrollToBottom(
//                    promptListView,
//                    promptListAdapter,
//                )
//            }
        }
        promptDialogObj?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        promptDialogObj?.window?.setGravity(
            Gravity.BOTTOM
        )
        promptDialogObj?.show()

        CoroutineScope(Dispatchers.Main).launch {
            StatisticsTool.displayStatisticsBk(
                terminalFragmentRef,
                promptDialogObj,
                fannelDirPath,
                promptList,
            )
        }
        KeyboardHandler.handle(
            terminalFragment,
            promptDialogObj,
            promptListTitleView,
            promptListView,
            editTextVisible,
        )
    }

//    private suspend fun scrollToBottom(
//        promptListView: RecyclerView?,
//        promptListAdapter: PromptListAdapter,
//    ){
//        delay(200)
//        promptListView?.scrollToPosition(promptListAdapter.itemCount - 1)
//    }

    private object KeyboardHandler {
        fun handle(
            terminalFragment: TerminalFragment,
            promptDialogObj: Dialog?,
            promptListTitleView: AppCompatTextView?,
            promptListView: RecyclerView?,
            editTextVisible: Boolean,
        ){
            val context = terminalFragment.context
                ?: return
            val searchCardView = promptDialogObj?.findViewById<CardView>(
                R.id.prompt_list_dialog_search_edit_cardview
            )
            val cancelImageView = promptDialogObj?.findViewById<AppCompatImageView>(
                R.id.prompt_list_dialog_cancel
            )
            val okImageView = promptDialogObj?.findViewById<AppCompatImageView>(
                R.id.prompt_list_dialog_ok
            )
            val searchCardViewVerticalMarginNormalDp = context.resources.getDimension(
                R.dimen.prompt_list_dialog_vertical_search_cardview_margin
            ).toInt()
//            .let {
//            ScreenSizeCalculator.toDp(
//                context,
//                it
//            )
//        }
            okImageView?.isVisible = editTextVisible
            val searchCardViewVerticalMarginKeyOpenDp = context.resources.getDimension(
                R.dimen.prompt_list_dialog_vertical_search_cardview_key_open_margin
            ).toInt()
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
                                promptListTitleView?.alpha = 0.4f
                                val constraintLayout = promptListView?.layoutParams as? ConstraintLayout.LayoutParams
                                constraintLayout?.apply {
                                    topToTop = ConstraintSet.PARENT_ID
                                    topToBottom = ConstraintLayout.LayoutParams.UNSET
                                    bottomMargin = searchCardViewVerticalMarginKeyOpenDp
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
                                cancelImageView?.isVisible = false
                                okImageView?.isVisible = false

                            }
                            else -> {
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
                                cancelImageView?.isVisible = true
                                okImageView?.isVisible = true && editTextVisible
                            }
                        }
                    }
                )
            }
        }
    }


    private object PromptList {
        fun makePromptList(
            promptListFile: File,
            promptListMap: Map<String, String>,
            filterString: String,
            listLimit: Int?,
        ): MutableList<String> {
            val mainList = ReadText(
                promptListFile.absolutePath
            ).textToList()
            val comcatFilePathList = makeExtraList(
                promptListMap.get(PromptListVars.concatFilePathList.name)?.let {
                    QuoteTool.splitBySurroundedIgnore(
                        it,
                        secondSeparator
                    )
                }
            )
            val promptListByComcatFilePathList = mainList + comcatFilePathList.filter {
                !mainList.contains(it)
            }
            val concatList = makeExtraListFromCon(
                promptListMap.get(PromptListVars.concatList.name)?.let {
                    QuoteTool.trimBothEdgeQuote(it)
                }
            )
            val promptList = promptListByComcatFilePathList + concatList.filter {
                !promptListByComcatFilePathList.contains(it)
            }
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lPrompt_make.txt").absolutePath,
//            listOf(
//                "mainList: ${mainList}",
//                "srcListEntry: ${comcatFilePathList}",
//                "srcListEntryFromCon: ${concatList}",
//                "promptListSrc: ${promptListByComcatFilePathList}",
//                "promptList: ${promptList}",
//            ).joinToString("\n")
//        )
            return when (filterString.isEmpty()) {
                true -> promptList
                else -> promptList.distinct().filter { line ->
                    Regex(
                        filterString
                            .lowercase()
                            .replace("\n", "")
                    ).containsMatchIn(
                        line.lowercase()
                    )
                }
            }.let { listSrc ->
                when (listLimit == null) {
                    true -> listSrc
                    else -> listSrc.take(listLimit)
                }.toMutableList()
            }
        }

        private fun makeExtraList(
            listConcatFilePathList: List<String>?,
        ): List<String> {
            if (
                listConcatFilePathList.isNullOrEmpty()
            ) return emptyList()
            return listConcatFilePathList.map {
                ReadText(it).textToList()
            }.flatten().filter { it.trim().isNotEmpty() }

        }

        private fun makeExtraListFromCon(
            concatList: String?,
        ): List<String> {
            if (
                concatList.isNullOrEmpty()
            ) return emptyList()
            return concatList.split(secondSeparator)

        }
    }

    private fun setPromptEditText(
        promptEditText: AppCompatEditText?,
        promptListFile: File,
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
                val updatePromptList = PromptList.makePromptList(
                    promptListFile,
                    editTextMap,
                    promptEditText.text.toString(),
                    listLimit,
                )
                promptListAdapter.promptList.clear()
                promptListAdapter.promptList.addAll(updatePromptList)
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
        promptListFile: File,
        listLimit: Int?,
    ){
        promptEditText?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent): Boolean {
                if (event.action != KeyEvent.ACTION_DOWN ||
                    keyCode != KeyEvent.KEYCODE_ENTER
                ) return false
                val currentInputEditable = promptEditText.text
                if(promptEditText.text.isNullOrEmpty()){
                    exitDialog(
                        fannelDirPath,
                        holderLinear,
                        bkRelative,
                        promptListView,
                        String(),
                        promptListFile,
                        listLimit,
                    )
                    return false
                }
                exitDialog(
                    fannelDirPath,
                    holderLinear,
                    bkRelative,
                    promptListView,
                    currentInputEditable.toString(),
                    promptListFile,
                    listLimit,
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
        promptListFile: File,
        listLimit: Int?,
    ){
        val onInsertByClick = promptListMap.get(
            PromptListVars.onInsertByClick.name
        ) == switchOn
        val onDismissByClick = promptListMap.get(
            PromptListVars.onDismissByClick.name
        ) == switchOn
        promptListAdapter.itemClickListener = object: PromptListAdapter.OnItemClickListener{
            override fun onItemClick(holder: PromptListAdapter.PromptListViewHolder) {
                val itemStr = holder.itemStr
                if(onInsertByClick) {
                    promptEditText?.setText(itemStr)
                }
                if(
                    !onDismissByClick
                ) return
                exitDialog(
                    fannelDirPath,
                    holderConstraint,
                    bkRelative,
                    promptListView,
                    itemStr,
                    promptListFile,
                    listLimit,
                )
            }
        }
    }

    private fun exitDialog(
        fannelDirPath: String,
        holderConstraint: ConstraintLayout?,
        bkRelative: RelativeLayout?,
        promptListView: RecyclerView?,
        returnStr: String,
        promptListFile: File,
        listLimit: Int?,
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
            returnValue
        )
        promptDialogObj?.dismiss()
        promptDialogObj = null
        onDialog = false
    }

    private object StatisticsTool {

        val statisticsName = "statistics"
        val statisticsMapSeparator = ','

        enum class StatisticsKey(
            val key: String
        ){
            TITLE("title"),
            DATETIME("datetime"),
        }

        suspend fun displayStatisticsBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            fannelDirPath: String,
            promptList: List<String>,
        ){
            val handleRnd = (1..6).random()
            when(true) {
                (handleRnd <= 2) -> makeMonocroBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    fannelDirPath,
                    promptList,
                )
                (handleRnd <= 4) -> makeWebBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    fannelDirPath,
                    promptList,
                )
                else -> makePieBk(
                    terminalFragmentRef,
                    promptDialogObj,
                    fannelDirPath,
                    promptList
                )
            }
        }

        private suspend fun makeMonocroBk(
            terminalFragmentRef: WeakReference<TerminalFragment>,
            promptDialogObj: Dialog?,
            fannelDirPath: String,
            promptList: List<String>,
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredHeight - dialogMargin
                }
                val screenHeightInt = screenHeightFloat.toInt()
                val screenWidthFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredWidth - dialogMargin
                }

                val screenWidthInt = screenWidthFloat.toInt()

                val statisticsTxtFile = makeStatisticsTextFile(
                    fannelDirPath
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
                val decentTextToFreqList = (frequencyMapListSrc + promptList).groupBy { it }
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
                val alphaRndList = (100..400)
                val repeatTimes = (7..10).random()
                val textSizeEnd = 11
                val textSizeDiff = textSizeEnd / decentTextList.size
                val textToFloatMap = decentTextList.mapIndexed {
                        index, text ->
                    text to (textSizeEnd - textSizeDiff * index).toFloat()
                }.toMap()

                val durationList = (0..100)
                val fixRotationAngle = rotateAngleRndList.random().toFloat()

                val colorIntArray = listOf(
                    CmdClickColorStr.DARK_GREEN.str,
                    CmdClickColorStr.DARK_GREEN.str
                ).map {
                    Color.parseColor(it)
                }.toIntArray()
                val bitMap = BitmapTool.GradientBitmap.makeGradientBitmap2(
                    100,
                    100,
                    colorIntArray
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
                            val oneSideLength = textToOneSideLengthMap.get(curText)?.random() ?: screenWidthInt
//                            oneSideLengthRndList.random()
                            val relativeParam = RelativeLayout.LayoutParams(
                                oneSideLength,
                                oneSideLength,
                            )
                            layoutParams = relativeParam
                            alpha = alphaRndList.random().toFloat() / 1000
                            rotation = fixRotationAngle
                            val putXyPair = runBlocking {
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
                                    ) return@runBlocking putXyPairSrc
                                    if (
                                        (1..3).random() == 1
                                    ) return@runBlocking putXyPairSrc
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
                                    setImageBitmap(bitMap)
                                    val duration = durationList.random()

                                    Glide
                                        .with(context)
                                        .load(bitMap)
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
                    val iconId = FannelIcons.values().map { it.id }.shuffled().first()
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
                        alpha = 0.6f
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
            fannelDirPath: String,
            promptList: List<String>,
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredHeight - dialogMargin
                }
                val screenHeightInt = screenHeightFloat.toInt()
                val screenWidthFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredWidth - dialogMargin
                }

                val screenWidthInt = screenWidthFloat.toInt()

                val statisticsTxtFile = makeStatisticsTextFile(
                    fannelDirPath
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
                val decentTextToFreqList = (frequencyMapListSrc + promptList).groupBy { it }
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
                val colorIdList = CmdClickColor.values().map {
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
                    colorIntArray
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
                    val text = it.key ?: String()
                    val srcBitmap = it.value ?: return@map text to defaultBkBitmap

                    val cutBitmap = BitmapTool.ImageRemaker.cut(
                        srcBitmap,
                        bitmapCutWidth,
                        (bitmapCutWidth * 1.73).toInt()
                    )
                    text to cutBitmap
                }.filter {
                    it.first.isNotEmpty()
                }.toMap()
                val oneSideLengthRndList = (100..((2 * screenWidthInt) / 10 ))
                val screenWidth = (10 * screenWidthInt) / 10 + 500
                val oneSideLengthDiff = (screenWidth) / decentTextList.size
                val textToOneSideLengthMap = decentTextList.mapIndexed {
                        index, text ->
                    val startInt = screenWidth - oneSideLengthDiff * (index + 1)
                    val endInt = screenWidth - oneSideLengthDiff * index
                    text to (startInt..endInt)
                }.toMap()
                val rotateAngleRndList = (-270..270)
                val alphaRndList = (100..400)
                val repeatTimes = (5..10).random()
                val textSizeEnd = 30
                val textSizeDiff = textSizeEnd / decentTextList.size
                val textToFloatMap = decentTextList.mapIndexed {
                        index, text ->
                    text to (textSizeEnd - textSizeDiff * index).toFloat()
                }.toMap()

                val thetaDiff = 360f / repeatTimes

//                FileSystems.writeFile(
//                    File(UsePath.cmdclickDefaultAppDirPath, "ltextView.txt").absolutePath,
//                    listOf(
//                        "textToIntRangeMap: ${textToIntRangeMap}"
//                    ).joinToString("\n")
//                )
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
                            val scaleWidth = textToOneSideLengthMap.get(curText)?.random() ?: screenWidthInt
                            val scaleHeight = (scaleWidth * 1.73).toInt()
                            val relativeParam = RelativeLayout.LayoutParams(
                                scaleWidth,
                                scaleHeight,
                            )
                            layoutParams = relativeParam
                            alpha = alphaRndList.random().toFloat() / 1000
                            rotation = fixRotationAngle + plusRotateList.random()
                            val margin = scaleWidth / 2
                            val putXyPair = runBlocking {
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
                                    ) return@runBlocking putXyPairSrc
                                    if (
                                        (1..3).random() == 1
                                    ) return@runBlocking putXyPairSrc
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
                                        BitmapTool.ImageRemaker.cut(
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
            fannelDirPath: String,
            promptList: List<String>
        ){
            val context = terminalFragmentRef.get()?.context
                ?: return
            withContext(Dispatchers.IO){
                //chartのコンポーネントを取得
                val bkRelative = promptDialogObj?.findViewById<RelativeLayout>(
                    R.id.prompt_list_dialog_bk_relative
                ) ?: return@withContext

                val dialogMargin = 200
                val screenHeightFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredHeight - dialogMargin
                }
                val screenHeightInt = screenHeightFloat.toInt()
                val screenWidthFloat = withContext(Dispatchers.Main) {
                    bkRelative.measuredWidth - dialogMargin
                }

                val screenWidthInt = screenWidthFloat.toInt()

                val statisticsTxtFile = makeStatisticsTextFile(
                    fannelDirPath
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
                val frequencyMapList = (frequencyMapListSrc + promptList).groupBy { it }
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
                val alphaRndList = (100..400)
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

                            isDrawHoleEnabled = true; // 真ん中に穴を空けるかどうか
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
                                val durationMiliSec = 800
                                pieChart.animateXY(durationMilliisX, durationMilliisY)
                            }
                        }
                    }
                }
            }
        }

        fun makeStatisticsTextFile(
            fannelDirPath: String
        ): File {
            return File("${fannelDirPath}/${statisticsName}/promptWithList.txt")
        }

        fun saveStatistics(
            fannelDirPath: String,
            trimedReturnValue: String,
        ) {
            val statisticsFile = makeStatisticsTextFile(
                fannelDirPath
            )
//        FileSystems.writeFile(
//            File(UsePath.cmdclickDefaultAppDirPath, "lStatistics.txt").absolutePath,
//            listOf(
//                "statisticsFile: ${statisticsFile.absolutePath}"
//            ).joinToString("\n")
//        )
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
    }

    private object ColdListRegister {


        fun register(
            fannelDirPath: String,
            promptListFile: File,
            listLimit: Int?,
            returnValue: String,
        ) {
            val trimedReturnValue =
                returnValue.trim()
            if (
                trimedReturnValue.isEmpty()
            ) return

            val promptListDirPath = promptListFile.parent
                ?: return
            StatisticsTool.saveStatistics(
                fannelDirPath,
                trimedReturnValue,
            )
            FileSystems.createDirs(
                promptListDirPath
            )
            val updatePromptList =
                listOf(trimedReturnValue) +
                        makeNoEmptyList(
                            trimedReturnValue,
                            promptListFile.name,
                            promptListDirPath,
                        ).filter {
                            trimedReturnValue != it
                        }.distinct().let { listSrc ->
                            when (listLimit == null) {
                                true -> listSrc
                                else -> listSrc.take(listLimit)
                            }
                        }
            FileSystems.writeFile(
                promptListFile.absolutePath,
                updatePromptList.joinToString("\n")
            )
        }

        private fun makeNoEmptyList(
            trimedReturnValue: String,
            listTxtName: String,
            listDirPath: String,
        ): List<String> {
            val curList = ReadText(
                File(
                    listDirPath,
                    listTxtName
                ).absolutePath
            ).textToList()
            if(
                trimedReturnValue.isNotEmpty()
            ) return listOf(trimedReturnValue) + curList
            return curList
        }
    }

    private fun makeListTextFileName(
        variableName: String?,
    ): String {
        val prefixUpperVariableName = variableName?.replaceFirstChar { it.uppercase() }
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
        if(
            !setText.isNullOrEmpty()
        ){
            promptEditText.setText(setText)
        }
        editTextMap.get(
            PromptEditTextKey.hint.name
        )?.let {
            promptEditText.hint = it
        }
        if(!visible) {
            promptEditText.isVisible = false
            return promptEditText
        }
        promptEditText.requestFocus()
        return promptEditText
    }

    fun makeTextByShell(
        terminalFragment: TerminalFragment,
        editTextMap: Map<String, String>,
    ): String? {
        val context = terminalFragment.context
            ?: return null
        val mainOrSubFannelPath = editTextMap.get(
            PromptEditTextKey.fannelPath.name
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
            PromptEditTextKey.shellPath.name
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
            PromptEditTextKey.repValCon.name
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


private enum class PromptWithTextMapKey {
    list,
    editText,
    background,
}

private enum class PromptEditTextKey {
    default,
    hint,
    shellPath,
    fannelPath,
    repValCon,
    disableListBind,
    visible,
}
private enum class PromptListVars {
    variableName,
    concatFilePathList,
    concatList,
    onInsertByClick,
    onDismissByClick,
    visible,
    limit,
}


object PromptBackground {
    private enum class Key {
        type
    }

    private enum class Type{
        sckelton
    }
}
class CustomGradientSpan(private val paint: TextPaint) : ReplacementSpan() {

    override fun getSize(paint: Paint, text: CharSequence, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(canvas: Canvas, text: CharSequence, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        canvas.drawText(text, start, end, x, y.toFloat(), this.paint)
    }
}

class LinearGradientSpan(
    private val colors: IntArray,
    private val positions: FloatArray?,
    private val angle: Int
) :
    ReplacementSpan() {
    override fun getSize(
        @NonNull paint: Paint,
        text: CharSequence,
        start: Int,
        end: Int,
        @Nullable fm: FontMetricsInt?
    ): Int {
        return paint.measureText(text, start, end).toInt()
    }

    override fun draw(
        @NonNull canvas: Canvas,
        text: CharSequence,
        start: Int,
        end: Int,
        x: Float,
        top: Int,
        y: Int,
        bottom: Int,
        @NonNull paint: Paint
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
