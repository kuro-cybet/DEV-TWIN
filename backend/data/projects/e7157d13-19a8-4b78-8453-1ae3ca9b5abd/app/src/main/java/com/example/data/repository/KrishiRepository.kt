package com.example.data.repository

import com.example.data.db.KrishiDatabase
import com.example.data.models.CropScan
import com.example.data.models.FarmField
import com.example.data.models.FarmTask
import com.example.data.models.FarmerProfile
import com.example.data.models.MandiCommodity
import com.example.data.models.VoiceMessage
import kotlinx.coroutines.flow.Flow

class KrishiRepository(private val database: KrishiDatabase) {

    val allFields: Flow<List<FarmField>> = database.farmFieldDao().getAllFields()
    val allScans: Flow<List<CropScan>> = database.cropScanDao().getAllScans()
    val allTasks: Flow<List<FarmTask>> = database.farmTaskDao().getAllTasks()
    val allCommodities: Flow<List<MandiCommodity>> = database.mandiDao().getAllCommodities()
    val profile: Flow<FarmerProfile?> = database.farmerProfileDao().getProfile()
    val voiceMessages: Flow<List<VoiceMessage>> = database.voiceMessageDao().getAllMessages()

    suspend fun addField(field: FarmField): Long = database.farmFieldDao().insert(field)
    suspend fun updateField(field: FarmField) = database.farmFieldDao().update(field)
    suspend fun deleteField(id: Long) = database.farmFieldDao().deleteById(id)

    suspend fun addScan(scan: CropScan): Long = database.cropScanDao().insert(scan)
    suspend fun setScanResolved(id: Long, resolved: Boolean) = database.cropScanDao().updateResolvedStatus(id, resolved)
    suspend fun deleteScan(id: Long) = database.cropScanDao().deleteById(id)

    suspend fun addTask(task: FarmTask): Long = database.farmTaskDao().insert(task)
    suspend fun toggleTask(id: Long, completed: Boolean) = database.farmTaskDao().toggleTaskCompletion(id, completed)
    suspend fun deleteTask(id: Long) = database.farmTaskDao().deleteById(id)

    suspend fun updateProfile(profile: FarmerProfile) = database.farmerProfileDao().insertOrUpdate(profile)

    suspend fun addVoiceMessage(message: VoiceMessage): Long = database.voiceMessageDao().insert(message)
    suspend fun clearVoiceHistory() = database.voiceMessageDao().clearHistory()
}
