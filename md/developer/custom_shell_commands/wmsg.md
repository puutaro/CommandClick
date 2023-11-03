# wmsg

Table
-----------------
* [Overview](#overview)
* [Argument](#argument)
  * [pid](#pid)
  * [message](#message)
  * [wait increasing symbol](#wait-increasing-symbol)
  * [example](#example)

## Overview

Toast wait message until backgrond process complete by 3 seconds


```sh.sh
wmsg \
  "${pid}" \
  "${message}" \
  "${wait increasing symbol}"
```

## Argument

### pid

Wait target pid

### message

Wait message

### wait increasing symbol

Wait increasing simbol

- default value -> `.`

### example

```sh.sh

sleep 60 &
sleep_pid=$!

wmsg \
  "${sleep_pid}"
  "wait"

# 0s -> wait.
# 3s -> wait..
# 6s -> wait...
# ...

```
