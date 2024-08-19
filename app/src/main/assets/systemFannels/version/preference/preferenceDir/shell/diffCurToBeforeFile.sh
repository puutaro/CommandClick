
src_file_path="${value}"
src_file_name="$(basename "${src_file_path}")"
dest_file_path="${preferenceTempDirPath}/${src_file_name}"
${b} diff \
  "${src_file_path}" \
  "${dest_file_path}" \
  2>/dev/null

