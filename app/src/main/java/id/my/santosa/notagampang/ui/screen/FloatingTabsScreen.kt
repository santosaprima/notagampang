package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingTabsScreen(
  viewModel: FloatingTabsViewModel,
  suggestions: List<String>,
  onTabClick: (Long) -> Unit,
  onKasbonClick: () -> Unit,
  onSettingsClick: () -> Unit,
  onMenuSettingsClick: () -> Unit,
  onShiftManagementClick: () -> Unit,
) {
  val activeGroups by viewModel.activeGroups.collectAsState()
  var showNewTabDialog by remember { mutableStateOf(false) }

  Scaffold(
    topBar = {
      var showMenu by remember { mutableStateOf(false) }
      TopAppBar(
        title = { Text("Nota Aktif") },
        actions = {
          Box {
            IconButton(onClick = { showMenu = !showMenu }) {
              Icon(Icons.Filled.MoreVert, contentDescription = "Lainnya")
            }
            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
              DropdownMenuItem(
                text = { Text("Atur Pilihan Cepat") },
                onClick = {
                  showMenu = false
                  onSettingsClick()
                },
                leadingIcon = {
                  Icon(Icons.Filled.Settings, contentDescription = null)
                },
              )
              DropdownMenuItem(
                text = { Text("Buku Kasbon") },
                onClick = {
                  showMenu = false
                  onKasbonClick()
                },
              )
              DropdownMenuItem(
                text = { Text("Kelola Menu (Makanan/Minuman)") },
                onClick = {
                  showMenu = false
                  onMenuSettingsClick()
                },
                leadingIcon = {
                  Icon(Icons.Filled.Add, contentDescription = null)
                },
              )
              DropdownMenuItem(
                text = { Text("Tutup Kasir (Shift)") },
                onClick = {
                  showMenu = false
                  onShiftManagementClick()
                },
                leadingIcon = {
                  Icon(Icons.Filled.Refresh, contentDescription = null)
                },
              )
            }
          }
        },
        colors =
          TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor =
              MaterialTheme.colorScheme.onPrimaryContainer,
          ),
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { showNewTabDialog = true },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
      ) { Icon(Icons.Filled.Add, contentDescription = "Buka Nota Baru") }
    },
  ) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      if (activeGroups.isEmpty()) {
        EmptyState(modifier = Modifier.weight(1f))
      } else {
        ActiveTabsGrid(
          groups = activeGroups,
          onTabClick = onTabClick,
          modifier = Modifier.weight(1f),
        )
      }
    }

    if (showNewTabDialog) {
      NewTabBottomSheet(
        suggestions = suggestions,
        onDismiss = { showNewTabDialog = false },
        onConfirm = { alias ->
          viewModel.createNewTab(alias)
          showNewTabDialog = false
        },
      )
    }
  }
}

@Composable
fun EmptyState(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(
      text = "Belum ada nota aktif",
      style = MaterialTheme.typography.titleLarge,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    Text(
      text = "Tekan tombol + untuk membuka nota baru",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@Composable
fun ActiveTabsGrid(
  groups: List<CustomerGroupWithTotal>,
  onTabClick: (Long) -> Unit,
  modifier: Modifier = Modifier,
) {
  LazyVerticalGrid(
    columns = GridCells.Adaptive(minSize = 150.dp),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    modifier = modifier,
  ) {
    items(groups, key = { it.group.id }) { groupWithTotal ->
      TabCard(
        groupWithTotal = groupWithTotal,
        onClick = { onTabClick(groupWithTotal.group.id) },
      )
    }
  }
}

@Composable
fun TabCard(
  groupWithTotal: CustomerGroupWithTotal,
  onClick: () -> Unit,
) {
  val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
  currencyFormat.maximumFractionDigits = 0

  Card(
    modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    colors =
      CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
      ),
  ) {
    Column(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = groupWithTotal.group.alias,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        maxLines = 2,
      )
      Text(
        text = currencyFormat.format(groupWithTotal.totalAmount),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSecondaryContainer,
        modifier = Modifier.padding(top = 8.dp),
      )
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTabBottomSheet(
  suggestions: List<String>,
  onDismiss: () -> Unit,
  onConfirm: (String) -> Unit,
) {
  var alias by remember { mutableStateOf("") }
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp)
          // Extra padding for system nav bar
          .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Text(
        text = "Buka Nota Baru",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
      )

      OutlinedTextField(
        value = alias,
        onValueChange = { alias = it },
        label = { Text("Nama/Ciri Pelanggan (Cth: Topi Merah)") },
        minLines = 2,
        // Allow multiline but cap it
        maxLines = 4,
        modifier = Modifier.fillMaxWidth(),
      )

      Text("Pilihan Cepat:", style = MaterialTheme.typography.labelMedium)

      LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 100.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
      ) {
        items(suggestions) { suggestion ->
          Button(
            onClick = {
              alias =
                if (alias.isEmpty()) {
                  suggestion
                } else if (alias.endsWith(" ")) {
                  "$alias$suggestion"
                } else {
                  "$alias $suggestion"
                }
            },
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
          ) { Text(suggestion) }
        }
      }

      Button(
        onClick = { onConfirm(alias) },
        enabled = alias.isNotBlank(),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
      ) { Text("Buka Nota") }
    }
  }
}
