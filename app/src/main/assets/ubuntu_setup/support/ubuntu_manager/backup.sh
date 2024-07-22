#!/bin/bash

exec repbash "${0}" \
	-t "\${UBUNTU_ENV_TSV_PATH}"

echo "#" >> "${MONITOR_FILE_PATH}"
echo "[1/4] ready.." >> "${MONITOR_FILE_PATH}"
sudo apt-get clean
sudo apt-get autoremove
sudo pip cache purge
sudo pip3 cache purge
dpkg -l 'linux-*' | sed '/^ii/!d;/'"$(uname -r | sed "s/\(.*\)-\([^0-9]\+\)/\1/")"'/d;s/^[^ ]* [^ ]* \([^ ]*\).*/\1/;/[0-9]/!d' | xargs sudo apt-get -y purge
echo "[2/4] extract.." >> "${MONITOR_FILE_PATH}"
mkdir -p "${UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH}"
cd / 
readonly ORDINALY_EXCLUDE_CON="$(\
	echo "/sys
		/dev
		/proc
		/data
		/mnt
		/host-rootfs
		/support
		/etc/mtab
		/storage
		/etc/profile.d/userland_profile.sh  
	"\
	|awk '{ 
 		gsub(/^[ \t]+/, "", $0)
 		gsub(/[ \t]+$/, "", $0)
 		if(!$0) next
 		printf "--exclude=%s ", $0
 	}'\
)"
readonly EXTRA_EXCLUDE_CON=$(\
	cat "${EXTRA_EXCLUDE_PATH}"\
	|awk '{ 
		gsub(/^[ \t]+/, "", $0)
		gsub(/[ \t]+$/, "", $0)
		if($0 ~ /^#/) next
		if(!$0) next
		print $0
	}'\
	| awk '{
		printf "test -d \x22%s\x22 && echo \x22%s\x22\n", $0, $0
	}' | bash\
) 
awk \
	-v EXTRA_EXCLUDE_CON="${EXTRA_EXCLUDE_CON}" \
	-v ORDINALY_EXCLUDE_CON="${ORDINALY_EXCLUDE_CON}" \
	-v UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH="${UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH}" \
	-v ROOTFS_TAR_NAME="${ROOTFS_TAR_NAME}" \
	-v concurrency=3 \
	'BEGIN{
		len_extra_exclude_list = split(EXTRA_EXCLUDE_CON, extra_exclude_list, "\n")
		cd_cmd="cd /"
		for(k=1; k <= len_extra_exclude_list; k++){
			printf "echo \x22(%d/%d)\x22\n", k, len_extra_exclude_list
			exclude_ops = ""
	 		for(i=1; i <= len_extra_exclude_list; i++){
	 			if(k == i) {
	 				continue
	 			}
	 			exclude_ops = sprintf( "%s --exclude=%s ", exclude_ops, extra_exclude_list[i])
	 		}
	 		dir_suffix = gensub("/", "___", "g", extra_exclude_list[k])
	 		make_dir_path = sprintf("%s/%s", UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH, dir_suffix)
	 		gsub(/[/]+/, "/", make_dir_path)
	 		mkdir_cmd = sprintf("mkdir -p \x22%s\x22", make_dir_path)
	 		rootfs_path = sprintf("%s/%s", make_dir_path, ROOTFS_TAR_NAME)
	 		gsub(/[/]+/, "/", rootfs_path)
	 		tar_cmd_body = sprintf("sudo tar -cpPf \x22%s\x22", rootfs_path)
	 		total_exclude_con = sprintf("%s %s", ORDINALY_EXCLUDE_CON, exclude_ops)
	 		tar_ops = "--one-file-system "
	 		tar_cmd = sprintf("%s %s %s /", \
	 			tar_cmd_body, \
	 			total_exclude_con, \
	 			tar_ops\
	 		)
	 		pipe_cmd = "e=$?"
	 		printf "%s && %s && %s || %s &\n", 
	 			cd_cmd,
	 			mkdir_cmd, 
	 			tar_cmd,
	 			pipe_cmd
	 		if(\
	 			k % concurrency == 0\
	 			&& k > 0 \
	 		){
	 			print "wait"
	 		}
		}
		print "wait"
	}' | bash >> "${MONITOR_FILE_PATH}" || e=$?

rm -rf "${UBUNTU_BACKUP_ROOTFS_DIR_PATH}"
mkdir "${UBUNTU_BACKUP_ROOTFS_DIR_PATH}"
echo "[3/4] cp rootfs.." >> "${MONITOR_FILE_PATH}"
readonly is_inner_storage="$(\
	echo "${UBUNTU_BACKUP_ROOTFS_DIR_PATH}" \
	| grep -E "^${APP_ROOT_PATH}"\
)"
case "${is_inner_storage}" in
	"") 
		cp2sd \
			-f "${UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH}" \
			-t "${UBUNTU_BACKUP_ROOTFS_DIR_PATH}"
		;;
	*)
		sudo cp -rvf \
			"${UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH}" \
			"${UBUNTU_BACKUP_DIR_PATH}/" \
			>> "${MONITOR_FILE_PATH}"
		;;
esac
echo "[4/4] crean up.." >> "${MONITOR_FILE_PATH}"
rm -rf "${UBUNTU_BACKUP_TEMP_ROOTFS_DIR_PATH}"
echo "Comp & Click CANCEL" >> "${MONITOR_FILE_PATH}"
bash "${NOTI_MANAGER_SHELL_PATH}"
