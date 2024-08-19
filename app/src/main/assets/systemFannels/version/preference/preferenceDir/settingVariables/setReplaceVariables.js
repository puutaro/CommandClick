
// state
TABLE="table",
SETTING_EDIT=
    "settingEdit",

// setting
coreTitle="preference",
// hideValsLimitNum +  recentLimitNum = all vals num
hideValsLimitNum=20,
recentLimitNum=6,
iconColor="lightGreen",
historyIconColor="blue",
iconBkColor="white",

// dir path
currentAppDirPath=
    "${01}",
preferenceDirPath=
    `${currentAppDirPath}/${001}`,
preferenceSettingsDirPath=
    `${preferenceDirPath}/settings`,
preferenceTempDirPath=
    `${preferenceDirPath}/temp`,
preferenceHideSettingValsDirPath=
    `${preferenceDirPath}/hideSettingVals`,
preferenceSrcDirPath=
    `${preferenceDirPath}/src`,
preferenceShellDirPath=
    `${preferenceDirPath}/shell`,
preferenceLongPressMenuDirPath=
    `${preferenceDirPath}/LongPressMenuDir`,

// file path
FANNEL_NANE=`${02}`,
FANNEL_PATH=`${0}`,
BEFORE_FANNEL_PATH=
    `${preferenceTempDirPath}/${FANNEL_NANE}`,

// setting file path
dynamicHideSettingVariablePath=
    `${preferenceTempDirPath}/dynamicHideSettingVarible.js`,
tableHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/tableHideVals.js`,
webviewSettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/webviewSettingHideVals.js`,
webviewExtraSettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/webviewExtraSettingHideVals.js`,
ubuntuSettingHideSettingVariablePath=
	`${preferenceHideSettingValsDirPath}/ubuntuSettingHideVals.js`,
historySettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/historySettingHideVals.js`,
longPressSettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/longPressSettingHideVals.js`,
startupSettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/startupSettingHideVals.js`,
recentSettingHideSettingVariablePath=
    `${preferenceHideSettingValsDirPath}/recentSettingHideVals.js`,
preferenceEditSettingAllValsPath=
    `${preferenceSrcDirPath}/editSettingVals.js`,
preferenceBeforeEditSettingValsPath=
    `${preferenceTempDirPath}/beforeSettingValsPath.js`,
preferenceEditInfoPath=
    `${preferenceTempDirPath}/editInfo.txt`,
preferenceHomeFannelsPath=
    `${preferenceSettingsDirPath}/homeFannelsFilePaths.txt`,
preferenceHomeScriptUrlsPath=
	`${preferenceSettingsDirPath}/homeScriptUrlsPath.txt`,
srcImageAnchorLongPressMenuPath=
    `${preferenceLongPressMenuDirPath}/srcImageAnchorLongPressMenu.txt`,
imageLongPressMenuPath=
    `${preferenceLongPressMenuDirPath}/imageLongPressMenu.txt`,
srcAnchorLongPressMenuPath=
    `${preferenceLongPressMenuDirPath}/srcAnchorLongPressMenu.txt`,


// table state
preferenceTableDirPath=
    `${preferenceDirPath}/table`,
preferenceTableSettingsDirPath=
    `${preferenceTableDirPath}/settings`,
settingimport=
    `${preferenceTableSettingsDirPath}/tableRepVars.js`,

// settingEdit state
preferenceSettingEditDirPath=
    `${preferenceDirPath}/settingEdit`,
preferenceSettingEditSettingsDirPath=
    `${preferenceSettingEditDirPath}/settings`,
settingimport=
    `${preferenceSettingEditSettingsDirPath}/settingEditSettingRepVars.js`,


// action
preferenceActionDirPath=
    `${preferenceDirPath}/action`,
preferenceActionSettingDirPath=
    `${preferenceActionDirPath}/settings`,
settingimport=
    `${preferenceActionSettingDirPath}/actionRepVals.js`,

// shell
preferenceDiffCurToBeforeFilePath=
    `${preferenceShellDirPath}/diffCurToBeforeFile.sh`,
