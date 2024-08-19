
name=
    |removeExtend=,
list=
    listDir=${CMDCLICK_APP_DIR_ADMIN_DIR_PATH}
    |${LIST_SUFFIX}=.js
    |editByDrag=
        editByDragDisable=ON,
longClick=
    func=MENU
    ?args=
        menuPath=`${CMDCLICK_APP_DIR_ADMIN_LIST_INDEX_MENU_PATH}`,
click=
    jsPath=${CMDCLICK_APP_DIR_ADMIN_CLICK_JS_PATH}
    ?args=
        "ITEM_NAME=${ITEM_NAME}"
    |enableUpdate=ON,