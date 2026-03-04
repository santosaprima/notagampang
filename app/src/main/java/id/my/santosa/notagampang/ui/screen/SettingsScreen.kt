package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.data.ThemeMode
import id.my.santosa.notagampang.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
  val currentTheme by viewModel.themeMode.collectAsState()

  Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Text(
      text = "Tema Aplikasi",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 16.dp),
    )

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors =
        CardDefaults.cardColors(
          containerColor =
            MaterialTheme.colorScheme.surfaceVariant.copy(
              alpha = 0.5f,
            ),
        ),
    ) {
      Column(modifier = Modifier.selectableGroup()) {
        ThemeOption(
          selected = currentTheme == ThemeMode.LIGHT,
          onSelect = { viewModel.setThemeMode(ThemeMode.LIGHT) },
          icon = Icons.Default.BrightnessHigh,
          label = "Terang",
        )
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 16.dp),
          color =
            MaterialTheme.colorScheme.onSurfaceVariant.copy(
              alpha = 0.1f,
            ),
        )
        ThemeOption(
          selected = currentTheme == ThemeMode.DARK,
          onSelect = { viewModel.setThemeMode(ThemeMode.DARK) },
          icon = Icons.Default.Brightness4,
          label = "Gelap",
        )
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 16.dp),
          color =
            MaterialTheme.colorScheme.onSurfaceVariant.copy(
              alpha = 0.1f,
            ),
        )
        ThemeOption(
          selected = currentTheme == ThemeMode.SYSTEM,
          onSelect = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
          icon = Icons.Default.BrightnessAuto,
          label = "Ikuti Sistem",
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
      text = "Pesan WhatsApp",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 16.dp),
    )

    val whatsappPrompt by viewModel.whatsappPrompt.collectAsState()
    var tempPrompt by remember { mutableStateOf(whatsappPrompt) }

    LaunchedEffect(whatsappPrompt) { tempPrompt = whatsappPrompt }

    Card(
      modifier = Modifier.fillMaxWidth(),
      colors =
        CardDefaults.cardColors(
          containerColor =
            MaterialTheme.colorScheme.surfaceVariant.copy(
              alpha = 0.5f,
            ),
        ),
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
          value = tempPrompt,
          onValueChange = { tempPrompt = it },
          label = { Text("Template Pesan") },
          modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Gunakan {nama} untuk nama pelanggan dan {tagihan} untuk jumlah tagihan.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.fillMaxWidth(),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          TextButton(
            onClick = {
              viewModel.setWhatsappPrompt(
                id.my.santosa.notagampang.data.PreferenceManager
                  .DEFAULT_WHATSAPP_PROMPT,
              )
            },
            colors =
              ButtonDefaults.textButtonColors(
                contentColor = MaterialTheme.colorScheme.error,
              ),
          ) {
            Icon(
              Icons.Default.Refresh,
              contentDescription = null,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Atur Ulang")
          }
          Spacer(modifier = Modifier.width(8.dp))
          Button(
            onClick = { viewModel.setWhatsappPrompt(tempPrompt) },
            enabled = tempPrompt != whatsappPrompt,
            colors =
              ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
              ),
          ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Simpan")
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Versi Info
    Text(
      text = "Informasi Aplikasi",
      style = MaterialTheme.typography.titleMedium,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      modifier = Modifier.padding(bottom = 8.dp),
    )
    Text(
      text = "NotaGampang v1.0.0",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun ThemeOption(
  selected: Boolean,
  onSelect: () -> Unit,
  icon: ImageVector,
  label: String,
) {
  Row(
    modifier =
      Modifier.fillMaxWidth()
        .height(56.dp)
        .selectable(
          selected = selected,
          onClick = onSelect,
          role = Role.RadioButton,
        )
        .padding(horizontal = 16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint =
        if (selected) {
          MaterialTheme.colorScheme.secondary
        } else {
          MaterialTheme.colorScheme.onSurfaceVariant
        },
      modifier = Modifier.size(24.dp),
    )
    Spacer(modifier = Modifier.width(16.dp))
    Text(
      text = label,
      style = MaterialTheme.typography.bodyLarge,
      color =
        if (selected) {
          MaterialTheme.colorScheme.secondary
        } else {
          MaterialTheme.colorScheme.onSurface
        },
      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
      modifier = Modifier.weight(1f),
    )
    RadioButton(
      selected = selected,
      // handled by row selectable
      onClick = null,
      colors =
        RadioButtonDefaults.colors(
          selectedColor = MaterialTheme.colorScheme.secondary,
        ),
    )
  }
}
