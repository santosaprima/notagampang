package id.my.santosa.notagampang.ui.component.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.util.PriorityBackHandler
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel

@Composable
fun CategoryManagementTab(
  viewModel: CategoryManagementViewModel,
  bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
  val categories by viewModel.categories.collectAsState()
  var showDeleteDialog by remember { mutableStateOf(false) }
  var categoryToDelete by remember {
    mutableStateOf<id.my.santosa.notagampang.database.entity.CategoryEntity?>(null)
  }

  if (showDeleteDialog && categoryToDelete != null) {
    PriorityBackHandler {
      showDeleteDialog = false
      categoryToDelete = null
    }
    AlertDialog(
      onDismissRequest = {
        showDeleteDialog = false
        categoryToDelete = null
      },
      confirmButton = {
        TextButton(
          onClick = {
            categoryToDelete?.let { viewModel.deleteCategory(it) }
            showDeleteDialog = false
            categoryToDelete = null
          },
        ) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
      },
      dismissButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            categoryToDelete = null
          },
        ) { Text("Batal") }
      },
      title = { Text("Hapus Kategori?") },
      text = {
        Text(
          "Menghapus kategori \"${categoryToDelete?.name}\" juga akan menghapus SEMUA menu di dalamnya. " +
            "Tindakan ini tidak bisa dibatalkan.",
        )
      },
    )
  }

  Box(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
    if (categories.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "Belum ada kategori.\nKetuk tombol + untuk menambah.",
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
        items(categories, key = { it.id }) { cat ->
          ManagementItemCard(
            name = cat.name,
            detail = "Kategori",
            onDelete = {
              categoryToDelete = cat
              showDeleteDialog = true
            },
          )
        }
      }
    }
  }
}
