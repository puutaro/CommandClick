

appDirAdminList:
	LI=
		${LIST_DIR_PATH}=`${CMDCLICK_APP_DIR_ADMIN_DIR_PATH}`
			!${LIST_SUFFIX}=.js
		|menu=delete
			!copy_app_dir
			!rename_app_dir,

