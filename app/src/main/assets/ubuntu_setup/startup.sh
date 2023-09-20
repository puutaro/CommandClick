#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
R_USER="cmdclick"


install_pip3_pkg(){
	local package="${1:-}"
	local no_sudo="${2:-}"
	echo "### ${FUNCNAME}"
	case "${package}" in
		"") return
		;;
	esac
	local is_installed=$(\
		pip3 list | grep "${package}"\
	)
	case "${is_installed}" in
		"") ;;
		*)
			echo "pip3 installed: ${package}"
			return
			;;
	esac
	local install_cmd="pip3 install ${package}"
	case "${no_sudo}" in
		"")
			;;
		*)
			install_cmd="sudo ${install_cmd}"
			;;
	esac
	echo "pip3 installing.. ${package}"
	${install_cmd}
}


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
	insert_str_to_file \
		'%sudo ALL=(ALL) NOPASSWD:ALL' \
		"/etc/sudoers"
	insert_str_to_file \
		"127.0.1.1 $(hostname)" \
		"/etc/hosts"
}

setup_user(){
	echo "### $FUNCNAME: ${R_USER}"
	local R_UID=2000
	local R_GID=2000
	local isUser=$(\
		cat "/etc/passwd" \
		| grep "${R_USER}"\
	)
	test -n "${isUser}" \
	&& return
	useradd $R_USER -s /bin/bash -m -u 2000 
	echo "${R_USER}:${R_USER}" | chpasswd
}


add_user(){
	local is_user="$(\
		cat "/etc/passwd" | grep "${R_USER}"\
	)"
	test -n "${is_user}" \
	&& return
	if [ -z "${is_user}"  ]; then
		touch aa.txt
		ls -l
		echo --id
		id
		echo add user
		echo -- no passwd
		echo "root:$R_USER" | chpasswd
		setup_sudo
		setup_user
	
		echo --- touch /external/touch.txt
		touch /external/touch.txt
		echo --- ls -l /external/touch.txt
		ls -l /external/touch.txt
		echo --- id
		id
		echo -- sudo ls by root
		sudo ls -l
	fi
}

