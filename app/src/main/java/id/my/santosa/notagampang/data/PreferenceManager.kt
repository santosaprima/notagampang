package id.my.santosa.notagampang.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class ThemeMode {
  LIGHT,
  DARK,
  SYSTEM,
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferenceManager(private val context: Context) {
  companion object {
    val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
    val WHATSAPP_PROMPT_KEY = stringPreferencesKey("whatsapp_prompt")
    const val DEFAULT_WHATSAPP_PROMPT =
      "Halo {nama}, mengingatkan ada tagihan di Angkringan sebesar {tagihan}. Terima kasih!"
    const val WHATSAPP_AUTOMATIC_FOOTER =
      "\n\n_Pesan ini dikirim otomatis oleh aplikasi Notagampang._"
  }

  val themeMode: Flow<ThemeMode> =
    context.dataStore.data.map { preferences ->
      val themeString = preferences[THEME_MODE_KEY] ?: ThemeMode.SYSTEM.name
      ThemeMode.valueOf(themeString)
    }

  val whatsappPrompt: Flow<String> =
    context.dataStore.data.map { preferences ->
      preferences[WHATSAPP_PROMPT_KEY] ?: DEFAULT_WHATSAPP_PROMPT
    }

  suspend fun setThemeMode(mode: ThemeMode) {
    context.dataStore.edit { preferences -> preferences[THEME_MODE_KEY] = mode.name }
  }

  suspend fun setWhatsappPrompt(prompt: String) {
    context.dataStore.edit { preferences -> preferences[WHATSAPP_PROMPT_KEY] = prompt }
  }
}
