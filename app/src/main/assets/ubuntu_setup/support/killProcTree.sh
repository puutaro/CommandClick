#!/bin/bash


execKillpstree(){
	case "$1" in
	 "") return;;
	esac
    local children=$(ps --ppid $1 --no-heading | awk '{ print $1 }')
    for child in $children
    do
        execKillpstree $child
    done
    kill -9 $1
}

killpstree(){
	local grep_process_id=$(\
		echo "${PROCESS_NAME_LIST}" \
			| awk '{
				sub(/^\t/, "", $0)
				gsub("\t", "\x22 -e \x22", $0)
				print "ps aux | grep -e \x22"$0"\x22 | grep -v grep | grep -v eval | awk \x27{print $2}\x27"
			}' \
	)
	echo "${grep_process_id}"
	local parent_process_list=$(\
		eval "${grep_process_id}"
	)
	for parent in $parent_process_list
	do
	    execKillpstree $parent
	 done
}


PROCESS_NAME_LIST=""
for(( i=1; i<=$#; i++ )); do  
	PROCESS_NAME_LIST+=$'\t'"${!i}"
done

killpstree
