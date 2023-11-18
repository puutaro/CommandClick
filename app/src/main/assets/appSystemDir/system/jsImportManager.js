

/// LABELING_SECTION_START
// jsImport admin fannel @puutaro
// --
// --
// bellow setting variable main line up
// * terminalFontZoom adjust terminal font size (percentage)
// * terminalFontColor adjust terminal font color
// * terminalColor adjust terminal background color
/// LABELING_SECTION_END


/// SETTING_SECTION_START
terminalDo="OFF"
editExecute="ALWAYS"
onAdBlock="OFF"
overrideItemClickExec="ON"
disableSettingButton="ON"
disableEditButton="ON"
disablePlayButton="ON"
terminalFontZoom="0"
terminalColor=""
terminalFontColor=""
execPlayBtnLongPress=""
execEditBtnLongPress=""
setReplaceVariables="LIST_DIR_PATH=listDir"
setReplaceVariables="LIST_PREFIX=prefix"
setReplaceVariables="LIST_SUFFIX=suffix"
setReplaceVariables="CMDCLICK_ROOT_DIR_PATH=${00}"
setReplaceVariables="CMDCLICK_CONF_DIR_PATH=${CMDCLICK_ROOT_DIR_PATH}/conf"
setReplaceVariables="CMDCLICK_JS_IMPORT_DIR_PATH=${CMDCLICK_CONF_DIR_PATH}/jsimport"
setVariableTypes="jsImportList:LI=${LIST_DIR_PATH}=${CMDCLICK_JS_IMPORT_DIR_PATH}!${LIST_SUFFIX}=.js|menu=delete!get!sync!util&add&write&cat!copy&copy_file&copy_path"
scriptFileName="jsImportManager.js"
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
jsImportList=""
/// CMD_VARIABLE_SECTION_END


/// Please write bellow with javascript


let args = jsArgs.get().split("\t");
var FIRST_ARGS = args.at(0);


