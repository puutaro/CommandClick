

const importedJsFilePath = `${INDEX_LIST_DIR_PATH}/${ITEM_NAME}`;
const importedJsCon = jsFileSystem.readLocalFile(
    importedJsFilePath
);
if(
	!importedJsCon.trim()
) exitZero();
jsDialog.textDialog_S(
    `${ITEM_NAME}`,
    importedJsCon,
    false,
);