#!/bin/bash

set -ue 

readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly NOTI_BACKUP_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/backup.sh"
readonly NOTI_UPDATE_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_backuping.sh"

readonly is_summarize_process=$(\
	ps aux \
	| grep "${NOTI_BACKUP_SHELL_PATH}" \
	| grep -v grep \
)

case "${is_summarize_process}" in
	"") ;;
	*) exit 0 ;;
esac

bash \
	"${NOTI_BACKUP_SHELL_PATH}"\
	"$@" \
	&
readonly backup_pid=$!
bash "${NOTI_UPDATE_SHELL_PATH}" \
	"${backup_pid}"
