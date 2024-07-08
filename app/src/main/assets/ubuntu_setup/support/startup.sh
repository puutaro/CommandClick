#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
readonly SUPPORT_DIR_PATH="/support"
readonly USR_LOCAL_BIN="/usr/local/bin"
readonly UBUNTU_SETUP_COMP_FILE="${SUPPORT_DIR_PATH}/ubuntuSetupComp.txt"
readonly UBUNTU_LAUNCH_COMP_FILE="${SUPPORT_DIR_PATH}/ubuntuLaunchComp.txt"

kill_front_and_sub_process(){
	local killId=$(\
		ps aux \
		| grep -v "${PACKAGE_NAME}" \
		| grep \
			-e "pulseaudio"  \
			-e "wssh --address="  \
			-e "shell2http"  \
		| awk '{print $2}' \
	)
	case "${killId}" in
		"") return ;;
	esac
	echo --- kill process
	kill ${killId}
}

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
	echo "### $FUNCNAME: ${CMDCLICK_USER}"
	local R_UID=2000
	local R_GID=2000
	local isUser=$(\
		cat "/etc/passwd" \
		| grep "${CMDCLICK_USER}"\
	)
	test -n "${isUser}" \
	&& return
	useradd $CMDCLICK_USER -s /bin/bash -m -u 2000 
	echo "${CMDCLICK_USER}:${CMDCLICK_USER}" | chpasswd
}


add_user(){
	local is_user="$(\
		cat "/etc/passwd" | grep "${CMDCLICK_USER}"\
	)"
	test -n "${is_user}" \
	&& return
	if [ -z "${is_user}"  ]; then
		echo add user
		echo -- no passwd
		echo "root:$CMDCLICK_USER" | chpasswd
		setup_sudo
		setup_user
	fi
}

