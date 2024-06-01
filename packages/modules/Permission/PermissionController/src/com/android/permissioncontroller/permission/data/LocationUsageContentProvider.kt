package com.android.permissioncontroller.permission.data

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.content.UriMatcher

class LocationUsageContentProvider : ContentProvider() {

    companion object {
        val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI("be.kuleuven.contentprovider", "data", 1)
        }
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        if (uriMatcher.match(uri) == 1) {
            val manager = PermissionUsageManager(context!!)
            val logs = manager.fetchPermissionLogs()
            val cursor = MatrixCursor(arrayOf("packageName", "permissionGroup", "accessTime"))

            // Always filter logs based on the permission group "android.permission-group.LOCATION"
            for (entry in logs) {
                if (entry.permissionGroup == "android.permission-group.LOCATION") {
                    cursor.addRow(arrayOf(entry.packageName, entry.permissionGroup, entry.accessTime))
                }
            }
            return cursor
        }
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}
