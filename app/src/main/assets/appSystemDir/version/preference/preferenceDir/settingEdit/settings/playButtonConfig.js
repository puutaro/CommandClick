
visible=ON,
icon=ok,

click=
    onScriptSave=ON
    |actionImport=
        `${preferenceSettingEditOkBtnDirPath}
            /reflectSetingValAction.js`
    |actionImport=
        `${preferenceSettingEditOkBtnDirPath}
            /reflectRecentSettingVals.js`
    |actionImport=
        `${preferenceChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
