#!/bin/bash


readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_2"
readonly NOTI_EXIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/exit_manager.sh"
readonly NOTI_BACKUP_CONTROLLER_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup_controller.sh"
readonly EXEC_INIT_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_init.sh"
readonly NOTIFICATION_CAHNEL_NUM=$(\
	bash "${NOTI_SHELL_DIR_PATH}/echo_channel_num.sh"\
)
readonly support_dir_path="/support"
readonly ubuntu_env_tsv_path="${support_dir_path}/${UBUNTU_ENV_TSV_NAME}"
readonly UBUNTU_ENV_TSV_CON="$(cat "${ubuntu_env_tsv_path}")"

readonly title="Ubuntu rootfs backup manager"
readonly message="Press bellow button"

noti \
	-t launch \
	-cn ${NOTIFICATION_CAHNEL_NUM} \
	--icon-name copy \
	--importance high \
	--title "${title}" \
	--message "${message}" \
	--alert-once \
	--delete "shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}" \
	--button "label=CLOSE,shellPath=${NOTI_EXIT_SHELL_PATH},args=${NOTIFICATION_CAHNEL_NUM}" \
	--button "label=BACKUP,shellPath=${NOTI_BACKUP_CONTROLLER_SHELL_PATH},execType=back" \
	--button "label=INIT,shellPath=${EXEC_INIT_SHELL_PATH}" \
>/dev/null 2>&1
