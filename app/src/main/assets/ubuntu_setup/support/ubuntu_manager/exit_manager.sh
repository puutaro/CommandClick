#!/bin/bash

readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly NOTI_UPDATE_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_backuping.sh"
readonly NOTI_BACKUP_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup.sh"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly support_dir_path="/support"
readonly UBUNTU_ENV_TSV_PATH="${support_dir_path}/${UBUNTU_ENV_TSV_NAME}"
readonly UBUNTU_ENV_TSV_CON="$(cat "${UBUNTU_ENV_TSV_PATH}")"
readonly UBUNTU_BACKUP_TEMP_ROOTFS_PATH="$(\
	tsvar "${UBUNTU_ENV_TSV_CON}" "UBUNTU_BACKUP_TEMP_ROOTFS_PATH" \
)"
readonly NOTIFICATION_CAHNEL_NUM="${1:-}"
readonly REMOVE_ROOTFS="${2:-}"


exit_background(){
	local ps_aux_cmd="ps aux"
	${ps_aux_cmd} \
	| awk \
	-v UBUNTU_BACKUP_TEMP_ROOTFS_PATH="${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}" \
	-v ps_aux_cmd="${ps_aux_cmd}" \
	'{
		is_kill_gard_cmd=""
		if(\
			$0 ~ "cp"\
			|| $0 ~ "rm"\
		) is_kill_gard_cmd = "true"
		if(\
			is_kill_gard_cmd\
			&& $0 ~ UBUNTU_BACKUP_TEMP_ROOTFS_PATH \
			&& $0 !~ "awk " \
			&& $0 !~ ps_aux_cmd \
		) print $0
	}' | test -n "$(cat)" \
	&& return || e=$?
	kill_ptree \
		"${NOTI_BACKUP_SHELL_PATH}" \
	>> "${MONITOR_FILE_PATH}"

	kill_ptree \
		"${NOTI_UPDATE_SHELL_PATH}" \
	>> "${MONITOR_FILE_PATH}"

	noti \
		-t exit \
		-cn "${NOTIFICATION_CAHNEL_NUM}"

	test -z "${REMOVE_ROOTFS}" \
	&& return \
	|| rm "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}"
}


exit_background

