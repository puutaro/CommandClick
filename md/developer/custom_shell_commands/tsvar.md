# tsvar

Table
-----------------
* [Overview](#overview)
* [Usage](#usage)
  * [Get from tsv data](#get-from-tsv-data)
  * [Create tsv data from 1 row csv data](#create-tsv-data-from-1-row-csv-data)

## Overview

Get [tsv env variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntuFileApis.md#ubuntu_env_varialbles)

```sh.sh

tsvar \
  ${args}
```

## Usage

### Get from tsv data

```sh.sh

tsvar \
  "${tsv contents}" \
  "${variable name}"
```

ex)
```sh.sh
UBUNTU_ENV_TSV_PATH="/support/${UBUNTU_ENV_TSV_NAME}"
tsv_contents=$(cat "${UBUNTU_ENV_TSV_PATH}")
tsvar \
  "${tsv_contents}" \
  "UBUNTU_BACKUP_ROOTFS_PATH"

```

### Create tsv data from 1 row csv data

tsvar \
  "${1 row csv contents}"

ex)
```sh.sh

one_line_csv_contents="key1=value1,key2=\"value 2\",key3=value3,.."
tsv_contents=$(\
  tsvar \
    "${one_line_csv_contents}" \
)

value1=$(\
  tsvar \
    "${tsv_contents}" \
    key1 \
) # value1

value2=$(\
  tsvar \
    "${tsv_contents}" \
    key1 \
) # value 2

value3=$(\
  tsvar \
    "${tsv_contents}" \
    key1 \
) # value3

```

- Used when passing multiple value between shellscript
