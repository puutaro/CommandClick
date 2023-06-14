
let args = jsArgs.get().split("\t");
var PARENT_DIR = args.at(0);
var LIST_VIEW_DIR = args.at(1);
var ITEM_NAME = args.at(2);
var MENU_NAME = args.at(3);


const appDirName = jsPath.removeExtend(
	ITEM_NAME,
	".js"
);
const parentDirPath =`${00}/AppDir/${appDirName}`;

jsIntent.launchShortcut(
    parentDirPath,
    ""
);