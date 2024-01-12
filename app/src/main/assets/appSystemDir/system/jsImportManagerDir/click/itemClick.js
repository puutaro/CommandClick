
let args = jsArgs.get().split("\t");
var PARENT_DIR = args.at(0);
var LIST_VIEW_DIR = args.at(1);
var ITEM_NAME = args.at(2);
var MENU_NAME = args.at(3);


const importedJsFilePath = `${LIST_VIEW_DIR}/${ITEM_NAME}`;
const importedJsCon = jsFileSystem.readLocalFile(
    importedJsFilePath
);
if(
	!importedJsCon.trim()
) exitZero();
jsDialog.textDialog(
    ITEM_NAME,
    importedJsCon,
    false,
);