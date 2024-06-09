
visible=ON,
// disable=ON,
// color=gray,
icon=ok,

click=
    onScriptSave=ON
    |actionImport=
        `${cmdclickConfigSettingEditOkBtnDirPath}
            /relectSetingValAction.js`
    |actionImport=
        `${cmdclickConfigSettingEditOkBtnDirPath}
            /relectFreqSettingVals.js`
    |actionImport=
        `${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
