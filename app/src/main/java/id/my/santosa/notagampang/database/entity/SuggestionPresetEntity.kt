package id.my.santosa.notagampang.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "suggestion_presets")
data class SuggestionPresetEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val label: String,
  val sortOrder: Int = 0,
)
