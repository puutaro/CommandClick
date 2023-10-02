#!/bin/bash


killId=$(\
	ps aux \
	| grep -v "${PACKAGE_NAME}" \
	| grep \
		-e "pulseaudio"  \
	| awk '{print $2}' \
)

case "${killId}" in
	"") 
		exit 0
		;;
esac

kill ${killId}