
visible=ON,
// disable=ON,
// color=gray,
icon=list,

click=
    actionImport=`${cmdclickConfigChangeStateActionsPath}`
        ?replace=
            STATE=`${TABLE}`,
