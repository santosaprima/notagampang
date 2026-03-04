package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.dao.SuggestionPresetDao
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import kotlinx.coroutines.flow.Flow

class SuggestionPresetRepositoryImpl(
  private val dao: SuggestionPresetDao,
) : ISuggestionPresetRepository {
  override fun getAllPresets(): Flow<List<SuggestionPresetEntity>> = dao.getAllPresets()

  override suspend fun insertPreset(preset: SuggestionPresetEntity): Long = dao.insertPreset(preset)

  override suspend fun deletePreset(preset: SuggestionPresetEntity) = dao.deletePreset(preset)

  override suspend fun getCount(): Int = dao.getCount()
}
