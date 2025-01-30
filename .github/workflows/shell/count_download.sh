#!/bin/bash

set -ue

readonly WORKING_DIR_PATH=$(pwd)
readonly REPO_NAME_LIST_TSV_PATH="${WORKING_DIR_PATH}/.github/workflows/shell/list/download_count_list.tsv"
readonly REPO_NAME_LIST="$(\
    cat "${REPO_NAME_LIST_TSV_PATH}" \
    | awk '{
        gsub(/^[ \t]*/, "", $0)
        gsub(/[ \t]*$/, "", $0)
        if(!$0) next
        print $0
    }' \
)"
readonly STATISTICS_DIR_PATH="${WORKING_DIR_PATH}/statistics"
mkdir -p "${STATISTICS_DIR_PATH}"
readonly CC_DOWNLOAD_COUNT_DIR_PATH="${STATISTICS_DIR_PATH}/download_count"
mkdir -p "${CC_DOWNLOAD_COUNT_DIR_PATH}"

# res="$(\
#     echo '{
#       "url": "https://api.github.com/repos/puutaro/repbash/releases/164539090",
#       "assets_url": "https://api.github.com/repos/puutaro/repbash/releases/164539090/assets",
#       "upload_url": "https://uploads.github.com/repos/puutaro/repbash/releases/164539090/assets{?name,label}",
#       "html_url": "https://github.com/puutaro/repbash/releases/tag/0.0.1",
#       "id": 164539090,
#       "author": {
#         "login": "github-actions[bot]",
#         "id": 41898282,
#         "node_id": "MDM6Qm90NDE4OTgyODI=",
#         "avatar_url": "https://avatars.githubusercontent.com/in/15368?v=4",
#         "gravatar_id": "",
#         "url": "https://api.github.com/users/github-actions%5Bbot%5D",
#         "html_url": "https://github.com/apps/github-actions",
#         "followers_url": "https://api.github.com/users/github-actions%5Bbot%5D/followers",
#         "following_url": "https://api.github.com/users/github-actions%5Bbot%5D/following{/other_user}",
#         "gists_url": "https://api.github.com/users/github-actions%5Bbot%5D/gists{/gist_id}",
#         "starred_url": "https://api.github.com/users/github-actions%5Bbot%5D/starred{/owner}{/repo}",
#         "subscriptions_url": "https://api.github.com/users/github-actions%5Bbot%5D/subscriptions",
#         "organizations_url": "https://api.github.com/users/github-actions%5Bbot%5D/orgs",
#         "repos_url": "https://api.github.com/users/github-actions%5Bbot%5D/repos",
#         "events_url": "https://api.github.com/users/github-actions%5Bbot%5D/events{/privacy}",
#         "received_events_url": "https://api.github.com/users/github-actions%5Bbot%5D/received_events",
#         "type": "Bot",
#         "site_admin": false
#       },
#       "node_id": "RE_kwDOK87hs84JzqrS",
#       "tag_name": "0.0.1",
#       "target_commitish": "master",
#       "name": "repbash-0.0.1",
#       "draft": false,
#       "prerelease": false,
#       "created_at": "2023-12-23T09:12:07Z",
#       "published_at": "2024-07-09T05:00:44Z",
#       "assets": [
#         {
#           "url": "https://api.github.com/repos/puutaro/repbash/releases/assets/178487173",
#           "id": 178487173,
#           "node_id": "RA_kwDOK87hs84Ko3-F",
#           "name": "repbash-0.0.1-amd64",
#           "label": "",
#           "uploader": {
#             "login": "github-actions[bot]",
#             "id": 41898282,
#             "node_id": "MDM6Qm90NDE4OTgyODI=",
#             "avatar_url": "https://avatars.githubusercontent.com/in/15368?v=4",
#             "gravatar_id": "",
#             "url": "https://api.github.com/users/github-actions%5Bbot%5D",
#             "html_url": "https://github.com/apps/github-actions",
#             "followers_url": "https://api.github.com/users/github-actions%5Bbot%5D/followers",
#             "following_url": "https://api.github.com/users/github-actions%5Bbot%5D/following{/other_user}",
#             "gists_url": "https://api.github.com/users/github-actions%5Bbot%5D/gists{/gist_id}",
#             "starred_url": "https://api.github.com/users/github-actions%5Bbot%5D/starred{/owner}{/repo}",
#             "subscriptions_url": "https://api.github.com/users/github-actions%5Bbot%5D/subscriptions",
#             "organizations_url": "https://api.github.com/users/github-actions%5Bbot%5D/orgs",
#             "repos_url": "https://api.github.com/users/github-actions%5Bbot%5D/repos",
#             "events_url": "https://api.github.com/users/github-actions%5Bbot%5D/events{/privacy}",
#             "received_events_url": "https://api.github.com/users/github-actions%5Bbot%5D/received_events",
#             "type": "Bot",
#             "site_admin": false
#           },
#           "content_type": "application/octet-stream",
#           "state": "uploaded",
#           "size": 7367989,
#           "download_count": 4,
#           "created_at": "2024-07-09T05:00:43Z",
#           "updated_at": "2024-07-09T05:00:44Z",
#           "browser_download_url": "https://github.com/puutaro/repbash/releases/download/0.0.1/repbash-0.0.1-amd64"
#         },
#         {
#           "url": "https://api.github.com/repos/puutaro/repbash/releases/assets/178487174",
#           "id": 178487174,
#           "node_id": "RA_kwDOK87hs84Ko3-G",
#           "name": "repbash-0.0.1-arm64",
#           "label": "",
#           "uploader": {
#             "login": "github-actions[bot]",
#             "id": 41898282,
#             "node_id": "MDM6Qm90NDE4OTgyODI=",
#             "avatar_url": "https://avatars.githubusercontent.com/in/15368?v=4",
#             "gravatar_id": "",
#             "url": "https://api.github.com/users/github-actions%5Bbot%5D",
#             "html_url": "https://github.com/apps/github-actions",
#             "followers_url": "https://api.github.com/users/github-actions%5Bbot%5D/followers",
#             "following_url": "https://api.github.com/users/github-actions%5Bbot%5D/following{/other_user}",
#             "gists_url": "https://api.github.com/users/github-actions%5Bbot%5D/gists{/gist_id}",
#             "starred_url": "https://api.github.com/users/github-actions%5Bbot%5D/starred{/owner}{/repo}",
#             "subscriptions_url": "https://api.github.com/users/github-actions%5Bbot%5D/subscriptions",
#             "organizations_url": "https://api.github.com/users/github-actions%5Bbot%5D/orgs",
#             "repos_url": "https://api.github.com/users/github-actions%5Bbot%5D/repos",
#             "events_url": "https://api.github.com/users/github-actions%5Bbot%5D/events{/privacy}",
#             "received_events_url": "https://api.github.com/users/github-actions%5Bbot%5D/received_events",
#             "type": "Bot",
#             "site_admin": false
#           },
#           "content_type": "application/octet-stream",
#           "state": "uploaded",
#           "size": 7095857,
#           "download_count": 640,
#           "created_at": "2024-07-09T05:00:43Z",
#           "updated_at": "2024-07-09T05:00:43Z",
#           "browser_download_url": "https://github.com/puutaro/repbash/releases/download/0.0.1/repbash-0.0.1-arm64"
#         }
#       ],
#       "tarball_url": "https://api.github.com/repos/puutaro/repbash/tarball/0.0.1",
#       "zipball_url": "https://api.github.com/repos/puutaro/repbash/zipball/0.0.1",
#       "body": "update release"
#     }'\
# )"

