

const TSV_PATH = `${TSV_PATH}`;
const APP_URL_HISTORY_PATH="${01}/system/url/cmdclickUrlHistory.tsv";
const urlHistoryCon = JsFileSystem.readLocalFile(
	`${APP_URL_HISTORY_PATH}`
);
let urlHistoryConList = uniq(
	urlHistoryCon.split("\n")
).filter(function(line){
	return line.split("\t").length == 2;
}).slice(0, 5);

let displayUrlHistoryTitleListCon = urlHistoryConList.map(
	function(line){
		return line.split("\t").at(0);
	}
).join("\t");

const selectedTitle = jsDialog.listDialog(
	"Select bellow title",
	"",
	displayUrlHistoryTitleListCon,
);
if(!selectedTitle) exitZero();
const addTitleUrlLine = urlHistoryConList.find(
	function(el){
		return el.startsWith(selectedTitle);
	});
if(!selectedLine) exitZero();
const titleUrlList = selectedLine.split("\t");
const title = titleUrlList.at(0);
const url = titleUrlList.at(-1);

const curTsvCon = JsFileSystem.readLocalFile(
	TSV_PATH
);
const registerTsvCon = [
	addTitleUrlLine,
	curTsvCon,
].join("\n");
alert(selectedLine+ "--");
// JsFileSystem.writeLocalFile(
// 	TSV_PATH,
// 	registerTsvCon,
// );
