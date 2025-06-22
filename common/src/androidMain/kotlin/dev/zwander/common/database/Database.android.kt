package dev.zwander.common.database

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zwander.common.App

actual fun getDatabaseBuilder(): RoomDatabase.Builder<Database> {
    val appContext = App.instance
    val dbFile = appContext.getDatabasePath("snapshots.db")
    return Room.databaseBuilder<Database>(
        context = appContext,
        name = dbFile.absolutePath
    )
}