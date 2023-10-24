# tsvar

Table
-----------------
* [Overview](#overview)
* [Usage](#usage)

## Overview

Get [tsv env variables](https://github.com/puutaro/CommandClick/blob/master/md/developer/ubuntuFileApis.md#ubuntu_env_varialbles)

```sh.sh

tsvar \
  "${contents}" \
  "${variable name}"
```

## Usage

```sh.sh
UBUNTU_ENV_TSV_PATH="/support/${UBUNTU_ENV_TSV_NAME}"
contents=$(cat "${UBUNTU_ENV_TSV_PATH}")
tsvar \
  "${contents}" \
  "UBUNTU_BACKUP_ROOTFS_PATH"

```

