set -ue

readonly core_title=$(\
  basename ${FANNEL_PATH} \
  | ${b} cut -f1 -d'.'\
)

echo "(${BACKSTACK_COUNT}) ${core_title}: ${EXTRA_TITLE}"