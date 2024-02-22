
tsvimport `${savePageUrlConArgsTsvPath}`;

const siteUrl = location.href;
if(!siteUrl) exitZero();

jsToast.short(`Register ok: ${siteUrl}`);
jsToolbar.addUrl_S(siteUrl);