dpkg_err_solution(){
	echo "### $FUNCNAME"
	echo --- dpkg --configure -a
	dpkg --configure -a
	echo --- rm -rf /var/lib/dpkg/info/
	rm -rf /var/lib/dpkg/info/*
	echo -- upgrade
	apt-get upgrade -y
}

export -f dpkg_err_solution

install_pulseaudio(){
	local pulse_pkg_name="pulseaudio"
	local is_installed=$(\
		apt list --installed \
		| grep "${pulse_pkg_name}"\
	)
	case "${is_installed}" in
		"") 
			;;
		*) 
			echo "install_ok: ${pulse_pkg_name}"
			return
			;;
	esac
	apt-get install -y "${pulse_pkg_name}"
	dpkg_err_solution
	cp -avf \
		"/support/default.pa" \
		"/etc/pulse/default.pa"
}

install_add_repository(){
	echo "### $FUNCNAME"
	local software_pkg_name="software-properties-common"
	local is_installed=$(\
		apt list --installed \
		| grep "${software_pkg_name}"\
	)
	case "${is_installed}" in
		"") 
			;;
		*) 
			echo "install_ok: ${software_pkg_name}"
			return
			;;
	esac
	apt-get install -y \
		software-properties-common
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

	go_package="golang-go"
	is_installed=\$(\
		apt list --installed \
		| grep "\${go_package}" \
	)
	case "\${is_installed}" in
		"") 
			sudo apt install -y "\${go_package}"
			;;
		*)
			echo "installed: \${go_package}"
			;;
	esac
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
		pip_package="python3-pip"
		is_installed=\$(\
			apt list --installed \
			| grep "\${pip_package}" \
		)
		case "\${is_installed}" in
			"") 
				sudo apt install -y "\${pip_package}"
				;;
			*)
				echo "installed: \${pip_package}"
				;;
		esac
		# sudo apt install -y python3-pip
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
	dropbear_package="dropbear"
	local is_installed=$(\
		apt list --installed | grep "${dropbear_package}"\
	)
	case "${is_installed}" in
		"")
			echo --- install_opensshserver
			apt-get install -y "${dropbear_package}"

		  	dropbearkey -t dss -s 1024 -f /etc/dropbear/dropbear_dss_host_key
		    dropbearkey -t rsa -s 2048 -f /etc/dropbear/dropbear_rsa_host_key
		    dropbearkey -t ecdsa -s 521 -f /etc/dropbear/dropbear_ecdsa_host_key
		 ;;
	esac
	dropbear -E -p 10022 >/dev/null 2>&1
	echo "Type bellow command"
	echo -e "\tssh -p 10022  cmdclick@{your android ip_address}"
	echo -e "\tpassword: ${R_USER}"
}

startup_launch_cmd(){
	su - "${R_USER}" <<-EOF
	echo \$USER
	echo --- pulseaudio --start
	curl 127.0.0.1:10081 | sh
	for sec in \$(seq 5)
	do
	  echo \${sec}
	  sleep 1
	done
	espeak "sound quality test sound quality test  sound quality test sound quality test sound quality test sound quality test"
	
	echo --- ps aux grep proot 
	ps aux | grep proot 
	echo --- wssh start
	wssh --address='192.168.0.4' --port=18080 \
		>/dev/null 2>&1 &
	
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
	jq_package="jq"
	is_installed=\$(\
		apt list --installed \
		| grep "\${jq_package}" \
	)
	case "\${is_installed}" in
		"") 
			sudo apt install -y "\${jq_package}"
			;;
		*)
			echo "installed: \${jq_package}"
			;;
	esac
	sudo apt-get install -y jq
	echo --- touch touch.txt
	touch touch.txt
	echo --- chown ${R_USER}:${R_USER} touch.txt
	chown ${R_USER}:${R_USER} touch.txt
	echo --- apt install -y jq
	jq_package="jq"
	is_installed=\$(\
		apt list --installed \
		| grep "\${jq_package}" \
	)
	case "\${is_installed}" in
		"") 
			apt install -y "\${jq_package}"
			;;
		*)
			echo "installed: \${jq_package}"
			;;
	esac
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

make_package_list(){
	local packages="${1:-}"
	awk \
		-v packages="${packages}" \
		'
		BEGIN {
			gsub(/[ \t]/, "", packages)
			gsub(/\n/, " ", packages)
			sub(/^ /, "", packages)
			print packages
	}'
}



install_require_pacakges(){
	echo "### $FUNCNAME"
	local require_packages=$(\
		make_package_list "
			curl 
			sudo 
			fakeroot
			colorized-logs
			espeak
			python3-pip
			" \
	)
	apt-get install -y ${require_packages}
	# apt_install git
	# apt_install make
	# apt_install wget
	# apt_install nano
	# apt_install less
	install_pip3_pkg webssh
	# install_user_package
}


install_base_pkg(){
	echo "### ${FUNCNAME}"
	apt-get update -y && apt-get upgrade -y
	install_pulseaudio
	install_require_pacakges
	# apt_install \
	# 	"firmware-sof-signed"
	# apt_install \
	# 	"initramfs-tools"
	# apt_install man
	# apt_install manpages-dev
	# install_add_repository
}

install_user_package(){
	su - "${R_USER}" <<-EOF
		ansi2html_package="ansi2html"
		is_installed=\$(\
			pip3 list | grep "\${ansi2html_package}"\
		)
		case "\${is_installed}" in
			"") 
				pip3 install "\${ansi2html_package}"
				;;
			*)
				echo "pip3 installed: \${ansi2html_package}"
				;;
		esac
	EOF
}

install_nodjs(){
	apt-get install -y nodejs
}


wait_cmd(){
	while true; 
	do 
		sleep 1; 
	done	
}


install_base_pkg
add_user
install_opensshserver
startup_launch_cmd
wait_cmd
exit 0
install_golang_and_server

exit 0
# user_cmd_sudo_test
install_python_ssh_server
exit 0
echo --
user_cmd_sudo_test
