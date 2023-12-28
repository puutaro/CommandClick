#!/bin/bash


exec repbash "${0}"

noti \
	-t exit \
	-cn "${RSYNC_DOWNLOAD_CHANNEL_NUM}"

kill_ptree \
	"${RSYNC_DOWNLOADER_LOADER_SHELL_PATH}"
