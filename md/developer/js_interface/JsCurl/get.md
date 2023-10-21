
# get

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)

## Overview

`Get` request response string

```js.js
jsCurl.get(
  mainUrl: string,
  queryParameter: String,
  header: String(ex Authorication\tbear token,contentType\ttext/plain..),
  Timeout: Int
)

```

## Argument

| arg name | type | description |
| -------- | -------- | -------- |
| mainUrl | string | url string |
| queryParameter | string | queryParameter to be sepalated by `&` |
| header | string | header to be sepalated by tab |
| Timeout | int | timeout mili sec |


ex1) 

```js.js
jsCurl.get(
		"https://www.google.com/",
		"",
		"",
		1000,
	)
  -> `Get` request response string

```

