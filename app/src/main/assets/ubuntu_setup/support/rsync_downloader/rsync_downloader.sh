#!/bin/bash

set -ue

exec \
	repbash \
	"${0}" \
	-a "${1}"


readonly display_src_dir_path=$(basename "${SRC_DIR_PATH}")
readonly RSYNC_NOTI_TITLE_MESSAGE="Scp download.. ${display_src_dir_path}"

exec_rsync_download(){
	local rsync_failure_message=""
	local REMOTE_SERVER_AD="${USER_NAME}@${IP_V4_ADDRESS}"
	sshpass \
	-p "${PASSWORD}" \
	rsync -avzP \
	-e "ssh -p ${PORT} -o 'UserKnownHostsFile=/dev/null' -o 'StrictHostKeyChecking=no'" \
	"${REMOTE_SERVER_AD}:${SRC_DIR_PATH}"/  \
	"${DESTI_DIR_PATH}"/ || rsync_failure_message="$(\
		echo "Scp failure $(echo ${REPBASH_ARGS_CON} | tr ',' '\n')"\
	)"
	case "${rsync_failure_message}" in
		"") return;;
		*);;
	esac
	sleep 1
	kill $(\
		ps aux \
			| grep -v grep \
			| grep "wqnoti" \
			| grep "${RSYNC_NOTI_TITLE_MESSAGE}"\
			| awk '{print $2}' \
	) 
	noti \
		-t exit \
		-cn "${RSYNC_DOWNLOAD_CHANNEL_NUM}"
	sleep 0.5
	wqnoti \
		-p "12324568" \
		-cn "${RSYNC_DOWNLOAD_CHANNEL_NUM}" \
		-i "high" \
		--title "${rsync_failure_message}" \
		--cancel-shell-path "${RSYNC_DOWNLOADER_EXIT_SHELL_PATH}" \
		--comp-message "${rsync_failure_message}"
}
mkdir -p "${SRC_DIR_PATH}"
cd "${SRC_DIR_PATH}"

exec_rsync_download &
readonly rsync_proc_id=$!

wqnoti \
	-p "${rsync_proc_id}" \
	-cn "${RSYNC_DOWNLOAD_CHANNEL_NUM}" \
	-i "high" \
	--title "${RSYNC_NOTI_TITLE_MESSAGE}" \
	--cancel-shell-path "${RSYNC_DOWNLOADER_EXIT_SHELL_PATH}" \
	--comp-message "Comp scp download: ${display_src_dir_path}"
