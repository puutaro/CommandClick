
type=
    tsvEdit,

list=
    listDir=`${cmdclickConfigTableTsvPath}`
    |sortType=lastUpdate
    |compPath=`${cmdclickConfigTableInitTsvPath}`
    |editByDrag=
        editByDragDisable=ON,

click=
    enableUpdate=ON
    |var=runReflectToDynamicHideVals
        ?func=jsFileSystem.copyFile
        ?args=
            srcFilePath="${ITEM_NAME}"
            &desitFilePath=`${dynamicHideSettingVariblePath}`
    // |var=extraMapCon
    |actionImport=
        `${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${SETTING_EDIT}`
            &ON_LIST_DIR_UPDATER=ON
            &ON_PLAY_INFO_SAVE=ON,
