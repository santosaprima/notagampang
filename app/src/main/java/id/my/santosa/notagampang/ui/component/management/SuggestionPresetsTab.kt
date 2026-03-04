package id.my.santosa.notagampang.ui.component.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel

@Composable
fun SuggestionPresetsTab(
  viewModel: SuggestionPresetsViewModel,
  bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
  val presets by viewModel.presets.collectAsState()

  Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
    if (presets.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "Belum ada pilihan cepat.\nKetuk tombol + untuk menambah.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 24.dp, bottom = bottomPadding + 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        items(presets, key = { it.id }) { preset ->
          ManagementItemCard(
            name = preset.label,
            detail = "Pilihan Cepat",
            onDelete = { viewModel.deletePreset(preset) },
          )
        }
      }
    }
  }
}
