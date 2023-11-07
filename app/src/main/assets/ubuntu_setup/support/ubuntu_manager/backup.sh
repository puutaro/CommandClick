#!/bin/bash

readonly NOTI_SHELL_DIR_PATH=$(dirname "$0")
readonly MONITOR_FILE_PATH="${MONITOR_DIR_PATH}/term_3"
readonly NOTI_MANAGER_SHELL_PATH="${NOTI_SHELL_DIR_PATH}/launch_manager.sh"
readonly KILL_PROC_SHELL_PATH="/support/killProcTree.sh"
readonly support_dir_path="/support"
readonly ubuntu_env_tsv_path="${support_dir_path}/${UBUNTU_ENV_TSV_NAME}"
readonly UBUNTU_ENV_TSV_CON="$(cat "${ubuntu_env_tsv_path}")"
readonly UBUNTU_BACKUP_ROOTFS_PATH="$(\
	tsvar "${UBUNTU_ENV_TSV_CON}" "UBUNTU_BACKUP_ROOTFS_PATH" \
)"
readonly UBUNTU_BACKUP_DIR_PATH="$(dirname "${UBUNTU_BACKUP_ROOTFS_PATH}")"
readonly ROOTFS_TAR_GZ="$(basename "${UBUNTU_BACKUP_ROOTFS_PATH}")"
readonly UBUNTU_BACKUP_TEMP_ROOTFS_PATH="$(\
	tsvar "${UBUNTU_ENV_TSV_CON}" "UBUNTU_BACKUP_TEMP_ROOTFS_PATH" \
)"
readonly UBUNUT_BACKUP_TMP_DIR_PATH="$(dirname "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}")"

echo "extract.." >> "${MONITOR_FILE_PATH}"
mkdir -p "${UBUNUT_BACKUP_TMP_DIR_PATH}"
rm -f "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}"
cd / 
sudo tar \
	-cvpzf "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}" \
	--exclude=/sys \
	--exclude=/dev \
	--exclude=/proc \
	--exclude=/data \
	--exclude=/mnt \
	--exclude=/host-rootfs \
	--exclude=/support  \
	--exclude=/etc/mtab \
	--exclude=/storage \
	--exclude=/etc/profile.d/userland_profile.sh \
	--one-file-system \
	/ \
	>> "${MONITOR_FILE_PATH}" || e=$?

mkdir -p "${UBUNTU_BACKUP_DIR_PATH}"
echo "cp rootfs.." >> "${MONITOR_FILE_PATH}"
sudo cp -vf \
	"${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}" \
	"${UBUNTU_BACKUP_DIR_PATH}/" \
	>> "${MONITOR_FILE_PATH}"
echo "crean up.." >> "${MONITOR_FILE_PATH}"
sudo rm \
	-f "${UBUNTU_BACKUP_TEMP_ROOTFS_PATH}"
echo "Comp & Press CANCEL" >> "${MONITOR_FILE_PATH}"
bash "${NOTI_MANAGER_SHELL_PATH}"
