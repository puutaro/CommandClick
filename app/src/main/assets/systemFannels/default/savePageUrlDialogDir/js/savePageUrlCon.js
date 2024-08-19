
const siteUrl = location.href;
if(!siteUrl) exitZero();

jsToast.short(`Register ok: ${siteUrl}`);
const title = document.title;
jsToolbar.addUrl_S(
    title,
    siteUrl
);
