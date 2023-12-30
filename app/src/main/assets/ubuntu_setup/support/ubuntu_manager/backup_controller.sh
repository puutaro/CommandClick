#!/bin/bash

set -ue 

exec repbash "${0}"

bash "${NOTI_BACKUP_SHELL_PATH}" &
readonly backup_pid=$!
bash "${NOTI_UPDATE_SHELL_PATH}" \
	"BACKUP_PID=${backup_pid}"
