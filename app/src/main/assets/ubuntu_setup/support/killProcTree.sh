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

get_help(){
	awk \
	'BEGIN{
		print "### Replace variables getter"
		print ""
		print "* Kill All Process Tree"
		print ""
		print "## Usage"
		print ""
		print "kill_ptree \x22${shell path1 or keyword1}\x22 \x22${shell path2 or keyword2}\x22 .."
		print ""
	}'
	exit 0
}


case "${1:-}" in
	--help|-h) get_help;;
	"") echo "no arg"; exit 0;;
esac

PROCESS_NAME_LIST=""
for(( i=1; i<=$#; i++ )); do  
	PROCESS_NAME_LIST+=$'\t'"${!i}"
done

killpstree
