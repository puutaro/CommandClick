#!/bin/bash

R_USER="cmdclick"

startup_launch_front_system(){
	echo "### $FUNCNAME"
	su - "${R_USER}" <<-EOF
	echo \$USER
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
	EOF
}


bash "/support/kill_front_process.sh"

startup_launch_front_system