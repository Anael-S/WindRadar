package com.anael.samples.apps.windradar.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertDao {

    @Query("SELECT * FROM alerts ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<AlertEntity>>

    @Query("SELECT * FROM alerts WHERE enabled = 1 ORDER BY createdAt DESC")
    fun observeEnabled(): Flow<List<AlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(alert: AlertEntity)

    @Query("UPDATE alerts SET enabled = :enabled WHERE id = :id")
    suspend fun setEnabled(id: String, enabled: Boolean)

    @Update
    suspend fun update(alert: AlertEntity)

    @Query("DELETE FROM alerts WHERE id = :id")
    suspend fun deleteById(id: String)
}
