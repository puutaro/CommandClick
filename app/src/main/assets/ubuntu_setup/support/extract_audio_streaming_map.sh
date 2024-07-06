#!/bin/bash

set -ue

get_audio_streaming_map_con(){
	local src_url="${1}" 
	local separator="CMDCLICK_SEPARATOR"
	yt-dlp \
		-x -g -f bestaudio \
		--print "duration=%(duration>%H:%M:%S)s${separator}title=%(title)s"  \
		--skip-download  \
		--socket-timeout 30 \
		--retries 3 \
		"${src_url}"	\
	| awk \
		-v separator="${separator}" \
		-v src_url="${src_url}" \
		'BEGIN{
			map_con_body_part = ""
			map_con_st_url_part = ""
		}{
			gsub(separator, "\n", $0)
			if(NR >= 3) next
			if(NR == 2){
				map_con_st_url_part = "streaming_url="$0
				next
			}
			datetime = gensub(/^duration=([0-9]{2}:[0-9]{2}:[0-9]{2})\n.*/, "\\1", "g", $0)
			toMiliSecCmd = "date --date=\x22"datetime"\x22 \x27+%s\x27"
			toMiliSecCmd | getline milisec
			duration_key_con = "duration="milisec
			map_con_body_part = gensub(/^duration=[0-9]{2}:[0-9]{2}:[0-9]{2}\n/, duration_key_con"\n", "1", $0)
			map_con_body_part = gensub("\n", "\t", "g", map_con_body_part)
		}
		END {
			printf("src_url=%s\t%s\t%s\n", \
				src_url,\
				map_con_body_part,\
				map_con_st_url_part\
			)
		}' 
}


get_audio_streaming_map_con \
	"${1}" \
	2>&1
