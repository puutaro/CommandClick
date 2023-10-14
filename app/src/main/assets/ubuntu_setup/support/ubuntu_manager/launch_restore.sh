#!/bin/bash


readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly KILL_PROC_SHELL_PATH="/support/killProcTree.sh"
readonly NOTIFICATION_CAHNEL_NUM=$(\
	bash "${NOTI_SHELL_DIR_PATH}/echo_channel_num.sh"\
)

bash "${NOTI_EXIT_SHELL_PATH}" \
	"${NOTIFICATION_CAHNEL_NUM}"

bash "${KILL_PROC_SHELL_PATH}" \
	"${NOTI_SHELL_DIR_PATH}"

send-broadcast \
	-a "com.puutaro.commandclick.ubuntu_service.stop"

rm -f /support/* || e=$?
