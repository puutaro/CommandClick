#!/bin/bash


# It is assumed that you are root user.
# sudo -su -

export rootfsTarGz="rootfs.tar.gz" \
; cd / \
; sudo apt-get purge \
	--auto-remove -y sudo \
; tar -cvpzf ${rootfsTarGz} \
	--exclude=/${rootfsTarGz} \
	--exclude=/storage \
	--exclude=/host-rootfs \
	--one-file-system \
	/ \
; cp -f ${rootfsTarGz} \
	/storage/emulated/0/Documents/ 


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