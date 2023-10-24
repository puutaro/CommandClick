# isend

Intent sender in com.puutaro.commandclick


Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [intent_action](#intent_action)
  * [uri_str](#uri_str)
  * [extra_strs](#extra_strs)
  * [extra_ints](#extra_ints)
  * [extra_longs](#extra_longs)
  * [extra_floats](#extra_floats)
  

## Overview

Launch notification on android, for background service, etc.


```sh.sh

noti \
	{arguments}
```

## Argument

### --action, -a <a id="intent_action"></a>

Intent action string

| type | description |
| ------ | -------|
| launch | launch notification |
| exit | close notification |

### --uri-str, -u <a id="uri_str"></a>

Uri string

### [Optional] --extra-strs, -s <a id="extra_strs"></a>

Intent extra string

- format:

  ${key1}=${valueStr2},${key1}=${valueStr2},..

- ex)

  --extra-strs="${key1}=${valueStr2},${key1}=${valueStr2},.."

### [Optional] --extra-ints, -i <a id="extra_ints"></a>

Intent extra int

- format:

  ${key1}=${valueIntStr2},${key1}=${valueIntStr2},..

- ex)

  --extra-ints="${key1}=${valueIntStr2},${key1}=${valueIntStr2},.."

### [Optional] --extra-longs, -l <a id="extra_longs"></a>

Intent extra long

- format:

  ${key1}=${valueLongStr2},${key1}=${valueLongStr2},..

- ex)

  --extra-longs="${key1}=${valueLongStr2},${key1}=${valueLongStr2},.."

### [Optional] --extra-floats, -f <a id="extra_floats"></a>

Intent extra string

- format:

  ${key1}=${valueFloatStr2},${key1}=${valueFloatStr2},..

- ex)   

  --extra-floats="${key1}=${valueFloatStr2},${key1}=${valueFloatStr2},.."


