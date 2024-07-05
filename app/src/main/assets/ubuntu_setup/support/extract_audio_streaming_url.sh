#!/bin/bash

set -ue

get_audio_info(){
	local src_url="${1}" 
	local separator="CMDCLICK_SEPARATOR"
	yt-dlp \
		-x -g -f bestaudio \
		--print "channel=%(channel)s${separator}duration=%(duration>%H:%M:%S)s${separator}title=%(title)s${separator}"  \
		--skip-download  \
		"${src_url}"	\
		| sed  '2s/^/streaming_url=/'  \
		| awk -v separator="${separator}" \
			'{
				gsub(separator, "\n", $0)
				print $0
		}' \
		| awk -v separator="${separator}" \
			'{
				if($0 !~ /^duration=[0-9]{2}:[0-9]{2}:[0-9]{2}/){
					print $0
					next
				}
				datetime = gensub(/duration=([0-9]{2}:[0-9]{2}:[0-9]{2})/, "\\1", "1", $0)
				toMiliSecCmd = "date --date=\x22"datetime"\x22 \x27+%s\x27"
				toMiliSecCmd | getline milisec
				print "duration="milisec
		}' | awk \
			-v con="$(cat)" \
			-v src_url="${src_url}" \
			-v separator="${separator}" \
		'BEGIN {
			gsub(/\n/, separator, con)
			map_con = sprintf("src_url=%s%s%s", src_url, separator, con)
			print "--"
			print gensub(separator, "\t", "g", map_con)
		}'
}

get_audio_info \
	"${1}" \
	2>&1
