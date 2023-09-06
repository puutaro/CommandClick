#! /bin/bash

if [[ -z "${INITIAL_USERNAME}" ]]; then
  INITIAL_USERNAME="user"
fi

if [[ -z "${INITIAL_VNC_PASSWORD}" ]]; then
  INITIAL_VNC_PASSWORD="userland"
fi

if [ ! -f /home/$INITIAL_USERNAME/.vnc/passwd ]; then

prog=/usr/bin/vncpasswd

/usr/bin/expect <<EOF
spawn "$prog"
expect "Password:"
send "$INITIAL_VNC_PASSWORD\r"
expect "Verify:"
send "$INITIAL_VNC_PASSWORD\r"
expect "(y/n)?"
send "n\r"
expect eof
exit
EOF

fi

if [[ -z "${DIMENSIONS}" ]]; then
	DIMENSIONS="1024x768"
fi

vncrc_line="\$geometry = \"${DIMENSIONS}\";"
echo $vncrc_line > /home/$INITIAL_USERNAME/.vncrc

if [[ -z "${VNC_DISPLAY}" ]]; then
  VNC_DISPLAY="51"
fi

rm /tmp/.X${VNC_DISPLAY}-lock
rm /tmp/.X11-unix/X${VNC_DISPLAY}
tightvncserver -kill :${VNC_DISPLAY}
tightvncserver :${VNC_DISPLAY}

while [ ! -f /home/$INITIAL_USERNAME/.vnc/localhost:${VNC_DISPLAY}.pid ]
do
  sleep 1
done
cd ~
DISPLAY=localhost:${VNC_DISPLAY} xterm -geometry 80x24+0+0 -e /bin/bash --login &
