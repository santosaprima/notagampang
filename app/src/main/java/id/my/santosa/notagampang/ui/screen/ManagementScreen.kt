package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.component.management.AddCategorySheet
import id.my.santosa.notagampang.ui.component.management.AddMenuSheet
import id.my.santosa.notagampang.ui.component.management.AddPresetSheet
import id.my.santosa.notagampang.ui.component.management.CategoryManagementTab
import id.my.santosa.notagampang.ui.component.management.MenuManagementTab
import id.my.santosa.notagampang.ui.component.management.SuggestionPresetsTab
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel
import id.my.santosa.notagampang.viewmodel.SuggestionPresetsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
  menuViewModel: MenuManagementViewModel,
  presetsViewModel: SuggestionPresetsViewModel,
  categoryViewModel: CategoryManagementViewModel,
  showAddSheet: Boolean,
  bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
  onSheetDismiss: () -> Unit,
) {
  var selectedTab by remember { mutableIntStateOf(1) } // Default to Menu
  val tabs = listOf("Kategori", "Menu", "Pilihan Cepat")
  val sheetState = rememberModalBottomSheetState()

  Column(modifier = Modifier.fillMaxSize()) {
    // Tab Row
    PrimaryTabRow(
      selectedTabIndex = selectedTab,
      containerColor = MaterialTheme.colorScheme.surface,
      contentColor = MaterialTheme.colorScheme.primary,
      indicator = {
        TabRowDefaults.PrimaryIndicator(
          modifier = Modifier.tabIndicatorOffset(selectedTab),
          width = 64.dp,
          color = MaterialTheme.colorScheme.secondary,
        )
      },
    ) {
      tabs.forEachIndexed { index, title ->
        Tab(
          selected = selectedTab == index,
          onClick = { selectedTab = index },
          text = {
            Text(
              title,
              style = MaterialTheme.typography.titleMedium,
              fontWeight =
                if (selectedTab == index) {
                  FontWeight.ExtraBold
                } else {
                  FontWeight.SemiBold
                },
            )
          },
          selectedContentColor = MaterialTheme.colorScheme.secondary,
          unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }
    }

    Box(modifier = Modifier.fillMaxSize()) {
      when (selectedTab) {
        0 ->
          CategoryManagementTab(
            viewModel = categoryViewModel,
            bottomPadding = bottomPadding,
          )
        1 ->
          MenuManagementTab(
            viewModel = menuViewModel,
            categoryViewModel = categoryViewModel,
            bottomPadding = bottomPadding,
          )
        2 ->
          SuggestionPresetsTab(
            viewModel = presetsViewModel,
            bottomPadding = bottomPadding,
          )
      }
    }
  }

  if (showAddSheet) {
    ModalBottomSheet(
      onDismissRequest = onSheetDismiss,
      sheetState = sheetState,
      containerColor = MaterialTheme.colorScheme.surface,
    ) {
      when (selectedTab) {
        0 -> AddCategorySheet(viewModel = categoryViewModel, onDismiss = onSheetDismiss)
        1 ->
          AddMenuSheet(
            viewModel = menuViewModel,
            categoryViewModel = categoryViewModel,
            onDismiss = onSheetDismiss,
          )
        2 -> AddPresetSheet(viewModel = presetsViewModel, onDismiss = onSheetDismiss)
      }
    }
  }
}
