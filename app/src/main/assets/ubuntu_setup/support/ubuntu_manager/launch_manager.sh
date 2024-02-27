#!/bin/bash

exec repbash "${0}"

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
	--delete "shellPath=${NOTI_EXIT_SHELL_PATH}" \
	--button "label=CLOSE,shellPath=${NOTI_EXIT_SHELL_PATH}" \
	--button "label=BACKUP,shellPath=${NOTI_BACKUP_CONTROLLER_SHELL_PATH},execType=back" \
	--button "label=INIT,shellPath=${EXEC_INIT_SHELL_PATH}" \
>/dev/null 2>&1
