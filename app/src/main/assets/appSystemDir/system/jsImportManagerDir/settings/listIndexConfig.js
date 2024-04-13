
name=
    |removeExtend=,
list=
    listDir=${CMDCLICK_JS_IMPORT_DIR_PATH}
    |${LIST_SUFFIX}=.js,
longClick=
    jsPath=MENU
    ?args=
        menuPath=`${JS_IMPORT_LONG_PRESS_MENU_PATH}`,
click=
    jsPath=`${JS_IMPORT_SHOW_CON_JS_PATH}`
    ?args=
        "INDEX_LIST_DIR_PATH=${INDEX_LIST_DIR_PATH}|ITEM_NAME=${ITEM_NAME}"
    |enableUpdate=ON,
