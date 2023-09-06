#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
R_USER="cmdclick"

echo --- startupscript2

user_cmd(){
	su - "${R_USER}" <<-EOF
	echo \$USER
	echo --- pulseaudio --start
	pulseaudio --verbose --start --exit-idle-time=-1 --load="module-native-protocol-tcp auth-ip-acl=127.0.0.1 auth-anonymous=1"
	export PULSE_SERVER=tcp:127.0.0.1
	sleep 5
	echo --- espeak sound quality test
	espeak "sound quality test sound quality test  sound quality test sound quality test sound quality test sound quality test" &
	espeak_pid=\$?
	echo \${espeak_pid}
	sleep 5
	kill  ${espeak_pid}
	EOF
}

user_cmd