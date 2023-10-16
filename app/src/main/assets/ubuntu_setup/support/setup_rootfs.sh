#!/bin/bash


# It is assumed that you are root user.
# sudo -su -

export backup_dir_path="${APP_ROOT_PATH}/backup" \
;export rootfsTarGz="rootfs.tar.gz" \
; cd / \
; sudo apt-get purge \
	--auto-remove -y sudo \
; tar -cvpzf ${rootfsTarGz} \
	--exclude=/${rootfsTarGz} \
	--exclude=/storage \
	--exclude=/host-rootfs \
	--one-file-system \
	/ \
; mkdir -p "${backup_dir_path}" \
; cp -vf ${rootfsTarGz} \
	"${backup_dir_path}/"


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