exec_count_download(){
    local version="${1}"
    local name="${2}"
    local cc_donwload_count="${3}"
    local update_download_csv_con="${4}"
    local insert_line=$(\
        echo "${version},${name},$(date +%Y-%m-%d),${cc_donwload_count}"\
    )
    local cc_count_csv_con=$(\
        echo "${update_download_csv_con}" \
        | sed '1d' \
    )
    local header="version,name,date,download_count"
    local body=$(cat \
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
    local update_csv_con=$(awk \
        -v header="${header}" \
        -v body="${body}" \
    'BEGIN {
        print header
        print body
    }')
    echo "${update_csv_con}"
}

count_download(){
    local repo_name="${1}"
    local count_url="https://api.github.com/repos/puutaro/${repo_name}/releases/latest"
    local release_res=$(\
        curl \
        --silent \
        --header "Accept: application/vnd.github.v3+json" \
        "${count_url}" \
    )
    local ifs_bk="${IFS}"
    local IFS=$'\n'
    local name_to_donwload_count_list_con="$(\
            echo "${release_res}" \
            | jq -r '.assets[] | [.name, .download_count] | @tsv ' \
        )"
    local name_to_donwload_count_list=(\
        $(echo "${name_to_donwload_count_list_con}")\
    )
    IFS="${ifs_bk}"
    local version="$(\
        echo "${release_res}" \
         | jq -r '.tag_name' \
    )"
    local download_count_csv_path="${CC_DOWNLOAD_COUNT_DIR_PATH}/${repo_name}.csv"
    local update_download_csv_con=$(\
        cat "${download_count_csv_path}" \
        2>/dev/null \
    )
    for name_to_download_count in "${name_to_donwload_count_list[@]}"
    do
        local name="$(\
            echo "${name_to_download_count}"\
            | cut -f 1 \
            | sed 's/\./_/g' \
        )"
        local cc_donwload_count="$(\
            echo "${name_to_download_count}"\
            | cut -f 2 \
        )"
        update_download_csv_con=$(\
            exec_count_download \
                "${version}" \
                "${name}" \
                "${cc_donwload_count}" \
                "${update_download_csv_con}"
        )
        echo update_download_csv_con
        echo "${update_download_csv_con}" \
            | column -t -s ',' \
            |  sed -r '1s/(.*)/\x1b[1;38;5;14m\1\x1b[0m/'
    done
    echo  "${update_download_csv_con}" \
        > "${download_count_csv_path}"
}

for repo_name in ${REPO_NAME_LIST}
do
    case "${repo_name}" in
        "") continue;;
    esac
    count_download \
        "${repo_name}"
done