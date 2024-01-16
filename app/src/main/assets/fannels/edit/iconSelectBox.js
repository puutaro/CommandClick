

let args = jsArgs.get().split("\t");
const iconSelectBoxMapCon = args.at(0);
const selectBoxArgsKeySeparator = "@@@";
const VAL_NAME = jsMap.get(
    iconSelectBoxMapCon,
    selectBoxArgsKeySeparator,
    "valName",
);
const LIST_PATH = jsMap.get(
    iconSelectBoxMapCon,
    selectBoxArgsKeySeparator,
    "listPath",
);
if(!VAL_NAME) exitZero();
if(!LIST_PATH) exitZero();

jsIconSelectBox.launch(
    VAL_NAME,
    LIST_PATH,
);