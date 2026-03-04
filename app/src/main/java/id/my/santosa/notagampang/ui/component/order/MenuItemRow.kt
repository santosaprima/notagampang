package id.my.santosa.notagampang.ui.component.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.RemoveCircleOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.util.CurrencyUtil.formatCurrency

@Composable
fun MenuItemRow(
  name: String,
  price: Int,
  quantity: Int,
  isReadOnly: Boolean = false,
  onIncrease: () -> Unit,
  onDecrease: () -> Unit,
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium,
    colors =
      CardDefaults.cardColors(
        containerColor =
          MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
      ),
    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
  ) {
    Row(
      modifier = Modifier.padding(16.dp).fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          name,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
          formatCurrency(price),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      if (!isReadOnly) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
          if (quantity > 0) {
            Surface(
              modifier = Modifier.size(32.dp),
              color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
              shape = MaterialTheme.shapes.small,
              onClick = onDecrease,
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Outlined.RemoveCircleOutline,
                  contentDescription = "Kurangi",
                  modifier = Modifier.size(20.dp),
                  tint = MaterialTheme.colorScheme.secondary,
                )
              }
            }

            Text(
              quantity.toString(),
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onSurface,
            )
          }

          Surface(
            modifier = Modifier.size(32.dp),
            color = MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.small,
            onClick = onIncrease,
          ) {
            Box(contentAlignment = Alignment.Center) {
              Icon(
                Icons.Default.Add,
                contentDescription = "Tambah",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSecondary,
              )
            }
          }
        }
      } else if (quantity > 0) {
        Text(
          "${quantity}x",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.primary,
        )
      }
    }
  }
}
