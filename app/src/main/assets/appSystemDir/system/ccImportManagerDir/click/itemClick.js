
let args = jsArgs.get().split("\t");
var PARENT_DIR = args.at(0);
var LIST_VIEW_DIR = args.at(1);
var ITEM_NAME = args.at(2);
var MENU_NAME = args.at(3);


jsFileSystem.jsEcho(
	"NORMAL",
	`item click \n${PARENT_DIR}\n${LIST_VIEW_DIR}\n${ITEM_NAME}\n${MENU_NAME}\n`
);
