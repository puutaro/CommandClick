package com.puutaro.commandclick.util.tsv

//object TsvLineRenamer {
//
//    private var renamePromptDialog: Dialog? = null
//
//    fun rename(
//        editFragment: EditFragment,
//        tsvPath: String,
//        tsvLine: String,
//    ){
//        CoroutineScope(Dispatchers.Main).launch {
//            withContext(Dispatchers.Main) {
//                execRename(
//                    editFragment,
//                    tsvPath,
//                    tsvLine,
//                )
//            }
//        }
//    }
//
//    private fun execRename(
//        editFragment: EditFragment,
//        tsvPath: String,
//        lineMap: String,
//    ){
//        editFragment.disableKeyboardFragmentChange = true
//        val context = editFragment.context
//            ?: return
//
//        renamePromptDialog = Dialog(
//            context
//        )
//        renamePromptDialog?.setContentView(
//            R.layout.prompt_dialog_layout
//        )
//        val promptTitleTextView =
//            renamePromptDialog?.findViewById<AppCompatTextView>(
//                R.id.prompt_dialog_title
//            )
//        promptTitleTextView?.text = "Rename title"
//        val promptMessageTextView =
//            renamePromptDialog?.findViewById<AppCompatTextView>(
//                R.id.prompt_dialog_message
//            )
//        promptMessageTextView?.isVisible = false
//        val promptEditText =
//            renamePromptDialog?.findViewById<AutoCompleteTextView>(
//                R.id.prompt_dialog_input
//            )
//        val titleFileNameAndPathConPair =
//            TitleFileNameAndPathConPairForListIndexAdapter.get(lineMap)
//                ?: return
//        val fileNameOrTitle = titleFileNameAndPathConPair.first
//
//        promptEditText?.setText(
//            fileNameOrTitle
//        )
//        val promptCancelButton =
//            renamePromptDialog?.findViewById<AppCompatImageButton>(
//                R.id.prompt_dialog_cancel
//            )
//        promptCancelButton?.setOnClickListener {
//            dismissProcess(editFragment)
//        }
//        val promptOkButtonView =
//            renamePromptDialog?.findViewById<AppCompatImageButton>(
//                R.id.prompt_dialog_ok
//            )
//        promptOkButtonView?.setOnClickListener {
//            CoroutineScope(Dispatchers.Main).launch {
//                withContext(Dispatchers.Main) {
//                    ExecRenameProcess.rename(
//                        editFragment,
//                        tsvPath,
//                        lineMap,
//                        promptEditText,
//                    )
//                }
//                withContext(Dispatchers.Main) {
//                    dismissProcess(editFragment)
//                }
//            }
//        }
//        renamePromptDialog?.setOnCancelListener {
//            dismissProcess(editFragment)
//        }
//        renamePromptDialog?.window?.setLayout(
//            ViewGroup.LayoutParams.MATCH_PARENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )
//        renamePromptDialog?.window?.setGravity(
//            Gravity.BOTTOM
//        )
//        renamePromptDialog?.show()
//    }
//
//    private object ExecRenameProcess {
//        fun rename(
//            editFragment: EditFragment,
//            mapListPath: String,
//            lineMap: Map<String, String>,
//            promptEditText: AutoCompleteTextView?,
//
//            ) {
//            val inputEditable = promptEditText?.text
//            if (
//                inputEditable.isNullOrEmpty()
//            ) {
//                ToastUtils.showShort("No type item name")
//                return
//            }
////            val titleFileNameAndPathConPair =
////                TitleFileNameAndPathConPairForListIndexAdapter.get(lineMap)
////                    ?: return
//            val titleKey = ListSettingsForListIndex.MapListPathManager.Key.TITLE.key
//            val fileNameOrTitle = lineMap.get(
//                titleKey
//            ) ?: String()
////                titleFileNameAndPathConPair.first
//            val compExtend = CcPathTool.subExtend(fileNameOrTitle)
//            val renamedFileNameOrTitle = UsePath.compExtend(
//                inputEditable.toString().trim(),
//                compExtend
//            )
//            if (
//                fileNameOrTitle == renamedFileNameOrTitle
//            ) return
//            val srcConKey = ListSettingsForListIndex.MapListPathManager.Key.SRC_CON.key
//            val filePathOrCon = lineMap.get(
//                srcConKey
//            ) ?: String()
//            val filePathOrConObj = File(filePathOrCon)
//            val isTitleEqualPathOrCon =
//                fileNameOrTitle == filePathOrConObj.name
//            val isWithFileRename =
//                filePathOrConObj.isFile
//                        && isTitleEqualPathOrCon
//            val renameFilePathOrCon = renameConOrPath(
//                renamedFileNameOrTitle,
//                filePathOrCon,
//                isWithFileRename,
//            )
//            ListIndexDuplicate.isTsvDetect(
//                mapListPath,
//                renamedFileNameOrTitle,
//                renameFilePathOrCon
//            ).let {
//                isDetect ->
//                if(
//                    isDetect
//                ) return
//            }
//            val viewLayoutPathKey = ListSettingsForListIndex.MapListPathManager.Key.VIEW_LAYOUT_PATH.key
//            val renameLineMap = mapOf(
//                titleKey to renamedFileNameOrTitle,
//                srcConKey to renameFilePathOrCon,
//                viewLayoutPathKey to (lineMap.get(viewLayoutPathKey) ?: String())
//            )
//            val srcAndRepLinePairMapList = listOf(
//                lineMap to renameLineMap,
//            )
//            MapListFileTool.updateMapListFileByReplace(
//                mapListPath,
//                srcAndRepLinePairMapList
//            )
//            if (isWithFileRename) {
//                FileSystems.moveFile(
//                    filePathOrCon,
//                    renameFilePathOrCon
//                )
//            }
//            ExecReWriteForListIndexAdapter.replaceListElementForTsv(
//                editFragment,
//                srcAndRepLinePairMapList
//            )
//        }
//
//        private fun renameConOrPath(
//            renamedFileNameOrTitle: String,
//            filePathOrCon: String,
//            isWithFileRename: Boolean,
//        ): String {
//            val filePathOrConObj = File(filePathOrCon)
//            return when (isWithFileRename) {
//                true -> {
//                    val tsvParentDirPath = filePathOrConObj.parent
//                        ?: return String()
//                    File(tsvParentDirPath, renamedFileNameOrTitle).absolutePath
//                }
//                else -> filePathOrCon
//            }
//        }
//    }
//
//    private fun dismissProcess(
//        editFragment: EditFragment
//    ){
//        renamePromptDialog?.dismiss()
//        renamePromptDialog = null
//        CoroutineScope(Dispatchers.IO).launch {
//            withContext(Dispatchers.IO) {
//                delay(200)
//                editFragment.disableKeyboardFragmentChange = false
//            }
//        }
//    }
//}