#!/bin/bash


readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_3"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly NOTIFICATION_CAHNEL_NUM=$(\
	bash "${NOTI_SHELL_DIR_PATH}/echo_channel_num.sh"\
)

readonly title="Ubuntu backuping.."


launch_backuping(){
	local message="$(tail -1 "${MONITOR_FILE_PATH}")"
	noti \
		-t launch \
		-cn ${NOTIFICATION_CAHNEL_NUM} \
		--icon-name copy \
		--importance high \
		--title "${title}" \
		--message "${message}" \
		--alert-once \
		--delete "shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}&remove" \
		--button "label=CANCEL,shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}&remove" \
	>/dev/null 2>&1
}

while :
do
	launch_backuping
	sleep 1.5
done