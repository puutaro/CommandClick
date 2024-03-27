
function echoPlayTitle(){
  local playTitle='${playTitle}'
  local playTitleStr="$(printf "%c%s" '$' '{playTitle}')"
  case "${playTitle}" in
    "${playTitleStr}")
      basename "${playPath}"
        ;;
    *)
      echo "${playTitle}"
      ;;
  esac
}

if [ ! -f "${savePath}"  ]; then
  touch "${savePath}"
fi

readonly insertCon="$(echoPlayTitle)\t${playPath}"
readonly previousCon=$(cat "${savePath}")
sleep 0.1
readonly updatePriviousCon=$(\
  echo -e "${insertCon}\n${previousCon}" \
  | ${b} sort \
  | ${b} uniq \
)
case "${updatePriviousCon}" in
  "${previousCon}")
      exit 0
      ;;
esac
echo "${updatePriviousCon}" \
  > "${savePath}"
