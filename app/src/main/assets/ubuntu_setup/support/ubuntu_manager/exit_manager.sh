#!/bin/bash

readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly NOTI_UPDATE_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_backuping.sh"
readonly NOTI_BACKUP_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup.sh"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly KILL_PROC_SHELL_PATH="/support/killProcTree.sh"
readonly NOTIFICATION_CAHNEL_NUM="${1:-}"
readonly REMOVE_ROOTFS="${2:-}"


bash "${KILL_PROC_SHELL_PATH}" \
	"${NOTI_BACKUP_SHELL_PATH}" \
>> "${MONITOR_FILE_PATH}"

bash "${KILL_PROC_SHELL_PATH}" \
	"${NOTI_UPDATE_SHELL_PATH}" \
>> "${MONITOR_FILE_PATH}"

noti \
	-t exit \
	-cn "${NOTIFICATION_CAHNEL_NUM}"


test -z "${REMOVE_ROOTFS}" \
	&& exit 0 \
	|| rm "${UBUNTU_BACKUP_ROOTFS_PATH}"
