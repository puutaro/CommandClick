#!/bin/bash

exec repbash "${0}"

bash "${NOTI_EXIT_SHELL_PATH}" \
    "${NOTIFICATION_CAHNEL_NUM}"


kill_ptree \
	"${NOTI_SHELL_DIR_PATH}"

send-broadcast \
	-a "com.puutaro.commandclick.ubuntu_service.stop"

rm -f ${SUPPORT_DIR_PATH}/* || e=$?
