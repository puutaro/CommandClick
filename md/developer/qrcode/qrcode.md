# qrcode



# Format table

| type | format |  
| -------- | -------------- |  
| WIFI | `WIFI:T:WPA;S:<name>;P:<password>;;` <br> or `wifi:T:WPA;S:<name>;P:<password>;;` |  
| SMS | `SMSTO:<phone>:<message>` <br> or `smsto:<phone>:<message>` |  
| GMAIL | `MAILTO:<email>?subject=<subject>&body=<body>` <br> or `mailto:<email>?subject=<subject>&body=<body>` |  
| TEL | `TEL:<phone number>` <br> or `tel:<phone number>`  |  
| GOOGLE CALENDAR | `gcalendar:biginTime=<yyyy-MM-ddThh:mm>;endTime=<yyyy-MM-ddThh:mm>;title=<title str>;description=<description message>;android.intent.extra.EMAIL=<email ad>;eventLocation=<location name>` <br> ex)<br> `gcalendar:biginTime=2023-11-27T10:41;endTime=2023-11-27T11:41;title=title1;description=description1;android.intent.extra.EMAIL==https://mail.google.com/mail/u/0/#inbox;eventLocation=Net` |  
| URL | url string |  
| GIT CLONE1 | `onGit:prefix=https://raw.githubusercontent.com/puutaro/commandclick-repository/master;listPath=manage/fannels/list/fannels.txt;dirPath=fannel;name=<fannel name>` <br> ex)<br> onGit:prefix=https://raw.githubusercontent.com/puutaro/commandclick-repository/master;listPath=manage/fannels/list/fannels.txt;dirPath=fannel;name=newsSpeecher <br> -> [detail](https://github.com/puutaro/commandclick-repository?tab=readme-ov-file#procedure-1) |  
| GIT CLONE2 | `onGit:prefix=<your git repo url>;name=<fannel name>` <br> [detail](https://github.com/puutaro/commandclick-repository?tab=readme-ov-file#procedure-2) |  
| FILE DOWNLOAD | `cpFile:address=<{ipv4add}:{port}>;path=<path>((onMoveCurrentDir=on)`(;currentAppDirPathForServer=<current app dir for server>) |  
| SCP_DOWNLOAD | `scpDir:dirPath=<dir path>;ipv4add=<ipv4add>;port=<port>;userName=<user name>;password=<password>` |  
| JAVASCRIPT | `javascript:<bookmarklet contents>` <br> or `jsDesc:<description message> &&& `javascript:<bookmarklet contents>` |  
