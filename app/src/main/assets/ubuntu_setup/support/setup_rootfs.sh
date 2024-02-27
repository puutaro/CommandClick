#!/bin/bash


# It is assumed that you are root user.
# sudo -su -

export backup_dir_path="${APP_ROOT_PATH}/ubuntu/backup/temp" \
;export rootfsTarGz="rootfs.tar.gz" \
; cd / \
; apt-get purge \
	--auto-remove -y sudo \
;mkdir -p "${backup_dir_path}" \
;tar \
	-cvpzf "${backup_dir_path}/${rootfsTarGz}" \
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
	/  
# \
# ; mkdir -p "${backup_dir_path}" \
# ; cp -vf ${rootfsTarGz} \
# 	"${backup_dir_path}/"

# tar -cvpzf ${rootfsTarGz} \
# 	--exclude=/${rootfsTarGz} \
# 	--exclude=/storage \
# 	--exclude=/host-rootfs \
# 	--one-file-system \
# 	/

# export rootfsTarGz="rootfs.tar.gz" \
# ; cd / \
# ; rm -f "/etc/pulse/default.pa" \
# ; sudo apt-get purge \
# 	--auto-remove -y sudo pulseaudio \
# ; tar -cvpzf ${rootfsTarGz} \
# 	--exclude=/${rootfsTarGz} \
# 	--exclude=/storage \
# 	--exclude=/host-rootfs \
# 	--one-file-system \
# 	/ \
# ; cp -f ${rootfsTarGz} \
# 	/storage/emulated/0/Documents/ 