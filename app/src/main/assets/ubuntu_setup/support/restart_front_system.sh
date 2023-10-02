#!/bin/bash


startup_launch_cmd(){
	su - "${CMDCLICK_USER}" <<-EOF
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
		# \
		# >/dev/null 2>&1 &
	echo --- launch shell2http
	echo "HTTP2_SHELL_PORT ${HTTP2_SHELL_PORT}"
	shell2http \
		-port ${HTTP2_SHELL_PORT} \
		/bash "bash \$HOME/cmd/cmd.sh"  &
		# \
		# >/dev/null 2>&1 &
	# echo --- kill pulse
	# kill -9 \$(ps aux | grep pulse | grep -v "/usr/bin" | grep -v "/usr/local/bin" | grep -v grep | awk '{print \$2}')
	# kill -9 \$(ps aux | grep proot | awk '{print \$2}')
	EOF
}
