#!/bin/bash



readonly WORKING_DIR_PATH=$(pwd)
readonly STATISTICS_DIR_PATH="${WORKING_DIR_PATH}/statistics"
mkdir -p "${STATISTICS_DIR_PATH}"
readonly CC_DOWNLOAD_COUNT_CSV_PATH="${STATISTICS_DIR_PATH}/download_count.csv"

# readonly release_res="$(
#     echo '{
#   "url": "https://api.github.com/repos/puutaro/CommandClick/releases/175259467",
#   "assets_url": "https://api.github.com/repos/puutaro/CommandClick/releases/175259467/assets",
#   "upload_url": "https://uploads.github.com/repos/puutaro/CommandClick/releases/175259467/assets{?name,label}",
#   "html_url": "https://github.com/puutaro/CommandClick/releases/tag/v1.4.1",
#   "id": 175259467,
#   "author": {
#     "login": "puutaro",
#     "id": 55217593,
#     "node_id": "MDQ6VXNlcjU1MjE3NTkz",
#     "avatar_url": "https://avatars.githubusercontent.com/u/55217593?v=4",
#     "gravatar_id": "",
#     "url": "https://api.github.com/users/puutaro",
#     "html_url": "https://github.com/puutaro",
#     "followers_url": "https://api.github.com/users/puutaro/followers",
#     "following_url": "https://api.github.com/users/puutaro/following{/other_user}",
#     "gists_url": "https://api.github.com/users/puutaro/gists{/gist_id}",
#     "starred_url": "https://api.github.com/users/puutaro/starred{/owner}{/repo}",
#     "subscriptions_url": "https://api.github.com/users/puutaro/subscriptions",
#     "organizations_url": "https://api.github.com/users/puutaro/orgs",
#     "repos_url": "https://api.github.com/users/puutaro/repos",
#     "events_url": "https://api.github.com/users/puutaro/events{/privacy}",
#     "received_events_url": "https://api.github.com/users/puutaro/received_events",
#     "type": "User",
#     "site_admin": false
#   },
#   "node_id": "RE_kwDOI5NAFs4Kcj9L",
#   "tag_name": "v1.4.1",
#   "target_commitish": "master",
#   "name": "[Big update] Adopt historical UI",
#   "draft": false,
#   "prerelease": false,
#   "created_at": "2024-09-16T14:30:09Z",
#   "published_at": "2024-09-16T14:47:16Z",
#   "assets": [
#     {
#       "url": "https://api.github.com/repos/puutaro/CommandClick/releases/assets/192980858",
#       "id": 192980858,
#       "node_id": "RA_kwDOI5NAFs4LgKd6",
#       "name": "CommandClick-1.4.1-release.apk",
#       "label": null,
#       "uploader": {
#         "login": "puutaro",
#         "id": 55217593,
#         "node_id": "MDQ6VXNlcjU1MjE3NTkz",
#         "avatar_url": "https://avatars.githubusercontent.com/u/55217593?v=4",
#         "gravatar_id": "",
#         "url": "https://api.github.com/users/puutaro",
#         "html_url": "https://github.com/puutaro",
#         "followers_url": "https://api.github.com/users/puutaro/followers",
#         "following_url": "https://api.github.com/users/puutaro/following{/other_user}",
#         "gists_url": "https://api.github.com/users/puutaro/gists{/gist_id}",
#         "starred_url": "https://api.github.com/users/puutaro/starred{/owner}{/repo}",
#         "subscriptions_url": "https://api.github.com/users/puutaro/subscriptions",
#         "organizations_url": "https://api.github.com/users/puutaro/orgs",
#         "repos_url": "https://api.github.com/users/puutaro/repos",
#         "events_url": "https://api.github.com/users/puutaro/events{/privacy}",
#         "received_events_url": "https://api.github.com/users/puutaro/received_events",
#         "type": "User",
#         "site_admin": false
#       },
#       "content_type": "application/vnd.android.package-archive",
#       "state": "uploaded",
#       "size": 21654944,
#       "download_count": 30,
#       "created_at": "2024-09-16T17:12:18Z",
#       "updated_at": "2024-09-16T17:12:25Z",
#       "browser_download_url": "https://github.com/puutaro/CommandClick/releases/download/v1.4.1/CommandClick-1.4.1-release.apk"
#     }
#   ],
#   "tarball_url": "https://api.github.com/repos/puutaro/CommandClick/tarball/v1.4.1",
#   "zipball_url": "https://api.github.com/repos/puutaro/CommandClick/zipball/v1.4.1",
#   "body": "[Changes]\r\n- Url history by gif\r\n- Fannel center by gif\r\n- UI by URL history captrue\r\n- Extra menu by art\r\n- Text selection system by URL catpture\r\n- Update search system \r\n- Integrate fannel install system to fannel center\r\n- Remove app manger , js manger\r\n- Remove scroll regiter menu, etx..\r\n- Integrate fannel management to fannel center\r\n- Apply original gif art to multiple functions\r\n- Adopt pin system\r\n"
# }')"
readonly release_res=$(\
    curl \
    --silent \
    --header "Accept: application/vnd.github.v3+json" \
    https://api.github.com/repos/puutaro/CommandClick/releases/latest \
)
readonly cc_donwload_count="$(\
    echo "${release_res}" \
    | jq -r '.assets[] |.download_count' \
)"
readonly version="$(\
    echo "${release_res}" \
    | jq -r '.tag_name' \
)"
readonly insert_line=$(echo "${version},$(date +%Y-%m-%d),${cc_donwload_count}")
readonly cc_count_csv_con=$(\
    cat "${CC_DOWNLOAD_COUNT_CSV_PATH}"\
    | sed '1d' \
    2>/dev/null \
)
readonly header="version,date,download_count"
readonly body=$(cat \
    <(echo "${cc_count_csv_con}") \
    <(echo "${insert_line}") \
    | awk '{
        gsub(/^[ \t]*/, "", $0)
        gsub(/[ \t]*$/, "", $0)
        if(!$0) next
        print $0
    }'\
    | sort -r \
    | uniq \
)
readonly update_csv_con=$(awk \
    -v header="${header}" \
    -v body="${body}" \
'BEGIN {
    print header
    print body
}')
echo "${update_csv_con}" \
    > "${CC_DOWNLOAD_COUNT_CSV_PATH}"

echo "${CC_DOWNLOAD_COUNT_CSV_PATH}"
cat "${CC_DOWNLOAD_COUNT_CSV_PATH}" \
| column -t -s ',' \
|  sed -r '1s/(.*)/\x1b[1;38;5;14m\1\x1b[0m/'