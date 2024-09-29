#!/bin/bash


readonly WORKING_DIR_PATH=$(pwd)
readonly README_PATH="${WORKING_DIR_PATH}/README.md"
readonly STATISTICS_DIR_PATH="${WORKING_DIR_PATH}/statistics"
mkdir -p "${STATISTICS_DIR_PATH}"
readonly WORKFLOW_LIST_DIR_PATH="${WORKING_DIR_PATH}/.github/workflows/shell/list"
readonly THANKS_IMAGE_LIST_PATH="${WORKFLOW_LIST_DIR_PATH}/thanks_image_list.tsv"

readonly THANKS_MESSAGE="Thanks for watching by end"
readonly thanks_image_urls=$(\
    cat "${THANKS_IMAGE_LIST_PATH}" | awk '{
        gsub(/^[ \t]*/, "", $0)
        gsub(/[ \t]*$/, "", $0)
        if(!$0) next
        print $0
    }' | shuf \
    | cut -f 2 \
    | head -4 \
    | awk -v THANKS_MESSAGE="${THANKS_MESSAGE}" '\
        BEGIN{
            print ""
            print THANKS_MESSAGE
            print "--------"
            print ""
            print ""
        }{
            printf "<img src=\x22%s\x22 width=\x2245%\x22>\n", $0
        }'
)
readonly readme_without_thanks_src=$(\
    cat "${README_PATH}" \
    | sed "/${THANKS_MESSAGE}/q"\
)
readonly readme_without_thanks="$(\
    awk \
        -v readme_without_thanks_src="${readme_without_thanks_src}" \
    'BEGIN{
        print gensub(/[\n]+$/, "", "g", readme_without_thanks_src)

    }'
)"
sleep 0.2
cat \
    <(echo "${readme_without_thanks}") \
    <(echo "${thanks_image_urls}") \
    <(echo "") \
> "${README_PATH}"