dpkg_err_solution(){
	echo "### $FUNCNAME"
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

install_golang_and_go_package(){
	echo "## $FUNCNAME"
	su - "${CMDCLICK_USER}" <<-EOF
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
	go install github.com/msoap/shell2http@latest
	export GOPATH=\$HOME/go
	export GOBIN=\$GOPATH/bin
	export PATH=\$PATH:\$GOBIN
	# \$HOME/go/bin/rtty run bash -a 127.0.0.1 -p 20080 --font "Cica Regular" --font-size 20
	EOF
}

setup_dropbear_sshserver(){
	echo "### $FUNCNAME"
	echo --- setup_dropbear
	rm -r \
		"/etc/dropbear/dropbear_dss_host_key" \
		"/etc/dropbear/dropbear_rsa_host_key" \
		"/etc/dropbear/dropbear_ecdsa_host_key"
  	dropbearkey -t dss -s 1024 -f /etc/dropbear/dropbear_dss_host_key
    dropbearkey -t rsa -s 2048 -f /etc/dropbear/dropbear_rsa_host_key
    dropbearkey -t ecdsa -s 521 -f /etc/dropbear/dropbear_ecdsa_host_key
}

startup_launch_system(){
	echo "### $FUNCNAME"
	rm -rf  /tmp/pulse* &

	su - "${CMDCLICK_USER}" <<-EOF
	export APP_ROOT_PATH="${APP_ROOT_PATH}"
	export MONITOR_DIR_PATH="${MONITOR_DIR_PATH}"
    export APP_DIR_PATH="${APP_DIR_PATH}"
    export INTENT_MONITOR_PORT="${INTENT_MONITOR_PORT}"
    export INTENT_MONITOR_ADDRESS="${INTENT_MONITOR_ADDRESS}"
    export REPLACE_VARIABLES_TSV_RELATIVE_PATH="${REPLACE_VARIABLES_TSV_RELATIVE_PATH}"
   	export UBUNTU_ENV_TSV_NAME="${UBUNTU_ENV_TSV_NAME}"
   	export UBUNTU_SERVICE_TEMP_DIR_PATH="${UBUNTU_SERVICE_TEMP_DIR_PATH}"
	echo \$USER
	echo --- launch sshd server
	echo "DROPBEAR_SSH_PORT ${DROPBEAR_SSH_PORT}"
	sudo dropbear -E -p ${DROPBEAR_SSH_PORT} >/dev/null &
	# 2>&1 &
	echo "Type bellow command"
	echo -e "\tssh -p ${DROPBEAR_SSH_PORT}  cmdclick@{your android ip_address}"
	echo -e "\tpassword: ${CMDCLICK_USER}"
	echo --- wssh start
	# 192.168.0.4
	wssh --address='127.0.0.1' \
		--port=${WEB_SSH_TERM_PORT} &
	echo --- launch shell2http
	echo "HTTP2_SHELL_PORT ${HTTP2_SHELL_PORT}"
	shell2http \
		-port ${HTTP2_SHELL_PORT} \
		-export-vars=APP_ROOT_PATH,MONITOR_DIR_PATH,APP_DIR_PATH,INTENT_MONITOR_PORT,INTENT_MONITOR_ADDRESS,REPLACE_VARIABLES_TSV_RELATIVE_PATH,UBUNTU_ENV_TSV_NAME,UBUNTU_SERVICE_TEMP_DIR_PATH \
		/bash "bash ${HTTP2_SHELL_PATH}"  &
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
	install_pulseaudio
	install_add_repository
	local require_packages=$(\
		make_package_list "
			curl 
			sudo 
			fakeroot
			git
			dropbear
			espeak
			python3-pip
			translate-shell
			bsdmainutils
			rsync
			sshpass
			" \
	)
	apt-get install -y ${require_packages}
}

install_shell2http(){
	local package_name="shell2http_1.16.0_linux_arm64.deb"
	curl \
		-L "https://github.com/msoap/shell2http/releases/download/v1.16.0/shell2http_1.16.0_linux_arm64.deb" \
		> "${package_name}"
	dpkg -i "${package_name}"
	rm -f "${package_name}"
}

install_repbash(){
	echo "### ${FUNCNAME}"
	local package_name="repbash"
	curl \
		-sSL "https://github.com/puutaro/repbash/releases/download/0.0.1/repbash-0.0.1-arm64" \
		> "${package_name}"
	local usrlocalbin_repbash="/usr/local/bin/${package_name}"
	mv \
		"${package_name}" \
		"${usrlocalbin_repbash}"
	chmod +x "${usrlocalbin_repbash}"
	echo "install ok"
}


install_base_pkg(){
	echo "### ${FUNCNAME}"
	apt-get update -y && apt-get upgrade -y
	install_require_pacakges
	install_pip3_pkg webssh
	install_pip3_pkg yt-dlp
	install_shell2http
}

install_user_package(){
	echo "### ${FUNCNAME}"
	su - "${CMDCLICK_USER}" <<-EOF
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

launch_setup(){
	echo "### ${FUNCNAME}"
	local profile_path="/etc/profile"
	insert_str_to_file \
		'export APP_ROOT_PATH="'${APP_ROOT_PATH}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export MONITOR_DIR_PATH="'${MONITOR_DIR_PATH}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export APP_DIR_PATH="'${APP_DIR_PATH}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export INTENT_MONITOR_PORT="'${INTENT_MONITOR_PORT}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export INTENT_MONITOR_ADDRESS="'${INTENT_MONITOR_ADDRESS}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export REPLACE_VARIABLES_TSV_RELATIVE_PATH="'${REPLACE_VARIABLES_TSV_RELATIVE_PATH}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export UBUNTU_ENV_TSV_NAME="'${UBUNTU_ENV_TSV_NAME}'"' \
		"${profile_path}"
	insert_str_to_file \
		'export UBUNTU_SERVICE_TEMP_DIR_PATH="'${UBUNTU_SERVICE_TEMP_DIR_PATH}'"' \
		"${profile_path}"
	apt-get purge --auto-remove -y sudo
	apt-get install -y sudo
	install_repbash
}

install_nodjs(){
	apt-get install -y nodejs
}

launch_extra_startup(){
	echo "### ${FUNCNAME}"
	local support_dir_path="/support"
	local ubuntu_env_tsv_path="${support_dir_path}/${UBUNTU_ENV_TSV_NAME}"
	local ubuntu_env_tsv_con="$(cat "${ubuntu_env_tsv_path}")"
	local ubuntuExtraStartupShellsPath=$(\
		tsvar "${ubuntu_env_tsv_con}" "UBUNTU_EXTRA_STARTUP_SHELLS_PATH" \
	)
	local pulse_macro_shell="PULSE.sh"
	local ubuntuExtraStartupShellsCon=$(\
		cat "${ubuntuExtraStartupShellsPath}"\
	)
	local is_pulse="$(\
		echo "${ubuntuExtraStartupShellsCon}" \
		| awk \
			-v pulse_macro_shell="${pulse_macro_shell}" \
			-F '\t' '{
			shell_path = $1
			gsub(/^[ \t]+/, "", shell_path)
			gsub(/[ \t]+$/, "", shell_path)
			if(shell_path != pulse_macro_shell) next
			extra_map_con = $2
			if(extra_map_con ~ /disable=ON/) next
			if(extra_map_con ~ /disable=on/) next
			print $0
		}' \
	)"
	case "${is_pulse}" in
		"") ;;
		*) bash "${support_dir_path}/pulse_setup.sh" &
			;;
	esac
	echo "${ubuntuExtraStartupShellsCon}"\
	 | awk \
	 	-F '\t' \
	 	-v pulse_macro_shell="${pulse_macro_shell}" \
	 '{
	 	shell_path = $1
		gsub(/^[ \t]+/, "", shell_path)
		gsub(/[ \t]+$/, "", shell_path)
		if(!shell_path) next
		if( shell_path == pulse_macro_shell ) next
		extra_map_con = $2
		if( extra_map_con ~ /disable=ON/) next
		if( extra_map_con ~ /disable=on/) next
		printf "bash \x22%s\x22 &", shell_path
	}' | bash 
}


wait_cmd(){
	echo "### ${FUNCNAME}"
	while true; 
	do 
		sleep 1; 
	done	
}


if [ ! -f "${UBUNTU_SETUP_COMP_FILE}" ] \
		&& [ "${CREATE_IMAGE_SWITCH}" = "on" ];then
	install_base_pkg
	add_user
	setup_dropbear_sshserver
fi
if [ ! -f "${UBUNTU_SETUP_COMP_FILE}" ];then \
	launch_setup
	touch "${UBUNTU_SETUP_COMP_FILE}"
fi
kill_front_and_sub_process
startup_launch_system
launch_extra_startup
touch "${UBUNTU_LAUNCH_COMP_FILE}"
echo "### Setup and launch ok"
wait_cmd
exit 0
install_golang_and_go_package
