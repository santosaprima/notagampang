package id.my.santosa.notagampang.ui.component.management

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.util.PriorityBackHandler
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel
import id.my.santosa.notagampang.viewmodel.MenuManagementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuSheet(
  viewModel: MenuManagementViewModel,
  categoryViewModel: CategoryManagementViewModel,
  onDismiss: () -> Unit,
) {
  val focusManager = LocalFocusManager.current
  PriorityBackHandler {
    focusManager.clearFocus()
    onDismiss()
  }
  val categoriesEntities by categoryViewModel.categories.collectAsState()
  val categories = categoriesEntities.map { it.name }

  var name by remember { mutableStateOf("") }
  var priceStr by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Makanan") }
  var expanded by remember { mutableStateOf(false) }

  LaunchedEffect(categories) {
    if (category !in categories && categories.isNotEmpty()) {
      category = categories.first()
    }
  }

  Column(
    modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      "Tambah Menu Baru",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
    )

    ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded },
      modifier = Modifier.fillMaxWidth(),
    ) {
      OutlinedTextField(
        value = category,
        onValueChange = {},
        readOnly = true,
        label = { Text("Kategori") },
        trailingIcon = {
          ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
        },
        modifier = Modifier.menuAnchor().fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
      )
      ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
        categories.forEach { selection ->
          DropdownMenuItem(
            text = { Text(selection) },
            onClick = {
              category = selection
              expanded = false
            },
          )
        }
      }
    }

    OutlinedTextField(
      value = name,
      onValueChange = { name = it },
      label = { Text("Nama Menu") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.medium,
      keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
    )

    OutlinedTextField(
      value = priceStr,
      onValueChange = { if (it.all { char -> char.isDigit() }) priceStr = it },
      label = { Text("Harga (Rp)") },
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.medium,
    )

    Button(
      onClick = {
        val price = priceStr.toIntOrNull() ?: 0
        viewModel.addMenuItem(name, price, category)
        onDismiss()
      },
      modifier = Modifier.fillMaxWidth().height(56.dp),
      enabled = name.isNotBlank() && priceStr.isNotBlank(),
      shape = MaterialTheme.shapes.medium,
      colors =
        ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) {
      Icon(Icons.Filled.Add, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Simpan Menu", fontWeight = FontWeight.Bold)
    }
  }
}
