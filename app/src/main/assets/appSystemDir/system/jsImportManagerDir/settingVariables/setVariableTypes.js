

jsImportList:
	LI=
		${LIST_DIR_PATH}=${CMDCLICK_JS_IMPORT_DIR_PATH}
			!${LIST_SUFFIX}=.js
		|menu=delete
			!write
			!copy
				&copy_file
				&copy_path,
