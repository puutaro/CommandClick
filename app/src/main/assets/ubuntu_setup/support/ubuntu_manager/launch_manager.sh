#!/bin/bash


readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly NOTI_BACKUP_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup.sh"
readonly EXEC_RESTORE_OK_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_restore.sh"
readonly NOTIFICATION_CAHNEL_NUM=$(\
	bash "${NOTI_SHELL_DIR_PATH}/echo_channel_num.sh"\
)

readonly title="Ubuntu backup manager"
readonly message="Press bellow button"
RESTORE_BUTTON_OPTION=""
if [ -f "${UBUNTU_BACKUP_ROOTFS_PATH}" ];then
	RESTORE_BUTTON_OPTION="--button \"label=RESTORE,shellPath=${EXEC_RESTORE_OK_SHELL_PATH}\""
fi

noti \
	-t launch \
	-cn ${NOTIFICATION_CAHNEL_NUM} \
	--icon-name copy \
	--importance high \
	--title "${title}" \
	--message "${message}" \
	--alert-once \
	--delete "shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}" \
	--button "label=CANCEL,shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}" \
	--button "label=BACKUP,shellPath=${NOTI_BACKUP_SHELL_PATH},execType=back" \
	${RESTORE_BUTTON_OPTION} \
>/dev/null 2>&1

