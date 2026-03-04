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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.util.CurrencyUtil.formatCurrency
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel

@Composable
fun MenuManagementTab(
  viewModel: MenuManagementViewModel,
  categoryViewModel: CategoryManagementViewModel,
  bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
) {
  val menuItems by viewModel.menuItems.collectAsState()
  val categoriesEntities by categoryViewModel.categories.collectAsState()
  val categories = categoriesEntities.map { it.name }
  val groupedItems = menuItems.groupBy { it.category }

  Box(modifier = Modifier.fillMaxSize()) {
    if (menuItems.isEmpty()) {
      Box(
        modifier = Modifier.fillMaxSize().padding(bottom = 140.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = "Belum ada menu.\nKetuk tombol + untuk menambah.",
          style = MaterialTheme.typography.bodyLarge,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
      }
    } else {
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
          PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 24.dp,
            bottom = bottomPadding + 20.dp,
          ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
      ) {
        categories.forEach { cat ->
          val itemsInCategory = groupedItems[cat] ?: emptyList()
          if (itemsInCategory.isNotEmpty()) {
            item {
              Text(
                cat,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
              )
            }
            items(itemsInCategory, key = { it.id }) { item ->
              ManagementItemCard(
                name = item.name,
                detail = "${item.category} • ${formatCurrency(item.price)}",
                onDelete = { viewModel.deleteMenuItem(item) },
              )
            }
          }
        }
      }
    }
  }
}
