package com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib

import android.app.Dialog
//import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer.getFcbMap
//import com.puutaro.commandclick.proccess.edit.edit_text_support_view.lib.FileSelectSpinnerViewProducer.getFilterType

object FileSelectGridViewProducer {
//    private const val noExtend = "NoExtend"
    private const val gridButtonLabel = "GSL"

    private var gridDialogObj: Dialog? = null

//    fun make (
//        editFragment: EditFragment,
//        insertEditText: EditText,
//        editParameters: EditParameters,
//        currentComponentIndex: Int,
//        weight: Float,
//    ): Button {
//        val context = editFragment.context
//        val currentId = editParameters.currentId
//        val linearParamsForGrid = LinearLayoutCompat.LayoutParams(
//            0,
//            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
//        )
//        linearParamsForGrid.weight = weight
//        val fcbMap = getFcbMap(
//            editParameters,
//            currentComponentIndex
//        )
//        val gridButtonView = Button(context)
//        gridButtonView.id = currentId + EditTextSupportViewId.EDITABLE_GRID.id
//        gridButtonView.tag = "gridEdit${currentId + EditTextSupportViewId.EDITABLE_GRID.id}"
//        gridButtonView.text = gridButtonLabel
//        ButtonSetter.set(
//            context,
//            gridButtonView,
//            mapOf()
//        )
//
//        gridButtonView.setOnClickListener {
//                buttonView ->
//            val buttonContext = buttonView.context
//            gridDialogObj = Dialog(
//                buttonContext
//            )
//            gridDialogObj?.setContentView(
//                com.puutaro.commandclick.R.layout.grid_dialog_layout
//            )
//            setGridListView(
//                editFragment,
////                editParameters,
//                insertEditText,
//                fcbMap
//            )
//            gridDialogObj?.setOnCancelListener {
//                gridDialogObj?.dismiss()
//                gridDialogObj = null
//            }
//            gridDialogObj?.window?.setLayout(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            )
//            gridDialogObj?.window?.setGravity(Gravity.BOTTOM)
//            gridDialogObj?.show()
//        }
//        gridButtonView.layoutParams = linearParamsForGrid
//        return gridButtonView
//    }
//
//    private fun setGridListView(
//        currentFragment: EditFragment,
////        editParameters: EditParameters,
//        insertEditText: EditText,
//        fcbMap: Map<String, String>?
//    ) {
//        val context = currentFragment.context
//            ?: return
//        val filterDir = getSelectDirPath(
//            fcbMap,
////            editParameters,
//        )
//        val filterPrefix = getFilterPrefix(
//            fcbMap,
//        )
//        val filterSuffix = getFilterSuffix(
//            fcbMap,
//        )
//        val filterType = getFilterType(
//            fcbMap,
//        )
//        val listDialogSearchEditText = gridDialogObj?.findViewById<AppCompatEditText>(
//            com.puutaro.commandclick.R.id.grid_dialog_search_edit_text
//        ) ?: return
//        listDialogSearchEditText.hint = "search selectable list"
//        val editableSpinnerList =
//            makeGridList(
//                filterDir,
//                filterPrefix,
//                filterSuffix,
//                filterType
//            )
//        val gridView =
//            gridDialogObj?.findViewById<GridView>(
//                com.puutaro.commandclick.R.id.grid_dialog_grid_view
//            ) ?: return
//        val imageAdapter = ImageAdapter(
//            context,
//        )
//        imageAdapter.clear()
//        imageAdapter.addAll(
//            editableSpinnerList.toMutableList()
//        )
//        gridView.adapter = imageAdapter
//        makeSearchEditText(
//            gridView,
//            listDialogSearchEditText,
//            editableSpinnerList.joinToString("\n"),
//        )
//        setGridViewItemClickListener(
//            currentFragment,
//            insertEditText,
//            listDialogSearchEditText,
//            gridView,
////            editParameters,
//            fcbMap,
//        )
//    }
//
//    private fun setGridViewItemClickListener(
//        editFragment: Fragment,
//        insertEditText: EditText,
//        searchText: EditText,
//        gridView: GridView,
////        editParameters: EditParameters,
//        fcbMap: Map<String, String>?
//    ){
//        Keyboard.hiddenKeyboardForFragment(
//            editFragment
//        )
////        val currentAppDirPath = FannelInfoTool.getCurrentAppDirPath(
////            editParameters.fannelInfoMap
////        )
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
//        gridView.setOnItemClickListener {
//                parent, View, pos, id
//            ->
//            gridDialogObj?.dismiss()
//            gridDialogObj = null
//            val currentGridList = makeGridList(
//                filterDir,
//                filterPrefixListCon,
//                filterSuffixListCon,
//                filterType
//            )
//            val selectedItem = currentGridList.filter {
//                Regex(
//                    searchText.text.toString()
//                        .lowercase()
//                        .replace("\n", "")
//                ).containsMatchIn(
//                    it.lowercase()
//                )
//            }.get(pos)
//            val selectedFileName = File(selectedItem).name
////            if(
////                currentAppDirPath != UsePath.cmdclickAppHistoryDirAdminPath
////            ) {
//            FileSystems.updateLastModified(
//                File(
//                    filterDir,
//                    selectedFileName
//                ).absolutePath
//            )
////            }
//            val selectUpdatedGridList = listOf(
//                selectedItem,
//            ) + currentGridList.filter {
//                it != selectedItem
//            }
//            val imageAdapter =
//                gridView.adapter as ImageAdapter
//            imageAdapter.clear()
//            imageAdapter.addAll(selectUpdatedGridList.toMutableList())
//            imageAdapter.notifyDataSetChanged()
//            gridView.setSelection(0)
//            insertEditText.setText(selectedFileName)
//            SelectJsExecutor.exec(
//                editFragment,
//                selectJsPath,
//                selectedItem,
//            )
//        }
//    }
//
////    private fun judgeBySuffix(
////        targetStr: String,
////        filterSuffix: String,
////    ): Boolean{
////        if(filterSuffix != noExtend) {
////            return QuoteTool.splitBySurroundedIgnore(
////                filterSuffix,
////                '&'
////            )
//////            filterSuffix.split("&")
////                .any {
////                targetStr.endsWith(it)
////            }
////        }
////        return !Regex("\\..*$").containsMatchIn(targetStr)
////    }
//
//    private fun makeGridList(
//        filterDir: String,
//        filterPrefixListCon: String,
//        filterSuffixListCon: String,
//        filterType: String,
//    ): List<String> {
//        val sortedList = FileSystems.sortedFiles(
//            filterDir,
//            "on"
//        )
//        val isFile = filterType != FilterFileType.dir.name
//        return when(isFile){
//            true -> {
//                sortedList.filter {
//                    FilterPathTool.isFilterByFile(
//                        it,
//                        filterDir,
//                        filterPrefixListCon,
//                        filterSuffixListCon,
//                        true,
//                        "&"
//                    )
////                    val okSuffix = FilterPathTool.judgeBySuffix(it, filterSuffixListCon, "&")
////                    val isFile = File("$filterDir/$it").isFile
////                    it.startsWith(filterPrefixListCon)
////                            && okSuffix
////                            && isFile
//                }.map {
//                    "$filterDir/$it"
//                }
//            }
//            false -> sortedList.filter {
//                FilterPathTool.isFilterByDir(
//                    it,
//                    filterDir,
//                    filterPrefixListCon,
//                    filterSuffixListCon,
//                    true,
//                    "&"
//                )
////                it.startsWith(filterPrefixListCon)
////                        && FilterPathTool.judgeBySuffix(it, filterSuffixListCon, "&")
////                        && File("$filterDir/$it").isDirectory
//            }.map {
//                "$filterDir/$it"
//            }
//        }
//    }
//
//    private fun makeSearchEditText(
//        gridView:  GridView,
//        searchText: EditText,
//        listCon: String,
//    ) {
//        searchText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                if (!searchText.hasFocus()) return
//                val filteredList = listCon.split("\n").filter {
//                    Regex(
//                        searchText.text.toString()
//                            .lowercase()
//                            .replace("\n", "")
//                    ).containsMatchIn(
//                        it.lowercase()
//                    )
//                }
//                val imageAdapter = gridView.adapter as ImageAdapter
//                imageAdapter.clear()
//                imageAdapter.addAll(filteredList.toMutableList())
//                imageAdapter.notifyDataSetChanged()
//            }
//        })
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
//            if (
//                it.isEmpty()
//            ) return@let cmdclickDefaultAppDirPath
//            it
//        } ?: cmdclickDefaultAppDirPath
//    }
//
//    private fun getFilterPrefix(
//        fcbMap: Map<String, String>?,
//    ): String {
//        return fcbMap?.get(FileSelectEditKey.prefix.name)?.let {
//            QuoteTool.trimBothEdgeQuote(it)
//        } ?: String()
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
//
//    private enum class FileSelectEditKey {
//        dirPath,
//        prefix,
//        suffix,
//        selectJs
//    }
//
//    private enum class FilterFileType {
//        file,
//        dir,
//    }
}