#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
readonly SUPPORT_DIR_PATH="/support"
readonly USR_LOCAL_BIN="/usr/local/bin"
readonly UBUNTU_SETUP_COMP_FILE="${SUPPORT_DIR_PATH}/ubuntuSetupComp.txt"
readonly UBUNTU_LAUNCH_COMP_FILE="${SUPPORT_DIR_PATH}/ubuntuLaunchComp.txt"
readonly FIRST_SETUP_OK_FILE="${SUPPORT_DIR_PATH}/firstSetupOk.txt"

kill_front_and_sub_process(){
	local killId=$(\
		ps aux \
		| grep -v "${PACKAGE_NAME}" \
		| grep \
			-e "pulseaudio"  \
			-e "wssh --address="  \
			-e "httpshd"  \
		| awk '{print $2}' \
	)
	case "${killId}" in
		"") return ;;
	esac
	echo --- kill process
	kill ${killId} 2>/dev/null
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
	local insert_str="${1}"
	local file_path="${2}"
	echo "### $FUNCNAME"
	echo "${!insert_str@}: ${insert_str}"
	echo "${!file_path@}: ${file_path}"
	
	if [ -z "${insert_str}" ] \
		|| [ -z "${file_path}" ]; then
			return
	fi
	local is_insert_str=$(\
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
	echo --- launch sshd server
	echo "DROPBEAR_SSH_PORT ${DROPBEAR_SSH_PORT}"
	dropbear -E -p ${DROPBEAR_SSH_PORT} >/dev/null &
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
	# sudo dropbear -E -p ${DROPBEAR_SSH_PORT} >/dev/null &
	# 2>&1 &
	echo "Type bellow command"
	echo -e "\tssh -p ${DROPBEAR_SSH_PORT}  cmdclick@{your android ip_address}"
	echo -e "\tpassword: ${CMDCLICK_USER}"
	echo --- wssh start
	# 192.168.0.4
	wssh --address='127.0.0.1' \
		--port=${WEB_SSH_TERM_PORT} &
	echo --- launch httpshd
	echo "HTTP2_SHELL_PORT ${HTTP2_SHELL_PORT}"
	httpshd \
		-port ${HTTP2_SHELL_PORT} &
	# echo --- launch shell2http
	# echo "HTTP2_SHELL_PORT ${HTTP2_SHELL_PORT}"
	# shell2http \
	# 	-port ${HTTP2_SHELL_PORT} \
	# 	-export-vars=APP_ROOT_PATH,MONITOR_DIR_PATH,APP_DIR_PATH,INTENT_MONITOR_PORT,INTENT_MONITOR_ADDRESS,REPLACE_VARIABLES_TSV_RELATIVE_PATH,UBUNTU_ENV_TSV_NAME,UBUNTU_SERVICE_TEMP_DIR_PATH \
	# 	/bash "bash ${HTTP2_SHELL_PATH}"  &
	wait
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

# install_shell2http(){
# 	local package_name="shell2http_1.16.0_linux_arm64.deb"
# 	curl \
# 		-L "https://github.com/msoap/shell2http/releases/download/v1.16.0/shell2http_1.16.0_linux_arm64.deb" \
# 		> "${package_name}"
# 	dpkg -i "${package_name}"
# 	rm -f "${package_name}"
# }


install_httpshd(){
	echo "### ${FUNCNAME}"
    local package_name="httpshd"
    curl \
        -sSL "https://github.com/puutaro/httpshd/releases/download/0.0.1/httpshd-0.0.1-arm64" \
        > "${package_name}"
    local usrlocalbin_httpshd="/usr/local/bin/${package_name}"
    mv -vf \
        "${package_name}" \
        "/usr/local/bin/${package_name}"
    chmod +x "${usrlocalbin_httpshd}"
}

install_repbash(){
	echo "### ${FUNCNAME}"
    local package_name="repbash"
    curl \
        -sSL "https://github.com/puutaro/repbash/releases/download/0.0.1/repbash-0.0.1-arm64" \
        > "${package_name}"
    local usrlocalbin_repbash="/usr/local/bin/${package_name}"
    mv -vf \
        "${package_name}" \
        "${usrlocalbin_repbash}"
    chmod +x "${usrlocalbin_repbash}"
}

install_bin(){
	install_httpshd &
	install_repbash &
	wait
}


install_base_pkg(){
	echo "### ${FUNCNAME}"
	apt-get update -y && apt-get upgrade -y
	install_require_pacakges
	install_pip3_pkg webssh
	install_pip3_pkg yt-dlp
	install_bin
	# install_shell2http
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


first_setup(){
	if [ -f "${FIRST_SETUP_OK_FILE}" ];then
		return
	fi
	echo "### ${FUNCNAME}"
	install_bin &
	apt-get purge --auto-remove -y sudo
	apt-get install -y sudo
	touch "${FIRST_SETUP_OK_FILE}"
}

echo_ubuntu_env_tsv_con(){
	local ubuntu_env_tsv_path="${SUPPORT_DIR_PATH}/${UBUNTU_ENV_TSV_NAME}"
	cat "${ubuntu_env_tsv_path}"
}

launch_extra_startup(){
	echo "### ${FUNCNAME}"
	bash "${SUPPORT_DIR_PATH}/extra_startup.sh"
}

put_launch_comp_file(){
	echo "### ${FUNCNAME}"
	local ubuntu_env_tsv_con=$(echo_ubuntu_env_tsv_con)
	local mustProcessGrepCmdsTxtName=$(\
		tsvar "${ubuntu_env_tsv_con}" "MUST_PROCESS_GREP_CMDS_TXT" \
	)
	local mustProcessGrepCmdsTxtCon=$(cat "${SUPPORT_DIR_PATH}/${mustProcessGrepCmdsTxtName}")
	local must_proc_num=$(echo "${mustProcessGrepCmdsTxtCon}" | wc -l)
	while :
	do
		local ps_con="$(ps aux)"
		local proc_grep_cmd=$(\
			echo  "${mustProcessGrepCmdsTxtCon}" \
			| awk -v ps_con="${ps_con}" '{
					printf "echo \x22%s\x22 %s\n", ps_con, $0
				}'\
		)
		local fact_proc_num=$(\
			echo "${proc_grep_cmd}" | bash | wc -l\
		)
		echo "# fact_proc_num: ${fact_proc_num}"
		echo "# must_proc_num: ${must_proc_num}"
		case "${fact_proc_num}" in
			"${must_proc_num}") 
				break
				;;
		esac
		sleep 1
	done
	echo "### Setup ok, launching..."
	touch "${UBUNTU_LAUNCH_COMP_FILE}"
}

update_and_upgrade(){
	rm -rf /var/lib/apt/lists/*
	apt-get update -y \
		&& apt-get -o Dpkg::Options::="--force-confnew" upgrade -y
}


if [ ! -f "${UBUNTU_SETUP_COMP_FILE}" ] ;then
	update_and_upgrade
fi
if [ ! -f "${UBUNTU_SETUP_COMP_FILE}" ] \
		&& [ "${CREATE_IMAGE_SWITCH}" = "ON" ];then
	install_base_pkg
	add_user
	setup_dropbear_sshserver
fi
if [ ! -f "${UBUNTU_SETUP_COMP_FILE}" ];then \
	# launch_setup
	touch "${UBUNTU_SETUP_COMP_FILE}"
fi
kill_front_and_sub_process
startup_launch_system &
launch_extra_startup &
put_launch_comp_file \
	"${ubuntu_env_tsv_con}"
first_setup
wait
exit 0
install_golang_and_go_package
