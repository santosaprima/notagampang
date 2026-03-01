package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
        currencyFormat.maximumFractionDigits = 0

        Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                Text(
                        "Rangkuman Pendapatan",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor =
                                                MaterialTheme.colorScheme.surfaceVariant.copy(
                                                        alpha = 0.5f
                                                )
                                )
                ) {
                        Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                Text(
                                        "Pembayaran Lunas: ${currencyFormat.format(uiState.totalPaidIncome)}"
                                )
                                Text(
                                        "Pembayaran Kasbon: ${currencyFormat.format(uiState.totalKasbonIncome)}"
                                )
                                Text(
                                        "Total Uang Masuk: ${currencyFormat.format(uiState.totalIncome)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                )
                        }
                }

                Text(
                        "Rangkuman Kasbon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                )

                Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                )
                ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                        "Total Kasbon Aktif: ${currencyFormat.format(uiState.totalActiveKasbon)}",
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Bold
                                )
                        }
                }

                Button(
                        onClick = { showConfirmDialog = true },
                        modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                        colors =
                                ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                )
                ) { Text("Tutup Kasir (Bersihkan Antrean)") }
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
