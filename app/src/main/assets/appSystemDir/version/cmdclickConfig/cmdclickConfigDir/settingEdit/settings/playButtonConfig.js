
visible=ON,
icon=ok,

click=
    onScriptSave=ON
    |actionImport=
        `${cmdclickConfigSettingEditOkBtnDirPath}
            /reflectSetingValAction.js`
    |actionImport=
        `${cmdclickConfigSettingEditOkBtnDirPath}
            /reflectRecentSettingVals.js`
    |actionImport=
        `${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
