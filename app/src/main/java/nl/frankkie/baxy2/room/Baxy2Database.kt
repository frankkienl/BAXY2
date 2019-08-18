package nl.frankkie.baxy2.room

import androidx.room.Database
import androidx.room.RoomDatabase
import nl.frankkie.baxy2.room.model.AppInfo

@Database(entities = arrayOf(AppInfo::class), version = 1)
abstract class Baxy2Database : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao
}