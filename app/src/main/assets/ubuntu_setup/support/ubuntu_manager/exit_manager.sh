#!/bin/bash

exec repbash "${0}" \
	-a "${1:-REMOVE_ROOTFS=}" \
	-t "\${UBUNTU_ENV_TSV_PATH}"


exit_background(){
	grepp "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}" \
		| grep "sudo cp" \
		| grep "sudo rm" \
		| test -n "$(cat)" \
		&& return || e=$?
	kill_ptree \
		"${NOTI_BACKUP_CONTROLLER_SHELL_PATH}" \
	>/dev/null

	noti \
		-t exit \
		-cn "${NOTIFICATION_CAHNEL_NUM}"

	test -z "${REMOVE_ROOTFS}" \
	&& return \
	|| rm "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}"
}


exit_background

