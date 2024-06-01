package com.android.permissioncontroller.permission.data

import com.android.permissioncontroller.permission.model.v31.PermissionUsages;
import com.android.permissioncontroller.permission.model.v31.AppPermissionUsage;

import android.content.Context
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.Instant

class PermissionUsageManager(private val context: Context) {

    private val permissionUsages: PermissionUsages = PermissionUsages(context)
    

    fun fetchPermissionLogs(): List<PermissionLogEntry> {
        val logs = mutableListOf<PermissionLogEntry>()
        val currentTime = System.currentTimeMillis()
        val usages = permissionUsages.getPermissionUsagesSynchronously(
            android.os.Process.INVALID_UID, null, null,
            currentTime - TimeUnit.DAYS.toMillis(1), currentTime,
            PermissionUsages.USAGE_FLAG_LAST or PermissionUsages.USAGE_FLAG_HISTORICAL,
            false, false
        )

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault())

        usages.forEach { appUsage ->
            appUsage.groupUsages.forEach { groupUsage ->
                if (groupUsage.hasDiscreteData()) {
                    groupUsage.allDiscreteAccessTime.forEach { discreteAccessTime ->
                        if (discreteAccessTime.first > 0 && discreteAccessTime.first >= currentTime - TimeUnit.DAYS.toMillis(1)) {
                            val accessTimeStr = formatter.format(Instant.ofEpochMilli(discreteAccessTime.first))
                            logs.add(PermissionLogEntry(appUsage.app.packageName, groupUsage.group.name, accessTimeStr))
                        }
                    }
                }
            }
        }

        return logs
    }
}

data class PermissionLogEntry(val packageName: String, val permissionGroup: String, val accessTime: String)


