package com.puutaro.commandclick.service.lib.ubuntu

import kotlinx.coroutines.Job

data class UbuntuCoroutineJobsHashMapUpdateData(
    val ubuntuCoroutineJobsHashMap: HashMap<String, Job?>,
    val backgroundJobType: String,
)