

// basic
LIST_DIR_PATH=listDir,
LIST_PREFIX=prefix,
LIST_SUFFIX=suffix,


// setting
UPDATE_LIST_INDEX_BROADCAST_ACTION=
	"com.puutaro.commandclick.edit_frag.update_index_list",


// dir path
CMDCLICK_ROOT_DIR_PATH=
	"${00}",
CMDCLICK_CONF_DIR_PATH=
	"${CMDCLICK_ROOT_DIR_PATH}/conf",
CMDCLICK_APP_DIR_ADMIN_DIR_PATH=
	"${CMDCLICK_CONF_DIR_PATH}/AppDirAdmin",
CURRENT_APP_DIR_PATH=
	"${01}",
FANNEL_DIR_PATH=
	`${CURRENT_APP_DIR_PATH}/${001}`,	

// menu path
CMDCLICK_APP_DIR_ADMIN_MENU_PATH=
	`${FANNEL_DIR_PATH}/settings/settingButtonMenu.js`,