
type=
    tsvEdit,

list=
    listDir=`${preferenceTableTsvPath}`
    |sortType=lastUpdate
    |compPath=`${preferenceTableInitTsvPath}`
    |editByDrag=
        editByDragDisable=ON,

click=
    enableUpdate=ON
    |var=runReflectToDynamicHideVals
        ?func=jsFileSystem.copyFile
        ?args=
            srcFilePath="${ITEM_NAME}"
            &desitFilePath=`${dynamicHideSettingVariablePath}`
    |acVar=runToSettingEdit
        ?importPath=`${preferenceChangeStateActionsPath}`
        ?replace=
            STATE=`${SETTING_EDIT}`
            &ON_LIST_DIR_UPDATER=ON
            &ON_PLAY_INFO_SAVE=ON,
