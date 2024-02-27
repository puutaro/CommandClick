#!/bin/bash


exec repbash "${0}" \
	-a "${1}" \
	-t "\${UBUNTU_ENV_TSV_PATH}"

readonly TITLE="Ubuntu backuping.."
readonly wait_quiz_tsv_path="${SUPPORT_DIR_PATH}/${WAIT_QUIZ_TSV_NAME}"
readonly WAIT_QUIZ_TSV_CON="$(cat "${wait_quiz_tsv_path}")"
readonly WAIT_QUIZ_TSV_CON_LINES="$(echo "${WAIT_QUIZ_TSV_CON}" | wc -l)"


decide_message(){
	for i in $(seq 1 5)
	do
		local rnd_line_num="$((${RANDOM} % ${WAIT_QUIZ_TSV_CON_LINES}))"
		local quiz_con_src=$(\
			echo "${WAIT_QUIZ_TSV_CON}" \
					| sed -n "${rnd_line_num}p"\
		)
		case "${quiz_con_src}" in
			"") 
				sleep 0.1
				continue
				;;
		esac
		echo "${quiz_con_src}"
		break;
	done
}

launch_backuping(){
	local message="${1}"
	local current_state="$(tail -1 "${MONITOR_FILE_PATH}")"
	noti \
		-t launch \
		-cn ${NOTIFICATION_CAHNEL_NUM} \
		--icon-name copy \
		--importance high \
		--title "${TITLE}	${current_state}" \
		--message "${message}" \
		--alert-once \
		--delete "shellPath=${NOTI_EXIT_SHELL_PATH},args=REMOVE_ROOTFS=" \
		--button "label=CANCEL,shellPath=${NOTI_EXIT_SHELL_PATH},args=REMOVE_ROOTFS=remove" \
	>/dev/null 2>&1
}


CURRENT_TIMES=1
ANSWER=""
while kill -0 "${BACKUP_PID}" 2>/dev/null
do
	if [ $((${CURRENT_TIMES} % 2)) -eq 0 ];then
		launch_backuping "${ANSWER}"
	else 
		quiz_con="$(decide_message "${CURRENT_TIMES}")"
		ANSWER="$(echo "${quiz_con}" | cut -f 2)"
		launch_backuping \
			"Q. $(echo "${quiz_con}" | cut -f 1)"
	fi
	sleep 2
	CURRENT_TIMES=$((${CURRENT_TIMES} + 1))
done