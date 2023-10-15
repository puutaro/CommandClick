#!/bin/bash

readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly NOTI_UPDATE_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_backuping.sh"
readonly NOTI_BACKUP_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup.sh"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly support_dir_path="/support"
readonly KILL_PROC_SHELL_PATH="${support_dir_path}/killProcTree.sh"
readonly UBUNTU_ENV_TSV_PATH="${support_dir_path}/${UBUNTU_ENV_TSV_NAME}"
readonly UBUNTU_ENV_TSV_CON="$(cat "${UBUNTU_ENV_TSV_PATH}")"
readonly UBUNTU_BACKUP_TEMP_ROOTFS_PATH="$(\
	tsvar "${UBUNTU_ENV_TSV_CON}" "UBUNTU_BACKUP_TEMP_ROOTFS_PATH" \
)"
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
	|| rm "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}"
