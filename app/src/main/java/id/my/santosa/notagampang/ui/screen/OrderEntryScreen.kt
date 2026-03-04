package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.ui.component.order.MenuItemRow
import id.my.santosa.notagampang.ui.util.CurrencyUtil.formatCurrency
import id.my.santosa.notagampang.viewmodel.OrderEntryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrderEntryScreen(
  viewModel: OrderEntryViewModel,
  isReadOnly: Boolean = false,
  onCheckout: () -> Unit,
) {
  val uiState by viewModel.uiState.collectAsState()

  Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
    Box(modifier = Modifier.fillMaxSize()) {
      Column(modifier = Modifier.fillMaxSize().imePadding()) {
        Row(
          modifier = Modifier.padding(20.dp).fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Column {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
              Text(
                text = "Nota #${uiState.group?.id ?: ""}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Bold,
              )
              if (isReadOnly) {
                val statusText =
                  if (uiState.group?.status == "Kasbon") {
                    "KASBON"
                  } else {
                    "SUDAH DIBAYAR"
                  }
                val statusColor =
                  if (uiState.group?.status == "Kasbon") {
                    MaterialTheme.colorScheme.error
                  } else {
                    MaterialTheme.colorScheme.primary
                  }

                Surface(
                  color =
                    statusColor.copy(
                      alpha = 0.1f,
                    ),
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
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                  )
                }
              }
            }
            uiState.group?.let { group ->
              val dateFormat =
                remember {
                  SimpleDateFormat(
                    "dd MMM yyyy • HH:mm",
                    Locale.forLanguageTag(
                      "id-ID",
                    ),
                  )
                }
              Text(
                text =
                  dateFormat.format(
                    Date(
                      group.createdAt,
                    ),
                  ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
          }
        }

        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 20.dp),
          color =
            MaterialTheme.colorScheme.outlineVariant.copy(
              alpha = 0.3f,
            ),
        )

        val categories = uiState.categories
        val groupedItems = uiState.menuItems.groupBy { it.category }

        LazyColumn(
          modifier = Modifier.weight(1f),
          contentPadding =
            PaddingValues(
              start = 20.dp,
              top = 0.dp,
              end = 20.dp,
              bottom = 120.dp,
            ),
          verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
          categories.forEach { cat ->
            val itemsInCategory = groupedItems[cat] ?: emptyList()
            if (itemsInCategory.isNotEmpty()) {
              item {
                Text(
                  cat,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = MaterialTheme.colorScheme.primary,
                  modifier =
                    Modifier.padding(
                      top = 16.dp,
                      bottom = 8.dp,
                    ),
                )
              }
              items(itemsInCategory, key = { it.id }) { menu ->
                val order = uiState.currentOrders.find { it.menuItemId == menu.id }
                val quantity = order?.quantity ?: 0

                MenuItemRow(
                  name = menu.name,
                  price = menu.price,
                  quantity = quantity,
                  isReadOnly = isReadOnly,
                  onIncrease = {
                    viewModel.addItemToOrder(
                      menu,
                    )
                  },
                  onDecrease = {
                    viewModel.removeItemFromOrder(
                      menu.id,
                    )
                  },
                )
              }
            }
          }
        }
      }

      // Custom Bottom Bar for Checkout
      val total = uiState.currentOrders.sumOf { it.priceAtOrder * it.quantity }
      val totalItems = uiState.currentOrders.sumOf { it.quantity }
      Surface(
        tonalElevation = 0.dp,
        modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
      ) {
        Column {
          HorizontalDivider(
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
          )
          Row(
            modifier =
              Modifier.fillMaxWidth()
                .padding(
                  horizontal = 20.dp,
                  vertical = 16.dp,
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Column {
              Text(
                "Total",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
              Text(
                formatCurrency(total),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.ExtraBold,
              )
              Text(
                "$totalItems items",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
              )
            }
            if (!isReadOnly) {
              Button(
                onClick = onCheckout,
                enabled = uiState.currentOrders.isNotEmpty(),
                shape = MaterialTheme.shapes.medium,
                colors =
                  ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary,
                  ),
                contentPadding =
                  PaddingValues(
                    horizontal = 32.dp,
                    vertical = 12.dp,
                  ),
              ) {
                Text(
                  "Bayar",
                  fontWeight = FontWeight.Bold,
                )
              }
            }
          }
        }
      }
    }
  }
}
