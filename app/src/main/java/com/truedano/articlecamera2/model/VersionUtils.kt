package com.truedano.articlecamera2.model

import android.content.Context
import android.content.pm.PackageManager
import com.truedano.articlecamera2.BuildConfig

object VersionUtils {
    fun getVersionName(context: Context): String {
        // 使用 BuildConfig 獲取從 build.gradle.kts 定義的版本名稱
        return try {
            BuildConfig.VERSION_NAME
        } catch (_: Exception) {
            // 如果 BuildConfig 不可用，則回退到傳統方法
            try {
                val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                packageInfo.versionName ?: "Unknown"
            } catch (_: PackageManager.NameNotFoundException) {
                "Unknown"
            }
        }
    }

    fun getVersionWithPrefix(context: Context): String {
        val version = getVersionName(context)
        return if (version != "Unknown") "v$version" else version
    }
}