package id.my.santosa.notagampang.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SuggestionPresetDao {
  @Query("SELECT * FROM suggestion_presets ORDER BY sortOrder ASC, id ASC")
  fun getAllPresets(): Flow<List<SuggestionPresetEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertPreset(preset: SuggestionPresetEntity): Long

  @Delete suspend fun deletePreset(preset: SuggestionPresetEntity)

  @Query("SELECT COUNT(*) FROM suggestion_presets")
  suspend fun getCount(): Int
}
