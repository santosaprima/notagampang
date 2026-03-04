package id.my.santosa.notagampang.repository

import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import kotlinx.coroutines.flow.Flow

interface ISuggestionPresetRepository {
  fun getAllPresets(): Flow<List<SuggestionPresetEntity>>

  suspend fun insertPreset(preset: SuggestionPresetEntity): Long

  suspend fun deletePreset(preset: SuggestionPresetEntity)

  suspend fun getCount(): Int
}
