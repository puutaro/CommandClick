# Javascript TroubleShooting

Table
-----------------
* [Cannot excute js script](#cannot-excute-js-script)
* [Js while roop cause crush](#js-while-roop-cause-crush)

## Cannot excute js script <a id="jquery-ui_css"></a>

You confirm how script step semicolon(`;`) exist except for function argument.  
	- Becuase javaxcript file convert one linear script string, as it, javascript:(function() { `${js contents}` })(); and webvoew.loadUrl().  


## Js while roop cause crush

- Add bellow code to the roop.  

```js.js
if(
  jsStop.how().includes("true")
) throw new Error('exit');
```  


- Optinaly may replace delay function with
  
```js.js
jsUtil.sleep($milisecond);
```

(The Roop crush is occur by memory leak.)
