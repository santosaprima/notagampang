package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun FloatingTabsScreen(
        viewModel: FloatingTabsViewModel,
        suggestions: List<String>,
        onTabClick: (Long) -> Unit
) {
  val activeGroups by viewModel.activeGroups.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }
  var newGroupName by remember { mutableStateOf("") }

  Box(modifier = Modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      Row(
              modifier = Modifier.fillMaxWidth().padding(8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
                "Nota Aktif",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
        )
      }

      if (activeGroups.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text(
                  "Belum ada nota aktif.\nKetuk + untuk menambah.",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier = Modifier.padding(16.dp)
          )
        }
      } else {
        LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
        ) {
          items(activeGroups, key = { it.group.id }) { groupWithTotal ->
            NotaCard(
                    groupWithTotal = groupWithTotal,
                    onClick = { onTabClick(groupWithTotal.group.id) }
            )
          }
        }
      }
    }

    FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
    ) { Icon(Icons.Default.Add, contentDescription = "Tambah Nota") }
  }

  if (showAddDialog) {
    AlertDialog(
            onDismissRequest = {
              showAddDialog = false
              newGroupName = ""
            },
            title = { Text("Tambah Nota Baru") },
            text = {
              Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                        value = newGroupName,
                        onValueChange = { newGroupName = it },
                        label = { Text("Nama Nota (misal: Meja 1)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        minLines = 2,
                        maxLines = 4
                )

                Text("Pilihan Cepat:", style = MaterialTheme.typography.labelMedium)

                LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 100.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp)
                ) {
                  items(suggestions) { suggestion ->
                    Button(
                            onClick = {
                              newGroupName =
                                      if (newGroupName.isEmpty()) {
                                        suggestion
                                      } else if (newGroupName.endsWith(" ")) {
                                        "$newGroupName$suggestion"
                                      } else {
                                        "$newGroupName $suggestion"
                                      }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) { Text(suggestion) }
                  }
                }
              }
            },
            confirmButton = {
              Button(
                      onClick = {
                        if (newGroupName.isNotBlank()) {
                          viewModel.createNewTab(newGroupName)
                          showAddDialog = false
                          newGroupName = ""
                        }
                      }
              ) { Text("Tambah") }
            },
            dismissButton = {
              TextButton(
                      onClick = {
                        showAddDialog = false
                        newGroupName = ""
                      }
              ) { Text("Batal") }
            }
    )
  }
}

@Composable
fun NotaCard(
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
