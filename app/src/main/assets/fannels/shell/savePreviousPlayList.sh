
if [ ! -f "${savePath}"  ]; then
  touch "${savePath}"
fi
insertCon="$(basename "${playPath}")\t${playPath}"
previousCon=$(cat "${savePath}")
sleep 0.1
updatePriviousCon=$(\
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
