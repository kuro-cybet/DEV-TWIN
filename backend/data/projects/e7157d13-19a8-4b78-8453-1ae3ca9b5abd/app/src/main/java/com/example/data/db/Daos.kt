package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.CropScan
import com.example.data.models.FarmField
import com.example.data.models.FarmTask
import com.example.data.models.FarmerProfile
import com.example.data.models.MandiCommodity
import com.example.data.models.VoiceMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface FarmFieldDao {
    @Query("SELECT * FROM farm_fields ORDER BY id ASC")
    fun getAllFields(): Flow<List<FarmField>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(field: FarmField): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(fields: List<FarmField>)

    @Update
    suspend fun update(field: FarmField)

    @Query("DELETE FROM farm_fields WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface CropScanDao {
    @Query("SELECT * FROM crop_scans ORDER BY timestamp DESC")
    fun getAllScans(): Flow<List<CropScan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(scan: CropScan): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(scans: List<CropScan>)

    @Update
    suspend fun update(scan: CropScan)

    @Query("UPDATE crop_scans SET isResolved = :resolved WHERE id = :id")
    suspend fun updateResolvedStatus(id: Long, resolved: Boolean)

    @Query("DELETE FROM crop_scans WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface FarmTaskDao {
    @Query("SELECT * FROM farm_tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<FarmTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: FarmTask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<FarmTask>)

    @Update
    suspend fun update(task: FarmTask)

    @Query("UPDATE farm_tasks SET isCompleted = :completed WHERE id = :id")
    suspend fun toggleTaskCompletion(id: Long, completed: Boolean)

    @Query("DELETE FROM farm_tasks WHERE id = :id")
    suspend fun deleteById(id: Long)
}

@Dao
interface MandiDao {
    @Query("SELECT * FROM mandi_commodities ORDER BY id ASC")
    fun getAllCommodities(): Flow<List<MandiCommodity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<MandiCommodity>)

    @Query("SELECT DISTINCT mandiName FROM mandi_commodities")
    fun getUniqueMandis(): Flow<List<String>>
}

@Dao
interface FarmerProfileDao {
    @Query("SELECT * FROM farmer_profile WHERE id = 1")
    fun getProfile(): Flow<FarmerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: FarmerProfile)
}

@Dao
interface VoiceMessageDao {
    @Query("SELECT * FROM voice_messages ORDER BY timestamp ASC")
    fun getAllMessages(): Flow<List<VoiceMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: VoiceMessage): Long

    @Query("DELETE FROM voice_messages")
    suspend fun clearHistory()
}
