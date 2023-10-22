# launchApp

Table
-----------------

* [Result](#overview)
* [Argument](#argument)


## Result

send app intent


```js.js

jsIntent.launchApp(
	action: String,
	uriString: String,
	extraString: tabSepalatedString,
	extraInt: tabSepalatedString,
	extraLong: tabSepalatedString,
	extraFloat: tabSepalatedString
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| action | string | action name |
| uriString | string | uri string |
| extraString | string | string sepalated by tab |
| extraInt | string | int string sepalated by tab |
| extraLong | string | long string sepalated by tab |
| extraFloat | string | float string sepalated by tab |


ex1)

```js.js
const extraString = "title=" + title + 
		"\tdescription=" + description + 
		"\teventLocation=" + eventLocation +
		"\tandroid.intent.extra.EMAIL=" + email;

const extraInt = "";
const extraLong = "beginTime=" + beginMiliTime + 
  "\tendTime=" + endMiliTime;
const extraFloat = "";


jsIntent.launchApp(
	"android.intent.action.INSERT",
	"content://com.android.calendar/events",
	extraString: tabSepalatedString,
	extraInt: tabSepalatedString,
	extraLong: tabSepalatedString,
	extraFloat: tabSepalatedString
)

```
- `${01}`, `${001}` -> [pre reserved word](https://github.com/puutaro/CommandClick/blob/master/md/developer/js_pre_reserved_word.md)
