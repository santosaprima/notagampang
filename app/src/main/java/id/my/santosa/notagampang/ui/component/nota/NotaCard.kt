package id.my.santosa.notagampang.ui.component.nota

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import id.my.santosa.notagampang.ui.util.CurrencyUtil.formatCurrency
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NotaCard(
  groupWithTotal: CustomerGroupWithTotal,
  onClick: () -> Unit,
) {
  val dateFormat =
    remember {
      SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.forLanguageTag("id-ID"))
    }

  Card(
    onClick = onClick,
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
      modifier = Modifier.padding(16.dp).fillMaxWidth().height(IntrinsicSize.Min),
      verticalAlignment = Alignment.Top,
    ) {
      Box(
        modifier =
          Modifier.size(40.dp)
            .background(
              MaterialTheme.colorScheme.primary,
              MaterialTheme.shapes.small,
            ),
        contentAlignment = Alignment.Center,
      ) {
        Icon(
          Icons.AutoMirrored.Filled.ReceiptLong,
          contentDescription = null,
          modifier = Modifier.size(20.dp),
          tint = MaterialTheme.colorScheme.secondary,
        )
      }

      Spacer(modifier = Modifier.width(16.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = "Nota #${groupWithTotal.group.id}",
          style = MaterialTheme.typography.labelMedium,
          color = MaterialTheme.colorScheme.secondary,
          fontWeight = FontWeight.Bold,
        )
        Text(
          text = groupWithTotal.group.alias,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onSurface,
          maxLines = 3,
          overflow = TextOverflow.Ellipsis,
        )
        Text(
          text = formatCurrency(groupWithTotal.totalAmount),
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.ExtraBold,
          color = MaterialTheme.colorScheme.secondary,
        )
        Text(
          text = "${groupWithTotal.itemCount} items",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Column(
        modifier = Modifier.fillMaxHeight(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.SpaceBetween,
      ) {
        val statusText =
          when (groupWithTotal.group.status) {
            "Paid" -> "SELESAI"
            "Kasbon" -> "KASBON"
            else -> "AKTIF"
          }
        val statusColor =
          when (groupWithTotal.group.status) {
            "Paid" -> MaterialTheme.colorScheme.primary
            "Kasbon" -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSecondaryContainer
          }
        val containerColor =
          when (groupWithTotal.group.status) {
            "Paid" ->
              MaterialTheme.colorScheme.primary.copy(
                alpha = 0.1f,
              )
            "Kasbon" ->
              MaterialTheme.colorScheme.error.copy(
                alpha = 0.1f,
              )
            else -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
          }
        Surface(
          color = containerColor,
          shape = MaterialTheme.shapes.extraSmall,
        ) {
          Text(
            statusText,
            modifier =
              Modifier.padding(
                horizontal = 6.dp,
                vertical = 2.dp,
              ),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = statusColor,
          )
        }

        Text(
          text =
            dateFormat.format(
              Date(groupWithTotal.group.createdAt),
            ),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.End,
        )
      }
    }
  }
}
