

/// LABELING_SECTION_START
// ccImport admin fannel @puutaro
// --
// --
// bellow setting variable main line up
// * terminalFontZoom adjust terminal font size (percentage)
// * terminalFontColor adjust terminal font color
// * terminalColor adjust terminal background color
/// LABELING_SECTION_END


/// SETTING_SECTION_START
editExecute="ALWAYS"
overrideItemClickExec="ON"
terminalFontZoom="0"
terminalColor=""
terminalFontColor=""
execPlayBtnLongPress=""
execEditBtnLongPress=""
setReplaceVariable="LIST_DIR_PATH=listDir"
setReplaceVariable="LIST_PREFIX=prefix"
setReplaceVariable="LIST_SUFFIX=suffix"
setReplaceVariable="CMDCLICK_ROOT_DIR_PATH=${00}"
setReplaceVariable="CMDCLICK_CONF_DIR_PATH=${CMDCLICK_ROOT_DIR_PATH}/conf"
setReplaceVariable="CMDCLICK_CCIMPORT_DIR_PATH=${CMDCLICK_CONF_DIR_PATH}/ccimport"
setVariableType="ccImportList:LI=${LIST_DIR_PATH}=${CMDCLICK_CCIMPORT_DIR_PATH}!${LIST_SUFFIX}=.js|menu=delete!get!sync!copy&copy_file&copy_path!cat"
scriptFileName="ccImportAdmin.js"
/// SETTING_SECTION_END


/// CMD_VARIABLE_SECTION_START
ccImportList=""
/// CMD_VARIABLE_SECTION_END


/// Please write bellow with javascript


let args = jsArgs.get().split("\t");
var FIRST_ARGS = args.at(0);


