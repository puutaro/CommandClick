#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
R_USER="cmdclick"

insert_str_to_file(){
	insert_str="${1}"
	file_path="${2}"
	echo "### $FUNCNAME"
	echo "${!insert_str@}: ${insert_str}"
	echo "${!file_path@}: ${file_path}"
	
	if [ -z "${insert_str}" ] \
		|| [ -z "${file_path}" ]; then
			return
	fi
	is_insert_str=$(\
		cat "${file_path}" \
		| grep "${insert_str}"\
	)
	test -n "${is_insert_str}" \
	&& return
	echo "${insert_str}" >> ${file_path}
}
export -f insert_str_to_file

setup_sudo(){
	echo "### $FUNCNAME"
	apt-get install -y sudo
	apt-get install fakeroot -y
	insert_str_to_file \
		'%sudo ALL=(ALL) NOPASSWD:ALL' \
		"/etc/sudoers"
	insert_str_to_file \
		"127.0.1.1 $(hostname)" \
		"/etc/hosts"
}

setup_user(){
	echo "### $FUNCNAME: ${R_USER}"
	R_UID=2000
	R_GID=2000
	isUser=$(\
		cat "/etc/passwd" \
		| grep "${R_USER}"\
	)
	test -n "${isUser}" \
	&& return
	useradd $R_USER -s /bin/bash -m -u 2000 
	echo "${R_USER}:${R_USER}" | chpasswd
}


