# wqmsg

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [--help](#--help) 
  * [pid](#pid)
  * [mark string](#mark-string)
* [example](#example)

## Overview

Toast wait quiz until backgrond process complete


```sh.sh
wqmsg \
  "${pid}" \
  "${mark string}"
```

## Argument

### --help

help contents

### pid

Wait target pid

### [Optional] mark string <a id="mark-string"></a>

Wait quiz's mark string

## example

ex1)

```sh.sh

sleep 60 &
sleep_pid=$!

wqmsg \
  "${sleep_pid}" \
  "SL"

# 0s -> [SL 0s] Q. {quiz contents1}
# 3s -> [SL 3s] Q. {quiz contents2}
# 6s -> [SL 6s] Q. {quiz contents3}
# ...

```

ex2)

```sh.sh

sleep 60 &
sleep_pid=$!

wqmsg \
  "${sleep_pid}"

# 0s -> [0s] Q. {quiz contents1}
# 3s -> [3s] Q. {quiz contents2}
# 6s -> [6s] Q. {quiz contents3}
# ...

```
