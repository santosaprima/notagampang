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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.util.PriorityBackHandler
import id.my.santosa.notagampang.viewmodel.CategoryManagementViewModel

@Composable
fun AddCategorySheet(
  viewModel: CategoryManagementViewModel,
  onDismiss: () -> Unit,
) {
  val focusManager = LocalFocusManager.current
  PriorityBackHandler {
    focusManager.clearFocus()
    onDismiss()
  }
  var name by remember { mutableStateOf("") }
  Column(
    modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 32.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Text(
      "Tambah Kategori Baru",
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.onSurface,
    )

    OutlinedTextField(
      value = name,
      onValueChange = { name = it },
      label = { Text("Nama Kategori") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.medium,
      keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
    )

    Button(
      onClick = {
        viewModel.addCategory(name)
        onDismiss()
      },
      modifier = Modifier.fillMaxWidth().height(56.dp),
      enabled = name.isNotBlank(),
      shape = MaterialTheme.shapes.medium,
      colors =
        ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.secondary,
          contentColor = MaterialTheme.colorScheme.onSecondary,
        ),
    ) {
      Icon(Icons.Filled.Add, contentDescription = null)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Simpan Kategori", fontWeight = FontWeight.Bold)
    }
  }
}
