package id.my.santosa.notagampang.ui.component.settings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
