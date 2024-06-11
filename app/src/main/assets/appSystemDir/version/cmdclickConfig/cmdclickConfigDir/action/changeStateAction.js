

var=onListDirUpdater
    ?value=`"{{ ON_LIST_DIR_UPDATER:OFF }}"`
|var=updateConfigInfoTsvPath
    ?value=
    ?if=`onListDirUpdater == "ON"`
    ?value=`${ITEM_TITLE}`
|var=extraMapCon
    ?value=`
        onListDirUpdater=${onListDirUpdater}
        |listDirTsvPath=${cmdclickConfigSettingEditListIndexTsvPath}
        |listDirValue=${updateConfigInfoTsvPath}
        |onInfoSave={{ ON_PLAY_INFO_SAVE:OFF }}
        |saveInfoPath=${cmdclickConfigEditInfoPath}
        |extraSaveInfo=
        |enableAddToBackStack={{ ENABLE_ADD_TO_BACKSTACK:OFF }}
        `
|var=runChangeState
    ?func=jsStateChange.change_S
    ?args=
        stateName="{{ STATE }}"
        &extraMapCon=`${extraMapCon}`
    ,
