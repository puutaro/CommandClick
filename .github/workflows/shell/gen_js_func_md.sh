#!/bin/bash


readonly WORKING_DIR_PATH=$(pwd) 
readonly IF_DIR_PATH="${WORKING_DIR_PATH}/app/src/main/java/com/puutaro/commandclick/fragment_lib/terminal_fragment/js_interface"
readonly JS_IF_MD_DIR="${WORKING_DIR_PATH}/md/developer/js_interface"
readonly func_dir_name="functions"
readonly FUNC_DIR_PATH="${JS_IF_MD_DIR}/${func_dir_name}"
hand_gen_feature_dir(){
	find "${IF_DIR_PATH}"\
		 -mindepth 1 -maxdepth 1 -type d \
	| sed -r 's/^(.*)/basename "\1"/' \
	| sh\
	| awk '{
		if(!$0) next
		if($0 == "lib") next
		printf "mkdir \x22'${DETAILS_DIR_PATH}'/%s\x22 2>/dev/null\n", $0
	}' | sh
}
hand_gen_feature_dir
readonly details_dir_name="details"
readonly DETAILS_DIR_PATH="${JS_IF_MD_DIR}/${details_dir_name}"
auto_gen_feature_dir(){
	rm -rf "${FUNC_DIR_PATH}"
	mkdir "${FUNC_DIR_PATH}"
	find "${IF_DIR_PATH}" \
		-mindepth 1 -maxdepth 1 -type d \
	| sed -r 's/^(.*)/basename "\1"/' \
	| sh\
	| awk '{
		if(!$0) next
		if($0 == "lib") next
		printf "mkdir \x22'${FUNC_DIR_PATH}'/%s\x22 2>/dev/null\n", $0, $0
	}' | sh
}
auto_gen_feature_dir
old_ifs="${IFS}"
IFS=$'\n'
if_file_list=($(find "${IF_DIR_PATH}" -type f | grep -v "/lib/"))
IFS="${old_ifs}"
exec_gen_md(){
	local newline="NEW_LINE"
	local src_path="${1}"
	awk \
		-v src_path="${src_path}" \
		-v js_if_con="$(\
			cat "${src_path}" \
			| sed 's/\\n/'${newline}'/g'\
		)" \
		-v class_name=$(\
			basename "${src_path}" \
			| sed 's/\..*$//'\
		) \
		-v classifi_func_dir_name=$(echo "${src_path}" \
			| sed 's/'${IF_DIR_PATH//\//\\\/}'//g' \
			| dirname "$(cat)" \
			| sed 's/^\///')\
		-v FUNC_DIR_PATH="${FUNC_DIR_PATH}" \
		-v DETAILS_DIR_PATH="${DETAILS_DIR_PATH}" '
		function convert_git_url(file_path){
			relative_path=""
			prefix = "github.com/puutaro/CommandClick/blob/master"
			full_path_contain_str="/CommandClick/"
			if(file_path !~ full_path_contain_str){
				relative_path = file_path
			} else {
				cut_regex = ".*"full_path_contain_str
				gsub(cut_regex, "", file_path)
				relative_path = file_path
			}
			base_uri = sprintf("%s/%s", prefix, relative_path)
			gsub(/[/]+/, "/", base_uri)
			md_url = "https://"base_uri
			return md_url
		}
		function make_class_name(class_name){
			return \
				tolower(substr(class_name,1,1)) \
				substr(class_name, 2, length(class_name))
		}
		function make_func_name(func_con, lower_class_name){
			func_end_index = index(func_con, "(") - 1
			func_name = substr(func_con, 0, func_end_index)
			gsub(/[ \t]+/, "", func_name)
			if(\
				func_name ~ /^\/\//\
			) return ""
			return func_name
		}
		function make_args_con(func_con){
			bracket_start_index = index(func_con, "(")
			bracket_end_index = index(func_con, ")")
			len_sub_str = bracket_end_index - bracket_start_index + 1
			args_con = substr(func_con, bracket_start_index, len_sub_str)
			gsub(/[()]/, "", args_con)
			gsub(/,$/, "", args_con)

			len_args_con_list = split(args_con, args_con_list, ",")
			args_raw_con = ""
			for(j=1; j <= len_args_con_list; j++){
				arg_name = args_con_list[j]
				gsub(/[: \t\n]/, "", arg_name)
				if(\
					arg_name ~ /^\/\//\
					|| !arg_name \
				) {
					continue
				}
				if(!args_raw_con){
					args_raw_con = sprintf("&%s=", arg_name)
					continue
				}
				args_line = sprintf("&%s=", arg_name)
				args_raw_con = sprintf("%s\n%s", args_raw_con, args_line)
			}

			return args_raw_con
		}
		function make_return_con(func_con, func_name){
			return_con = gensub(".*return ([a-zA-Z0-9_]+).*", "\\1", "1", func_con)
			if(\
				return_con !~ /\n/\
				&& return_con \
			){
				return return_con
			}
			func_name = toupper(substr(func_name,1,1)) \
				substr(func_name, 2, length(func_name))
			return "run"func_name
		}
		function make_description_con(func_con){
			desc_start_index = index(func_con, "/*")
			if(\
				desc_start_index <= 0\
			) return ""
			len_func_con_list = split(func_con, func_con_list, "\n")
			desc_blank = ""
			for (j=1; j<= len_func_con_list;j++){
				line = func_con_list[j]
				if(line !~ /\/\*/) continue
				if(line !~ /^[ \t]*\/\*/) continue
				gsub(/\/\*.*/, "", line)
				desc_blank = line
				break
			}
			desc_end_index = index(func_con, "*/")
			if(\
				desc_end_index <= 0\
			) return "" 
			if( desc_start_index > desc_end_index){
				return ""
			}
			len_sub_str = desc_end_index - desc_start_index + 2
			desc_con = substr(func_con, desc_start_index, len_sub_str)
			gsub(/^\/\*\n/, "", desc_con)
			gsub(/\*\/$/, "", desc_con)
			remove_desc_blank = "^"desc_blank
			gsub(remove_desc_blank, "", desc_con)
			remove_desc_blank = "\n"desc_blank
			gsub(remove_desc_blank, "\n", desc_con)
			return desc_con
		}
		function make_src_perm_link(\
			len_js_if_con_list,\
			js_if_con_list,\
			func_name\
		){
			len_js_if_con_list_by_new_line = \
				split(js_if_con, js_if_con_list_by_new_line, "\n")
			func_regex = "fun "func_name"[ \t]*[(]"
			line_num = 0
			for(j=1; j<=len_js_if_con_list_by_new_line;j++){
				line = js_if_con_list_by_new_line[j]
				if(\
					line !~ func_regex \
				) {
					continue
				}
				line_num = j
				break
			}
			if(line_num == 0) return ""
			return sprintf("%s#L%s",\
				convert_git_url(src_path),\
				line_num\
			)
		}
		function make_detail_url(detail_dir_path, func_name_md){
			func_detail_md_path = sprintf("%s/%s/%s/%s", \
						DETAILS_DIR_PATH,\
						classifi_func_dir_name,\
						gensub(/([a-zA-Z0-9_]+).*/, "\\1", "1", class_name),\
						func_name_md\
					)

			exist_cmd = sprintf("ls \x22%s\x22 2>/dev/null", func_detail_md_path)
			is_exist = system(exist_cmd)
			close(exist_cmd)
			if(is_exist != 0) return ""
			return convert_git_url(func_detail_md_path)
		}
		BEGIN {
			lower_class_name = make_class_name(class_name)
			if(!lower_class_name) exit
			detail_dir_path = sprintf("%s/%s/%s", \
						DETAILS_DIR_PATH,
						classifi_func_dir_name,
						gensub(/([a-zA-Z0-9_]+).*/, "\\1", "1", class_name)\
					)
			output_dir_path = sprintf("%s/%s/%s", \
								FUNC_DIR_PATH, \
								classifi_func_dir_name,
								gensub(/([a-zA-Z0-9_]+).*/, "\\1", "1", class_name)\
							)
			gsub(/[/]+/, "/", output_dir_path)
			print "# start dir setup"
			rmdir_cmd = sprintf("rm -rf \x22%s\x22 2>/dev/null", output_dir_path)
			system(rmdir_cmd)
			close(rmdir_cmd)
			mkdir_cmd = sprintf("mkdir \x22%s\x22 2>/dev/null", output_dir_path)
			system(mkdir_cmd)
			close(mkdir_cmd)
			print "# ok dir setup"
			len_js_if_con_list = split(js_if_con, js_if_con_list, "@JavascriptInterface")
			for(i=2;i<=len_js_if_con_list;i++){
				con = js_if_con_list[i]
				split(con, func_list, "fun ")
				js_if_con_list[i] = func_list[2]
			}
			for(i=2;i<=len_js_if_con_list;i++){
				func_con = js_if_con_list[i]
				func_name = make_func_name(func_con)
				lower_class_func_name =  sprintf("%s.%s", \
								lower_class_name, \
								substr(func_con, 0, func_end_index)\
						)
				args_raw_con = make_args_con(func_con)
				return_con = make_return_con(func_con, func_name)
				func_difinition_con = sprintf("%s\n", \
									return_con)
				func_args_con = args_raw_con
				gsub("&", "\t${", func_args_con)
				gsub("=", "},", func_args_con)
				func_difinition_con = sprintf("function %s(\n%s\n) -> %s\n", \
									lower_class_func_name, \
									func_args_con,\
									return_con\
								)
				run_desc = ""
				if(return_con ~ /^run/){
					run_desc = "- The `run` prefix annotation is a process annotation"
				}
				md_con = sprintf("## Definition\n\n```js.js\n%s```\n\n%s",  
									func_difinition_con,\
									run_desc\
								)
				js_ac_con = sprintf("var=%s\n", \
									return_con)
				js_ac_con = sprintf("%s\t?func=%s\n", \
									js_ac_con, \
									lower_class_func_name \
								)	
				js_ac_args_con = ""
				if(args_raw_con){
					insert_js_ac_args_con = \
						gensub(\
							/&([a-zA-Z0-9]+?([A-Z][a-z]+))=/,\
							"\t\t\\&\\1=${\\2}",\
							"g",\
							args_raw_con\
						)
					js_ac_args_con = sprintf("\t?args=\n%s", 
							insert_js_ac_args_con)
				}
				js_ac_con = sprintf("%s%s", 
									js_ac_con,
									js_ac_args_con)
				desc_con = make_description_con(func_con)
				func_name_md = func_name".md"
				output_md_path = sprintf("%s/%s", output_dir_path, func_name_md)
				js_ac_difinition = sprintf("```js.js\n%s\n```", js_ac_con)
				js_ac_link = "- [js action](#) is annotation-oriented language based on javascript in `CommandClick`"
				run_desc = ""
				if(return_con ~ /^run/){
					run_desc = "- The `run` prefix definition on `var` is a process annotation, not a variable definition"
				}
				md_con = sprintf("%s\n## Definition by js action\n\n%s\n\n%s\n\n%s",  
									md_con,\
									js_ac_difinition,\
									js_ac_link, \
									run_desc\
								)
				if(desc_con){
					md_con = sprintf("%s## Description\n\n%s",  
									md_con, \
									desc_con\
								)
				}
				src_perm_link = make_src_perm_link(\
					js_if_con, \
					js_if_con_list,\
					func_name\
				)
				if(src_perm_link){
					md_con = sprintf("%s\n\n## Src\n\n-> [%s](%s)\n\n",  
									md_con, \
									lower_class_func_name,
									src_perm_link\
								)
				}
				detail_url =  make_detail_url(detail_dir_path, func_name_md)
				if(detail_url){
					md_con = sprintf("%s## Detail\n\n-> [%s](%s)",  
									md_con, \
									lower_class_func_name,
									detail_url\
								)
				}
				md_con = sprintf("# %s\n\n%s", 
									lower_class_func_name, 
									md_con\
								)
				printf "# ok %s\n", output_md_path
				print md_con > output_md_path
			}
	}'
}
for if_file in ${if_file_list[@]}
do
	exec_gen_md "${if_file}"
done
