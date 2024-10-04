package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib


//object FileSelectSpinnerViewProducer {
//
//    val noExtend = "NoExtend"
//    private val throughMark = "-"
//
//    fun make (
//        fragment: Fragment,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Spinner {
//        val context = fragment.context
//        val currentId = editParameters.currentId
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            editParameters.fannelInfoMap
////        )
//
//        val linearParamsForSpinner = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForSpinner.weight = weight
//        val adapter = ArrayAdapter<String>(
//            context as Context,
//            R.layout.sppinner_layout,
//        )
//        val fcbMap = getFcbMap(
//            editParameters,
//            currentComponentIndex
//        )
//        val filterDir = getSelectDirPath(
//            fcbMap,
////            editParameters,
//        )
//        val filterPrefixListCon = getFilterPrefix(
//            fcbMap,
//        )
//        val filterSuffixListCon = getFilterSuffix(
//            fcbMap,
//        )
//        val filterType = getFilterType(
//            fcbMap,
//        )
//        val selectJsPath = getSelectJsPath(
//            fcbMap
//        )
//        val editableSpinnerList = makeSpinnerList(
//            filterDir,
//            filterPrefixListCon,
//            filterSuffixListCon,
//            filterType
//        )
//        val updatedEditableSpinnerList = listOf(throughMark) + editableSpinnerList
//        adapter.addAll(updatedEditableSpinnerList)
//
//        val insertSpinner = SpinnerInstance.make(
//            context,
//            updatedEditableSpinnerList,
//            editParameters.onFixNormalSpinner
//        )
//        insertSpinner.id = currentId + EditTextSupportViewId.EDITABLE_SPINNER.id
//        insertSpinner.tag = "spinnerEdit${currentId + EditTextSupportViewId.EDITABLE_SPINNER.id}"
//        insertSpinner.adapter = adapter
//        insertSpinner.setOnTouchListener(View.OnTouchListener {
//                v, event ->
//            val currentSpinnerList = makeSpinnerList(
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                filterType
//            )
//            val selectUpdatedSpinnerList =
//                listOf(throughMark) + currentSpinnerList
//            adapter.clear()
//            adapter.addAll(selectUpdatedSpinnerList)
//            adapter.notifyDataSetChanged()
//            insertSpinner.setSelection(0)
//            v.performClick()
//            false
//        })
//        insertSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
//                val selectedItem = adapter.getItem(pos)
//                    ?: return
//                if(
//                    selectedItem != throughMark
////                    && currentAppDirPath != UsePath.cmdclickAppHistoryDirAdminPath
//                    && File(selectedItem).isFile
//                ) {
//                    FileSystems.updateLastModified(
//                        File(
//                            filterDir,
//                            selectedItem
//                        ).absolutePath
//                    )
//                }
//                val currentSpinnerList = makeSpinnerList(
//                    filterDir,
//                    filterPrefixListCon,
//                    filterSuffixListCon,
//                    filterType
//                )
//                val selectUpdatedSpinnerList = if(
//                    selectedItem == throughMark
//                ){
//                    listOf(throughMark) + currentSpinnerList
//                } else listOf(
//                        throughMark,
//                        selectedItem,
//                    ) + currentSpinnerList.filter {
//                        it != selectedItem
//                    }
//                adapter.clear()
//                adapter.addAll(selectUpdatedSpinnerList)
//                adapter.notifyDataSetChanged()
//                insertSpinner.setSelection(0)
//                if(
//                    selectedItem == throughMark
//                ) return
//                insertEditText.setText(selectedItem)
//                SelectJsExecutor.exec(
//                    fragment,
//                    selectJsPath,
//                    selectedItem,
//                )
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {}
//        }
//        insertSpinner.layoutParams = linearParamsForSpinner
//        return insertSpinner
//    }
//
//    private fun judgeBySuffix(
//        targetStr: String,
//        filterSuffix: String,
//    ): Boolean{
//        if(filterSuffix != noExtend) {
//            return QuoteTool.splitBySurroundedIgnore(
//                filterSuffix,
//                '&'
//            )
////            filterSuffix.split("&")
//                .any {
//                targetStr.endsWith(it)
//            }
//        }
//        return !Regex("\\..*$").containsMatchIn(targetStr)
//    }
//
//    private fun makeSpinnerList(
//        filterDir: String,
//        filterPrefixListCon: String,
//        filterSuffixListCon: String,
//        filterType: String,
//    ): List<String> {
//        val sortedList = FileSystems.sortedFiles(
//            filterDir,
//            "on"
//        )
//        if (
//            filterType != FilterFileType.dir.name
//        ) return sortedList.filter {
//            FilterPathTool.isFilterByFile(
//                it,
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                true,
//                "&"
//            )
////            it.startsWith(filterPrefixListCon)
////                    && judgeBySuffix(it, filterSuffixListCon)
////                    && File("$filterDir/$it").isFile
//        }
//        return sortedList.filter {
//            FilterPathTool.isFilterByDir(
//                it,
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                true,
//                "&"
//            )
////            it.startsWith(filterPrefixListCon)
////                    && judgeBySuffix(it, filterSuffixListCon)
////                    && File("$filterDir/$it").isDirectory
//        }
//    }
//
//    private fun getSelectDirPath(
//        fcbMap: Map<String, String>?,
////        editParameters: EditParameters,
//    ): String {
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            editParameters.fannelInfoMap
////        )
//        val cmdclickDefaultAppDirPath = UsePath.cmdclickDefaultAppDirPath
//        return fcbMap?.get(
//            FileSelectEditKey.dirPath.name
//        )?.let {
//                if (
//                    it.isEmpty()
//                ) return@let cmdclickDefaultAppDirPath
//                it
//            } ?: cmdclickDefaultAppDirPath
//    }
//
//    private fun getFilterPrefix(
//        fcbMap: Map<String, String>?,
//    ): String {
//        return fcbMap?.get(FileSelectEditKey.prefix.name)?.let {
//                QuoteTool.trimBothEdgeQuote(it)
//            } ?: String()
//    }
//
//    private fun getFilterSuffix(
//        fcbMap: Map<String, String>?,
//    ): String {
//        return fcbMap?.get(FileSelectEditKey.suffix.name)?.let {
//            QuoteTool.trimBothEdgeQuote(it)
//        } ?: String()
//    }
//
//    fun getFilterType(
//        fcbMap: Map<String, String>?,
//    ): String {
//        return fcbMap?.get(FileSelectEditKey.type.name)?.let {
//            val trimType = QuoteTool.trimBothEdgeQuote(it)
//            if(
//                trimType.isEmpty()
//            ) return@let FilterFileType.file.name
//            trimType
//        } ?: FilterFileType.file.name
//    }
//
//    private fun getSelectJsPath(
//        fcbMap: Map<String, String>?,
//    ): String {
//        return fcbMap?.get(FileSelectEditKey.selectJs.name)?.let {
//            val trimType = QuoteTool.trimBothEdgeQuote(it)
//            if(
//                trimType.isEmpty()
//            ) return@let FilterFileType.file.name
//            trimType
//        } ?: FilterFileType.file.name
//    }


//    fun getFcbMap(
//        editParameters: EditParameters,
//        currentComponentIndex: Int
//    ): Map<String, String>? {
//        val currentSetVariableMap = editParameters.setVariableMap
//        val fannelInfoMap = editParameters.fannelInfoMap
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            fannelInfoMap
////        )
//        val currentScriptName = FannelInfoTool.getCurrentFannelName(
//            fannelInfoMap
//        )
//        return currentSetVariableMap?.get(
//            SetVariableTypeColumn.VARIABLE_TYPE_VALUE.name
//        )?.split('|')
//            ?.getOrNull(currentComponentIndex)
//            ?.let {
//                ScriptPreWordReplacer.replace(
//                    it,
////                    currentAppDirPath,
//                    currentScriptName
//                )
//            }?.let{
//                CmdClickMap.createMap(
//                    it,
//                    '?'
//                )
//            }?.toMap()
//    }

//    private enum class FileSelectEditKey {
//        dirPath,
//        prefix,
//        suffix,
//        type,
//        selectJs
//    }
//
//    private enum class FilterFileType {
//        file,
//        dir,
//    }
//}
