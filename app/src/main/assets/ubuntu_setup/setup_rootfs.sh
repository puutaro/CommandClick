#!/bin/bash


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