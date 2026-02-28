package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.SuggestionPresetEntity
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel

@Composable
fun SuggestionPresetsScreen(viewModel: SuggestionPresetsViewModel, onBack: () -> Unit = {}) {
  val presets by viewModel.presets.collectAsState()
  var newLabel by remember { mutableStateOf("") }

  Column(
          modifier = Modifier.fillMaxSize().padding(16.dp),
          verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
    ) {
      OutlinedTextField(
              value = newLabel,
              onValueChange = { newLabel = it },
              label = { Text("Tambah pilihan baru") },
              singleLine = true,
              modifier = Modifier.weight(1f)
      )
      FloatingActionButton(
              onClick = {
                viewModel.addPreset(newLabel)
                newLabel = ""
              },
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary
      ) { Icon(Icons.Filled.Add, contentDescription = "Tambah") }
    }

    if (presets.isEmpty()) {
      Text(
              text = "Belum ada pilihan cepat. Tambahkan di atas.",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              modifier = Modifier.padding(top = 32.dp)
      )
    } else {
      LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(presets, key = { it.id }) { preset ->
          PresetCard(preset = preset, onDelete = { viewModel.deletePreset(preset) })
        }
      }
    }
  }
}

@Composable
fun PresetCard(preset: SuggestionPresetEntity, onDelete: () -> Unit) {
  Card(
          modifier = Modifier.fillMaxWidth(),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          colors =
                  CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
  ) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
              text = preset.label,
              style = MaterialTheme.typography.bodyLarge,
              modifier = Modifier.weight(1f)
      )
      IconButton(onClick = onDelete) {
        Icon(
                Icons.Filled.Delete,
                contentDescription = "Hapus",
                tint = MaterialTheme.colorScheme.error
        )
      }
    }
  }
}
