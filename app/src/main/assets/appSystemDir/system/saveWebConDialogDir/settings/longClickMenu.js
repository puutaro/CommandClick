

name=Delete
|icon=delete
|jsPath=DELETE,
// |extra=
//     parentDirPath=`${CMDCLICK_JS_IMPORT_DIR_PATH}`,

name=Write
|icon=edit
|jsPath=WRITE,
// |extra=
//     parentDirPath=`${CMDCLICK_JS_IMPORT_DIR_PATH}`,

name=Copy
|icon=copy,

name=Copy file
|parentName=Copy
|icon=folda
|jsPath=COPY_FILE,
|extra=
    // parentDirPath=`${CMDCLICK_JS_IMPORT_DIR_PATH}`
    broadcastAction=`${UPDATE_LIST_INDEX_BROADCAST_ACTION}`,

name=Copy path
|parentName=Copy
|icon=copy
|jsPath=COPY_PATH,
