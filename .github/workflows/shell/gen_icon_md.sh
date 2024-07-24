#!/bin/bash


readonly WORKING_DIR_PATH=$(pwd) 
readonly OUTPUT_PATH="${WORKING_DIR_PATH}/md/developer/collection/icons.md"
readonly icon_color_kt="${WORKING_DIR_PATH}/app/src/main/java/com/puutaro/commandclick/common/variable/res/CmdClickIcons.kt"

readonly BLANK_IMAGE_TAG='<img src="" width="40">'
readonly FIELD_LINES=$(\
	cat "${icon_color_kt}" \
	| awk -v BLANK_IMAGE_TAG="${BLANK_IMAGE_TAG}" '
		function trim(con){
			gsub(/^[ \t]+/, "", con)
			gsub(/[ \t]+$/, "", con)
			return con
		}
		{
			$0 = trim($0)
			if($0 !~ /^[A-Z_]+/) next
			$0 = gensub(/[A-Z_]+\((.*)\)[^(]+/, "\\1", "g", $0)
			if(!$0) next
			gsub(/,.*/, "", $0)
			gsub("\x22", "`", $0)
			if(!$0) next
			printf "| %s | %s |\n", $0, BLANK_IMAGE_TAG
		}'\
)

readonly md_path="${OUTPUT_PATH}"
readonly CUR_TABLES=$(\
	cat "${md_path}" \
	| awk '
	function trim(con){
			gsub(/^[ \t]+/, "", con)
			gsub(/[ \t]+$/, "", con)
			return con
		}
	BEGIN{
			is_header = 1
		}{
		if($0 ~ "-----"){
			is_header = 0
			next
		}
		if(is_header) next
		$0 = trim($0)
		gsub(/[ \t]/, " ", $0)
		gsub(/[ ]+/, " ", $0)
		if(!$0) next
		if($0 !~ /^\|/) next
		print $0
	}'\
)
readonly UPDATE_MD_CON=$(\
	cat \
		<(echo "${CUR_TABLES}") \
		<(echo "${FIELD_LINES}") \
	| sort \
	| awk -v BLANK_IMAGE_TAG="${BLANK_IMAGE_TAG}" '
		BEGIN{
			print ""
			print "# Pre reserved icon names table"
			print "\n"
			print "| icon macro name | Description |  "
			print "| --------- | --------- | "
		}
		function trim(con){
			gsub(/^[ \t]+/, "", con)
			gsub(/[ \t]+$/, "", con)
			return con
		}
		function select_icon_con(\
			icon_con1,\
			icon_con2\
		){
			icon_con_list[1] = icon_con1
			icon_con_list[2] = icon_con2
			for(i=1;i <= 2; i++){
				icon_con = trim(icon_con_list[i])
				if(\
					icon_con == BLANK_IMAGE_TAG\
					|| !icon_con \
				){
					continue
				}
				return icon_con
			}
			return BLANK_IMAGE_TAG
		}
		{
			last_index = NR
			$0 = trim($0)
			gsub(/[ \t]/, " ", $0)
			gsub(/[ ]+/, " ", $0)
			if(!$0) next
			if($0 !~ /^\|/) next
			icon_name = $0
      gsub(/^[ \t]+/, "", icon_name)
      gsub(/[ \t]+$/, "", icon_name)
      icon_name = \
        gensub(/^[|] `([a-zA-Z_-]+)` \|.*/, "\\1", "1", $0)
			nr_icon_name_array[NR] = icon_name
			split($0, line_array, "|")
			icon_con = trim(line_array[3])
			icon_name_value_array[icon_name] = \
				select_icon_con(icon_con, icon_name_value_array[icon_name])
			if(\
				duplicate_icon[icon_name]\
			) next
			duplicate_icon[icon_name] = icon_con
		}
		END {
			for(i=1; i <= last_index; i++){
				icon_name = nr_icon_name_array[i]
				next_icon_name = nr_icon_name_array[i+1]
				if(\
					icon_name == next_icon_name\
				) continue
				icon_con = icon_name_value_array[icon_name]
				printf "| `%s` | %s |\n", icon_name, icon_con
			}
			print ""
		}' \
)

echo "${UPDATE_MD_CON}" \
	| tee "${OUTPUT_PATH}"
