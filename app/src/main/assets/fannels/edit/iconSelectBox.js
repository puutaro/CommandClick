

let args = jsArgs.get().split("\t");
const iconSelectBoxMapCon = args.at(0);
const selectBoxArgsKeySeparator = "@@@";
const listSeparator = "///";
const VAL_NAME = jsMap.get(
    iconSelectBoxMapCon,
    selectBoxArgsKeySeparator,
    "valName",
);
const LIST_SRC = jsMap.get(
    iconSelectBoxMapCon,
    selectBoxArgsKeySeparator,
    "listSrc",
).replaceAll(listSeparator, "\t");
if(!VAL_NAME) exitZero();
if(!LIST_SRC) exitZero();

jsIconSelectBox.launch(
    VAL_NAME,
    LIST_SRC,
);