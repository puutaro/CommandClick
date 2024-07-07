#!/bin/bash

pulseaudioSetup(){
	su - "${CMDCLICK_USER}" <<-EOF
	rm -rf \$HOME/.config/pulse
	echo --- pulseaudio --start
	retry_times=5
	ok_count=0
	for i in \$(seq \${retry_times})
	do
		shellCon="\$(curl 127.0.0.1:${UBUNTU_PC_PULSE_SET_SERVER_PORT})"
		case "\${shellCon}" in
			"") ;;
			*)	
				echo "UBUNTU_PC_PULSE_SET_SERVER_PORT ${UBUNTU_PC_PULSE_SET_SERVER_PORT}"
				sh -c "\${shellCon}"
				ok_count=\$((\${ok_count} + 1))
				echo "ok_count: \${ok_count}"
				;;
		esac
		if [ -n "\${shellCon}" ] && [ \${ok_count} -ge 2 ]; then
			break;
		fi
		echo  "[\${i}/\${retry_times}] re-try pulseaudio --start"
		sleep 2
	done
	EOF
}

pulseaudioSetup