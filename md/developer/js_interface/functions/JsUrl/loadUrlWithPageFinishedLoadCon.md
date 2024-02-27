# loadUrlWithPageFinishedLoadCon

Table
-----------------

* [Result](#result)
* [Argument](#argument)


## Result

Load url by webview


```js.js
jsUrl.loadUrlWithPageFinishedLoadCon(
    urlCon: String,
    pageFinishedLoadCon: String,
    beforeDelayMiliSec: String
)
```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| urlCon | string | url load js contents: ex) `javascript:~` |
| pageFinishedLoadCon | string |  js contents when page loading finished: ex) `javascript:~` |
| beforeDelayMiliSec | (long) string |  delay long time before `pageFinishedLoadCon` |



ex1) 

```js.js

const terminalUrl =
  http://127.0.0.1:18080/?hostname=127.0.0.1&port=10022&username=cmdclick&password=Y21kY2xpY2s=&command=script%20-qf%20script.log`;
const loadJsCon =
  jsUrl.makeJsUrlFromCon(`jsUrl.loadUrl("${terminalUrl}")`);
const pageFinishedLoadCon =
  jsUrl.makeJsUrl(`${sshTerminalNoArgsJs}`);
jsUrl.loadUrlWithPageFinishedLoadCon(
    loadJsCon,
    pageFinishedLoadCon,
    1000,
);
```

