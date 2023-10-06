
# JsUbuntu

Manage functinos about ubuntu 


```js.js

- jsUbuntu.runCmd(
    executeShellPath:String,
    tabSepalateArguments: String = String(),
    timeoutMiliSec: Int,
)
	-> output string


- jsUbuntu.runByBackground(
    backgroundShellPath: String,
    argsTabSepaStr:String,
    monitorNum: Int,
)
  - execute shell command as backgrond service


- jsUbuntu.boot()
  - boot ubuntu service


jsUbuntu.bootOnExec(
        execJavascriptCode: String,
        delayMiliTime: Int
    )
	 -> execute javascript on boot ubuntu

```

