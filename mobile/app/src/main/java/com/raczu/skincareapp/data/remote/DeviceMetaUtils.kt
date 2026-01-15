package com.raczu.skincareapp.data.remote

import android.os.Build

object DeviceMetaUtils {
    fun getDeviceMeta(): String {
        return StringBuilder()
            .append("model=${Build.MODEL};")
            .append("brand=${Build.BRAND};")
            .append("os_version=${Build.VERSION.RELEASE};")
            .append("sdk=${Build.VERSION.SDK_INT}")
            .toString()
    }
}
