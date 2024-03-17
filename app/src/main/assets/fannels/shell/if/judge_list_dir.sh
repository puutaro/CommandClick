set -ue

readonly rev='${rev}'
readonly revVarStr="$(printf "%c%s" '$' '{rev}')"
readonly alterCon='${alterCon}'
readonly alterConStr="$(printf "%c%s" '$' '{alterCon}')"

function echoAlterCon() {
    case "${alterCon}" in
      "${alterConStr}")
        echo "no display"
        ;;
      *)
        echo '${alterCon}'
        ;;
    esac
}

function cat_grep_con(){
  local value_row_num=2
  local grepKey="listDir"
  cat \
    "${tsvPath}" \
    | grep "${grepKey}" \
    | ${b} cut -f ${value_row_num}
}

case "${rev}" in
  "${revVarStr}")
    cat_grep_con \
    | ${b} test "$(cat)" == "${tsvValue}" \
      && echoAlterCon
      ;;
  *)
    cat_grep_con \
        | ${b} test "$(cat)" != "${tsvValue}" \
          && echoAlterCon
    ;;
esac