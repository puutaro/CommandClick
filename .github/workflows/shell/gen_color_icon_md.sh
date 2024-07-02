#!/bin/bash


readonly WORKING_DIR_PATH=$(pwd) 
readonly src_path="${WORKING_DIR_PATH}/app/src/main/java/com/puutaro/commandclick/common/variable/res/CmdClickColor.kt"
readonly FIELD_LINES=$(\
	cat "${src_path}" \
	| awk '
		{
			$0 = gensub(/^[ \t]*(.*)[ \t]*$/, "\\1", "g", $0)
			if($0 !~ /^[A-Z_]+/) next
			$0 = gensub(/[A-Z_]+\((.*)\)[^(]+/, "\\1", "g", $0)
			if(!$0) next
			len_enum_value_list = split($0 , enum_value_list, ",")
			for(i=1; i<=len_enum_value_list; i++){
				el = enum_value_list[i]
				gsub("\x22", "`", el)
				enum_value_list[i] = gensub(/^[ \t]*(.*)[ \t]*$/, "\\1", "g", el)
			}
			color_macro_index = 1
			color_res_index = 2
			color_macro = enum_value_list[color_macro_index]
			color_res = enum_value_list[color_res_index]
			printf "%s\t%s\n", color_macro, color_res
		}'\
)
readonly COLOR_MACRO_LINES=$(echo "${FIELD_LINES}" | cut -f1)
readonly color_res_lines=$(echo "${FIELD_LINES}" | cut -f2)
readonly color_xml="${WORKING_DIR_PATH}/app/src/main/res/values/colors.xml"
readonly side_length=50
readonly COLOR_LINKS=$(\
	echo "${color_res_lines}" | awk '{
		color_res_name = gensub(/.*\.([a-z]+)/, "\\1", "1", $0)
		printf "cat \x22'${color_xml}'\x22 | grep \x27\x22%s\x22\x27 | tail -1\n", color_res_name
	}' \
	 | bash \
	| awk \
		-v side_length="${side_length}" '
		function extract_code(code_src){
			end_index = split(code_src, code_char_list, "")
			start_index = end_index - 5
			code_con = ""
			for(i=start_index;i<=end_index;i++){
				code_con = code_con""code_char_list[i]
			}
			return code_con
		}
		{
			color_code_src = gensub(/<[^>]+>([a-zA-Z0-9#]+)<\/[^>]+>/, "\\1", "1", $0)
			color_code_src = gensub(/^[ \t]*(.*)[ \t]*$/, "\\1", "g", color_code_src)
			color_code = extract_code(color_code_src)
			printf "<img src=\x22https://placehold.co/%dx%d/%s/%s.png\x22>\n", 
				side_length, side_length, color_code, color_code
		}'\
)

readonly MD_CON=$(\
	paste -d'\t' \
		<(echo "${COLOR_MACRO_LINES}") \
		<(echo "${COLOR_LINKS}") \
	| awk -F '\t' '
	BEGIN{
		print ""
		print "# Pre reserved color names table"
		print "\n"
		print "| color macro name | Description                                                         | "
		print "|-----------------|---------------------------------------------------------------------| "
	}
	{
		printf "| %s | %s |\n", $1, $2
	}
	END{
		print ""
	}'\
)

readonly outputPath="${WORKING_DIR_PATH}/md/developer/collection/color.md"
echo "${MD_CON}" \
	| tee "${outputPath}"
