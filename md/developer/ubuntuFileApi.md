# ubuntuFileApi

Support file for `CommandClick`'s ubuntu
For example, alternative data base, etc..

- "~" -> `/storage/emulated/0/Documents/cmdclick`


Table
-----------------
* [`/support/ubuntu_env.tsv`](#ubuntu_env_varialbles)
* [`/support/wait_quiz.tsv`](#wait_quiz_tsv)

  

## `/support/ubuntu_env.tsv` <a id="ubuntu_env_varialbles"></a>

Second environment variables tsv like key tab value


ex)

```
WAIT_QUIZ_TSV_NAME\twait_quiz.tsv 
UBUNTU_BACKUP_ROOTFS_PATH\t/storage/emulated/0/Documents/cmdclcik/ubuntu/backup/rootfs.tar.gz,
UBUNTU_BACKUP_TEMP_ROOTFS_PATH\t/storage/emulated/0/Documents/cmdclcik/ubuntu/backup/temp/rootfs.tar.gz,
.
.
.
```

## `/support/wait_quiz.tsv` <a id="wait_quiz_tsv"></a>

Wait quizes about `CommandClick` like Q tab A

ex)
```
What's alternative fakeroot\t-> green-green-avk/build-proot-android in recent android
Why select fakeroot\tSteady on many device
.
.
.
```

