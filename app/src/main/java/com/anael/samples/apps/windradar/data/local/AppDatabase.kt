package com.anael.samples.apps.windradar.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [AlertEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alertDao(): AlertDao
}