add_user(){
	is_user="$(cat "/etc/passwd" | grep "${R_USER}")"
	if [ -z "${is_user}"  ]; then
		apt-get update -y && apt-get upgrade -y
		apt-get fakeroot -y
		touch aa.txt
		ls -l
		echo --id
		id
		echo add user
		echo -- no passwd
		echo "root:$R_USER" | chpasswd
		# etc_passwd="/etc/passwd"
		# sed \
		# 	's/^root\:x\:0\:0\:root\:\/root:/root::0:0:root:\/root:/' \
		# 	-i "${etc_passwd}"
		# cat "${etc_passwd}"
		# sleep 10
		setup_sudo
		setup_user
		# apt-get install -y sudo
		# chown -R root:root /usr/bin/sudo
		# chmod -R 4755 /usr/bin/sudo
		# #ここでも頭に、usr/binの前にmediaPATHをつけ忘れないように
		# chown -R root:root /usr/lib/sudo/sudoers.so
		# chmod -R 4755 /usr/lib/sudo/sudoers.so
		# chown -R root:root /etc/sudoers
		# chown root:root /etc/sudo.conf
		# chown root:root /usr/bin/sudo
		# chmod 755 /etc/sudoers.d
		# chmod 440 /etc/sudoers.d/*
		
		# insertHost="127.0.1.1 $(hostname)"
		# isInsertHost=$(\
		# 	cat "${insertHost}" \
		# 	| grep "${insertHost}"\
		# )
		# test -z "${isInsertHost}" \
		# && echo "${insertHost}" >> /etc/hosts
		
		# REGISTER_UID=2000
		# REGISTER_GID=2000
		# echo -- sudo ls by root
		# sudo ls -l
		# echo --- sudo ls -l /etc/sudo.conf
		# sudo ls -l /etc/sudo.conf
		# echo $INITIAL_USERNAME:$INITIAL_PASSWORD | chpasswd
		# chsh -s /bin/bash $INITIAL_USERNAME
		# groupadd -g $R_GID ${R_USER} && \
		#     useradd -m -s /bin/bash -u $R_UID -g $R_GID ${R_USER}
		# useradd -ms /bin/bash "${R_USER}" && \
		# usermod -aG sudo "${R_USER}"
		# groupadd -g 1000 "${R_USER}"
		#  usermod -aG "${R_USER}" "${R_USER}"
		
		# R_UID=2000
		# R_GID=2000
		# isUser=$(\
		# 	cat "/etc/passwd" \
		# 	| grep "${R_USER}"\
		# )
		# test -n "${isUser}" \
		# && return
		# useradd $R_USER -s /bin/bash -m -u 2000 
		# echo "${R_USER}:${R_USER}" | chpasswd
		
		# chmod -R 777 /home/${R_USER}
		echo --- touch /external/touch.txt
		touch /external/touch.txt
		echo --- ls -l /external/touch.txt
		ls -l /external/touch.txt
		echo --- id
		id
		echo -- sudo ls by root
		sudo ls -l
		# pamDSuFilePath="/etc/pam.d/su" 
		# groupadd -g $REGISTER_GID "${R_USER}" && \
    	# useradd -m -s /bin/bash -u $REGISTER_UID -g $REGISTER_GID "${R_USER}"
    	# # echo "${R_USER} ALL=NOPASSWD: /bin/su" >> /etc/sudoers
    	# groupadd -g ${REGISTER_GID} "${R_USER}"
    	# usermod -aG "${R_USER}" "${R_USER}"
    	# sed "8i auth       [success=ignore default=1] pam_succeed_if.so user = ${R_USER}" -i "${pamDSuFilePath}"
    	# sed "9i auth       sufficient   pam_succeed_if.so use_uid user ingroup ${R_USER}" -i "${pamDSuFilePath}"
    	# sleep 0.5
		# cat "${pamDSuFilePath}" | head -15

		# echo ''${R_USER}'   ALL=(ALL:ALL) NOPASSWD:ALL' >> /etc/sudoers
		# groupadd -g ${REGISTER_GID} "${R_USER}"
    	# usermod -aG "${R_USER}" "${R_USER}"
    	# echo "auth [success=ignore default=1] pam_succeed_if.so user = ${R_USER}" >> "${pamDSuFilePath}"
		# echo "auth sufficient   pam_succeed_if.so use_uid user ingroup ${R_USER}" >> "${pamDSuFilePath}"
    	# noPassSuLine="auth sufficient pam_wheel.so trust group=wheel"
    	# sed "25i ${noPassSuLine}" -i "${pamDSuFilePath}"
    	# pamDSuCon=$(cat "${pamDSuFilePath}")
    	# isNoPassSu=$(\
    	# 	echo "${pamDSuCon}" \
    	# 	| grep "${noPassSuLine}" \
    	# )
    	# case "${isNoPassSu}" in
    	# 	"") 
		# 		cat  \
		# 			<(echo "${pamDSuCon}") \
		# 			<(echo "${noPassSuLine}") \
		# 			> "${pamDSuFilePath}"
		# 	;;
		# esac
		# sleep 0.5
		# echo --pamDSuFileCon 
		# WHEEL_GP="wheel"
		# echo --- confirm ${WHEEL_GP}
		# cat "${pamDSuFilePath}" | grep "${noPassSuLine}"
		# echo '%wheel ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers
		# sudo addgroup ${WHEEL_GP}
		# sudo usermod -aG ${WHEEL_GP} "${R_USER}"
		
		# echo --- sudo ls -l /etc/sudo.conf
		# sudo ls -l /etc/sudo.conf
		# echo ''${R_USER}'   ALL=(ALL:ALL) NOPASSWD:ALL'
	fi
}

