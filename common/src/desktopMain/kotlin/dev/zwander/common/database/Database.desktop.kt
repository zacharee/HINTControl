package dev.zwander.common.database

import androidx.room.Room
import androidx.room.RoomDatabase
import dev.zwander.common.GradleConfig
import net.harawata.appdirs.AppDirsFactory

actual fun getDatabaseBuilder(): RoomDatabase.Builder<Database> {
    val path = "${AppDirsFactory.getInstance().getUserDataDir(GradleConfig.appName, null, "Zachary Wander")}/snapshots.db"

    return Room.databaseBuilder<Database>(
        name = path,
    )
}