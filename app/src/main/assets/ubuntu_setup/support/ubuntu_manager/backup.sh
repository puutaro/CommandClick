#!/bin/bash

readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_3"
readonly NOTI_UPDATE_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_backuping.sh"
readonly NOTI_MANAGER_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_manager.sh"
readonly KILL_PROC_SHELL_PATH="/support/killProcTree.sh"

bash "${NOTI_UPDATE_SHELL_PATH}" & 

readonly UBUNTU_BACKUP_DIR_PATH="$(dirname ${UBUNTU_BACKUP_ROOTFS_PATH})"
readonly ROOTFS_TAR_GZ="$(basename "${UBUNTU_BACKUP_ROOTFS_PATH}")"

cd / 
sudo tar -cvpzf ${ROOTFS_TAR_GZ} \
	--exclude=/${ROOTFS_TAR_GZ} \
	--exclude=/storage \
	--exclude=/host-rootfs \
	--one-file-system \
	/ \
	>> "${MONITOR_FILE_PATH}" || e=$?

mkdir -p "${UBUNTU_BACKUP_DIR_PATH}"
echo "cp rootfs.." >> "${MONITOR_FILE_PATH}"
sudo mv -vf ${ROOTFS_TAR_GZ} \
	"${UBUNTU_BACKUP_DIR_PATH}/" \
	>> "${MONITOR_FILE_PATH}"
echo "crean up.." >> "${MONITOR_FILE_PATH}"
bash "${KILL_PROC_SHELL_PATH}" \
	"${NOTI_UPDATE_SHELL_PATH}"
sleep 2
bash "${NOTI_MANAGER_SHELL_PATH}"
