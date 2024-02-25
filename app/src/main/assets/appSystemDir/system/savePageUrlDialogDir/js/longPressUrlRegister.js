


const LONG_PRESS_LINK_URL = "${LONG_PRESS_LINK_URL}";

end_judge();
jsUtil.copyToClipboard(LONG_PRESS_LINK_URL, 10);
jsToast.short(`Register ok, ${LONG_PRESS_LINK_URL}`);
jsToolbar.addUrl(LONG_PRESS_LINK_URL);

function end_judge(){
    if(
        !LONG_PRESS_LINK_URL.includes("/")
    ) exitZero();
};