dpkg_err_solution(){
	echo "### $FUNCNAME"
	echo --- dpkg --configure -a
	sudo dpkg --configure -a
	echo --- rm -rf /var/lib/dpkg/info/
	sudo rm -rf /var/lib/dpkg/info/*
	echo -- upgrade
	sudo apt-get upgrade -y
}

export -f dpkg_err_solution

install_pulseaudio(){
	apt-get -y install aptitude
	touch /var/lib/aptitude/pkgstates
	aptitude install -y pulseaudio \
		|| echo pulse failure
	dpkg_err_solution
	cp -avf \
		"/support/default.pa" \
		"/etc/pulse/default.pa"
}

install_add_repository(){
	echo "### $FUNCNAME"
	apt-get install -y software-properties-common
	dpkg_err_solution
}

install_golang_and_server(){
	echo "## install_golang_and_server"
	su - "${R_USER}" <<-EOF
	echo --- add-apt-repository -y ppa:longsleep/golang-backports
	sudo add-apt-repository -y ppa:longsleep/golang-backports
	echo --- sudo apt-get update
	sudo apt-get update
	echo --- sudo apt-get install -y golang-go
	sudo apt-get install -y golang-go
	# echo --- dpkg --configure -a
	# dpkg --configure -a
	# echo --- rm -rf /var/lib/dpkg/info/
	# sudo rm -rf /var/lib/dpkg/info/*
	# echo -- upgrade
	# sudo apt-get upgrade -y
	go version
	go install github.com/skanehira/rtty@latest
	export GOPATH=\$HOME/go
	export GOBIN=\$GOPATH/bin
	export PATH=\$PATH:\$GOBIN
	\$HOME/go/bin/rtty run bash -a 127.0.0.1 -p 20080 --font "Cica Regular" --font-size 20
	EOF
}


install_python_ssh_server(){
	echo "### $FUNCNAME"
	su - "${R_USER}" <<-EOF
		echo --- sudo apt install -y python3-pip
		sudo apt install -y python3-pip
		wget https://bootstrap.pypa.io/get-pip.py
		python3 get-pip.py
		echo --- pip3 install webssh
		pip3 install webssh
		echo --- "wssh --address=127.0.0.1 --port=18080"
		wssh --address='127.0.0.1' --port=18080
		while true;
		do 
			sleep 1; 
			echo -n ssh[\${waitTimes}]..
			waitTimes=\$(( waitTimes + 1 ))
		done
	EOF
}

install_opensshserver(){
	echo "### $FUNCNAME"
	# su - "${R_USER}" <<-EOF
	dropbear_pakcage="dropbear"
	local is_installed=$(\
		apt list --installed | grep "${dropbear_pakcage}"\
	)
	case "${is_installed}" in
		"")
			echo --- install_opensshserver
			sudo apt-get install -y \
				"${dropbear_pakcage}"
			# sudo apt-get install -y \
			# 	openssh-server
		    # echo "${R_USER}:${R_USER}" | chpasswd

		    # dpkg_err_solution

		  	dropbearkey -t dss -s 1024 -f /etc/dropbear/dropbear_dss_host_key
		    dropbearkey -t rsa -s 2048 -f /etc/dropbear/dropbear_rsa_host_key
		    dropbearkey -t ecdsa -s 521 -f /etc/dropbear/dropbear_ecdsa_host_key
		 ;;
	esac
	dropbear -E -p 10022 >/dev/null 2>&1
	echo "Type bellow command"
	echo -e "\tssh -p 10022  cmdclick@{your android ip_address}"
	echo -e "\tpassword: ${R_USER}"


	# etc_ssh_dir="/etc/ssh"
	# sshd_config="sshd_config"
	# support_sshd_config="/support/${sshd_config}"
	# etc_ssh_sshd_config="${etc_ssh_dir}/sshd_config"
	# echo --- ls ${etc_ssh_dir}
	# ls "${etc_ssh_dir}"
	# echo --- cp -arvf ${support_sshd_config} ${etc_ssh_dir}/
	# cp -arvf ${support_sshd_config} ${etc_ssh_dir}/

	# sudo sed 's/^\#Port 22/Port 10022/' -i "${etc_ssh_dir}/sshd_config"
	# echo --- rm /run/sshd.pid
	# rm -f /run/sshd.pid
	# sleep 0.5
	# sleep 5 && echo ---- grep Port ${etc_ssh_dir}/sshd_config && sudo grep Port ${etc_ssh_sshd_config} &
	# sleep 7 && echo --- sudo netstat -plnt && sudo netstat -plnt &
	# sleep 9 & echo --- sudo iptables-save && sudo iptables-save &
	# echo --- service ssh start
	# # sudo service ssh start
	# sudo /etc/init.d/ssh start
	# waitTimes=0
	# while true; 
	# do 
	# 	sleep 1; 
	# 	# echo -n ssh[${waitTimes}]..
	# 	# waitTimes=$(( waitTimes + 1 ))
	# done
	# EOF
}

pulse_launch(){
	su - "${R_USER}" <<-EOF
	echo \$USER
	echo --- pulseaudio --start
	curl 127.0.0.1:10081 | sh
	# pulseaudio --kill; sleep 0.5 \
	# ; PULSE_SERVER=  && pulseaudio --start \
	# && pactl load-module module-null-sink sink_name=TCP_output \
	# && pacmd update-sink-proplist TCP_output device.description=TCP_output \
	# && pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=TCP_output.monitor record=true port=10080 listen=0.0.0.0
	# echo 00
	for sec in \$(seq 5)
	do
	  echo \${sec}
	  sleep 1
	done
	espeak "sound quality test sound quality test  sound quality test sound quality test sound quality test sound quality test"
	echo --- ps aux grep proot 
	ps aux | grep proot 
	# echo --- kill pulse
	# kill -9 \$(ps aux | grep pulse | grep -v "/usr/bin" | grep -v "/usr/local/bin" | grep -v grep | awk '{print \$2}')
	# kill -9 \$(ps aux | grep proot | awk '{print \$2}')
	EOF
}


user_cmd_sudo_test(){
	echo "### $FUNCNAME"
	su - "${R_USER}" <<-EOF
	echo -- sudo ls by "${R_USER}"
	sudo ls -l
	echo --- sudo apt-get install -y jq
	sudo apt-get install -y jq
	echo --- touch touch.txt
	touch touch.txt
	echo --- chown ${R_USER}:${R_USER} touch.txt
	chown ${R_USER}:${R_USER} touch.txt
	echo --- apt install -y jq
	apt install -y jq
	echo --- ls -l
	ls -l
	echo --- ls -l /etc/sudo.conf
	ls -l /etc/sudo.conf
	echo --- ls -l / 
	ls -l /
	echo --- ls -l /external/touch.txt
	ls -l /external/touch.txt
	echo --- id
	id
	echo --- which su
	which su
	EOF
}

install_require_packge(){
	echo "### $FUNCNAME"
	sudo apt-get install -y \
		man \
		manpages-dev \
		git \
		make \
		wget \
		nano \
		less \
		espeak \
		curl \
		python3-pip
	sudo pip3 install webssh
}

wait_cmd(){
	while true; 
	do 
		sleep 1; 
	done	
}


add_user
user_cmd_sudo_test
install_require_packge
install_add_repository
install_opensshserver
install_pulseaudio
pulse_launch
wait_cmd
exit 0
install_golang_and_server

exit 0
install_python_ssh_server
exit 0
echo --
user_cmd_sudo_test
exit 0
echo \$HOME
echo --
pwd
echo --awk
echo aa | awk '{print $1}'
echo aa | nawk '{print $1}'
which --help
echo --- apt-get update and apt-getbupgrade
apt-get update -y && apt-get upgrade -y

apt-get install -y \
	firmware-sof-signed \
	initramfs-tools

# apt-get -y install aptitude
# touch /var/lib/aptitude/pkgstates
# aptitude install -y pulseaudio || echo pulse failure
# #pulseaudio --start
# echo --- dpkg --configure -a
# dpkg --configure -a
# echo --- rm -rf var/lib/dpkg/info/*
# rm -rf var/lib/dpkg/info/*
# echo update
# apt upgrade -y
echo --- apt-get install pulseaudio-utils
apt-get install pulseaudio-utils
# echo --- apt-get install -y jq
# apt-get install -y jq 
# echo --- apt-get install -y fzf 
# apt-get install -y fzf 
# echo --- apt-get install -y mpv 
# apt-get install -y mpv 
# echo --- apt-get install -y git
# apt-get install -y git
# echo --- python3-pip
# apt install -y python3-pip
# echo --- pip3 install yt-dlp
# pip3 install yt-dlp

echo --- apt-get install -y espeak
apt-get install -y \
	espeak \
	pv
apt-get install -y espeak
echo --- apt-get search curl
# apt-get search curl
echo --- curl install
apt-get install -y curl
echo --- curl -LI http://google.com/
curl -LI http://google.com/
echo --- ls /external
ls /external
apt-get install -y initramfs-tools
apt-get install -y pulseaudio
apt-get install -y espeak