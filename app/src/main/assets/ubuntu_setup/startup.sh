#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
R_USER="cmdclick"

add_user(){
	is_user="$(cat "/etc/passwd" | grep "${R_USER}")"
	if [ -z "${is_user}"  ]; then
		apt-get update -y && apt-get upgrade -y
		echo add user
		apt-get install -y sudo
		chown -R root:root /usr/bin/sudo
		chmod -R 4755 /usr/bin/sudo
		#ここでも頭に、usr/binの前にmediaPATHをつけ忘れないように
		chown -R root:root /usr/lib/sudo/sudoers.so
		chmod -R 4755 /usr/lib/sudo/sudoers.so
		chown -R root:root /etc/sudoers
		chown -R root:root /etc/sudoers
		chown root:root /etc/sudo.conf
		chown root:root /usr/bin/sudo
		useradd -ms /bin/bash "${R_USER}" && \
		    usermod -aG sudo "${R_USER}"
		echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers
		echo ''${R_USER}'   ALL=(ALL:ALL) NOPASSWD:ALL'
	fi
}

install_pulseaudio(){
	apt-get -y install aptitude
	touch /var/lib/aptitude/pkgstates
	aptitude install -y pulseaudio \
		|| echo pulse failure
	echo --- dpkg --configure -a
	dpkg --configure -a
	echo --- rm -rf /var/lib/dpkg/info/
	rm -rf /var/lib/dpkg/info/*
	echo update
	apt upgrade -y
	cp -avf \
		"/support/default.pa" \
		"/etc/pulse/default.pa"
}

user_cmd(){
	su - "${R_USER}" <<-EOF
	echo \$USER
	echo --- pulseaudio --start
	# pacmd load-module module-native-protocol-tcp auth-ip-acl=127.0.0.1 auth-anonymous=1
	# pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=auto_null.monitor record=true port=8000
	#export PULSE_SERVER=tcp:127.0.0.1:4712
	# pulseaudio --start --exit-idle-time=-1
	#pulseaudio --start
	# pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=auto_null.monitor record=true port=8000 listen=127.0.0.1
	# export PULSE_SERVER=tcp:127.0.0.1:4712 && pulseaudio --start --disable-shm=1 --exit-idle-time=-1
	# pulseaudio --verbose --start --exit-idle-time=-1 --load="module-native-protocol-tcp auth-ip-acl=127.0.0.1 auth-anonymous=1"
	# export PULSE_SERVER=tcp:127.0.0.1:8080
	# echo --- cat /etc/pulse/default.pa
	# cat /etc/pulse/default.pa > /external/Documents/default.pa
	# sleep 5
	# echo --- espeak sound quality test
	# espeak "sound quality test sound quality test  sound quality test sound quality test sound quality test sound quality test" &
	# espeak_pid=\$?
	# echo \${espeak_pid}
	# sleep 5
	# kill  \${espeak_pid}
	pulseaudio --kill; sleep 0.5 \
	; PULSE_SERVER=  && pulseaudio --start \
	&& pactl load-module module-null-sink sink_name=TCP_output \
	&& pacmd update-sink-proplist TCP_output device.description=TCP_output \
	&& pactl load-module module-simple-protocol-tcp rate=48000 format=s16le channels=2 source=TCP_output.monitor record=true port=10080 listen=0.0.0.0
	echo 00
	for sec in \$(seq 30)
	do
	  echo \${sec}
	  sleep 1
	done
	espeak "sound quality test sound quality test  sound quality test sound quality test sound quality test sound quality test"
	echo --- ps aux grep proot 
	ps aux | grep proot 
	echo --- kill proot
	kill -9 \$(ps aux | grep proot | awk '{print \$2}')
	EOF
}


add_user
echo --

echo \$HOME
echo --
pwd
echo --awk
echo aa | awk '{print \$1}'
echo aa | nawk '{print \$1}'
which --help
echo --- apt-get update and apt-getbupgrade
apt-get update -y && apt-get upgrade -y

apt-get install -y \
	firmware-sof-signed \
	initramfs-tools

install_pulseaudio
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
user_cmd
exit 0
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
