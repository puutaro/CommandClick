#!/bin/bash


expport rootfsTarGz="rootfs.tar.gz"

cd /
sudo su - <<-EOF
	sudo apt-get purge \
		--auto-remove -y sudo
	tar -cvpzf ${rootfsTarGz} \
		--exclude=/${rootfsTarGz} \
		--exclude=/storage \
		--exclude=/host-rootfs \
		--one-file-system \
		/ 
	cp -f ${rootfsTarGz} \
		/storage/emulated/0/Documents/ 
EOF