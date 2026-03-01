package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.viewmodel.ShiftManagementViewModel
import java.text.NumberFormat
import java.util.*

@Composable
fun ShiftManagementScreen(
        viewModel: ShiftManagementViewModel,
        onBack: () -> Unit, // Add onBack back
        onShiftClosed: () -> Unit
) {
        val uiState by viewModel.uiState.collectAsState()
        var showConfirmDialog by remember { mutableStateOf(false) }

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
        currencyFormat.setMaximumFractionDigits(0)

        Column(
                modifier = Modifier.fillMaxSize().padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
                Column {
                        Text(
                                "Rangkuman Pendapatan",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                                "Rincian pemasukan dari semua transaksi",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                                        alpha = 0.5f
                                                )
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                        Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                "Pembayaran Lunas",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                                currencyFormat.format(uiState.totalPaidIncome),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                }

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                        Text(
                                                "Pembayaran Kasbon",
                                                style = MaterialTheme.typography.bodyLarge,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                                currencyFormat.format(uiState.totalKasbonIncome),
                                                style = MaterialTheme.typography.bodyLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                }

                                HorizontalDivider(
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        color =
                                                MaterialTheme.colorScheme.outlineVariant.copy(
                                                        alpha = 0.3f
                                                )
                                )

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                ) {
                                        Text(
                                                "Total Uang Masuk",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                                currencyFormat.format(uiState.totalIncome),
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.secondary
                                        )
                                }
                        }
                }

                Column {
                        Text(
                                "Rangkuman Kasbon",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                                "Total tagihan pelanggan yang belum lunas",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                }

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.errorContainer.copy(
                                                        alpha = 0.2f
                                                )
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                        Row(
                                modifier = Modifier.padding(20.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(
                                        "Total Kasbon Aktif",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Text(
                                        currencyFormat.format(uiState.totalActiveKasbon),
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.error
                                )
                        }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.error.copy(alpha = 0.8f),
                                        contentColor = MaterialTheme.colorScheme.onError
                                ),
                        contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                        Text(
                                "Tutup Kasir (Selesaikan Shift)",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                        )
                }
        }

        if (showConfirmDialog) {
                AlertDialog(
                        onDismissRequest = { showConfirmDialog = false },
                        title = { Text("Konfirmasi Tutup Kasir") },
                        text = {
                                Text(
                                        "Menutup kasir akan mengosongkan semua nota dan pesanan yang sudah lunas dari layar utama. " +
                                                "Tagihan dan menu tidak akan dihapus. Yakin lanjut?"
                                )
                        },
                        confirmButton = {
                                Button(
                                        onClick = {
                                                viewModel.closeShift {
                                                        showConfirmDialog = false
                                                        onShiftClosed()
                                                }
                                        },
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor =
                                                                MaterialTheme.colorScheme.error
                                                )
                                ) { Text("Ya, Tutup Kasir") }
                        },
                        dismissButton = {
                                TextButton(onClick = { showConfirmDialog = false }) {
                                        Text("Batal")
                                }
                        }
                )
        }
}
