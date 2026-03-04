package id.my.santosa.notagampang.ui.screen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.database.entity.DebtRecordEntity
import id.my.santosa.notagampang.viewmodel.KasbonViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KasbonScreen(
        viewModel: KasbonViewModel,
        onBack: () -> Unit = {},
        onViewNote: (Long) -> Unit = {}
) {
        val uiState by viewModel.uiState.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val whatsappPrompt by viewModel.whatsappPrompt.collectAsState()
        val context = LocalContext.current
        val currencyFormat = remember {
                val format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("id-ID"))
                format.maximumFractionDigits = 0
                format
        }
        val dateFormat = remember {
                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("id-ID"))
        }

        var selectedRecordForPayment by remember { mutableStateOf<DebtRecordEntity?>(null) }
        var paymentAmountStr by remember { mutableStateOf("") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        val focusManager = LocalFocusManager.current

        if (selectedRecordForPayment != null) {
                BackHandler {
                        focusManager.clearFocus()
                        selectedRecordForPayment = null
                }
        }

        if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                }
        } else {
                Column(modifier = Modifier.fillMaxSize()) {
                        PrimaryTabRow(
                                selectedTabIndex = uiState.selectedTab,
                                containerColor = MaterialTheme.colorScheme.surface,
                                contentColor = MaterialTheme.colorScheme.secondary,
                                indicator = {
                                        TabRowDefaults.PrimaryIndicator(
                                                modifier =
                                                        Modifier.tabIndicatorOffset(
                                                                uiState.selectedTab
                                                        ),
                                                width = 64.dp,
                                                color = MaterialTheme.colorScheme.secondary
                                        )
                                },
                                divider = {}
                        ) {
                                Tab(
                                        selected = uiState.selectedTab == 0,
                                        onClick = { viewModel.onTabSelected(0) },
                                        text = {
                                                Text(
                                                        text = "Aktif",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight =
                                                                if (uiState.selectedTab == 0)
                                                                        FontWeight.ExtraBold
                                                                else FontWeight.SemiBold,
                                                        color =
                                                                if (uiState.selectedTab == 0)
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                                else
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                )
                                        }
                                )
                                Tab(
                                        selected = uiState.selectedTab == 1,
                                        onClick = { viewModel.onTabSelected(1) },
                                        text = {
                                                Text(
                                                        text = "Selesai",
                                                        style =
                                                                MaterialTheme.typography
                                                                        .titleMedium,
                                                        fontWeight =
                                                                if (uiState.selectedTab == 1)
                                                                        FontWeight.ExtraBold
                                                                else FontWeight.SemiBold,
                                                        color =
                                                                if (uiState.selectedTab == 1)
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                                else
                                                                        MaterialTheme.colorScheme
                                                                                .onSurfaceVariant
                                                )
                                        }
                                )
                        }

                        Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { viewModel.onSearchQueryChange(it) },
                                        placeholder = { Text("Cari nama pelanggan...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        leadingIcon = {
                                                Icon(
                                                        androidx.compose.material.icons.Icons
                                                                .Default.Search,
                                                        contentDescription = null,
                                                        tint =
                                                                MaterialTheme.colorScheme
                                                                        .onSurfaceVariant
                                                )
                                        },
                                        singleLine = true,
                                        colors =
                                                OutlinedTextFieldDefaults.colors(
                                                        focusedContainerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant.copy(
                                                                        alpha = 0.5f
                                                                ),
                                                        unfocusedContainerColor =
                                                                MaterialTheme.colorScheme
                                                                        .surfaceVariant.copy(
                                                                        alpha = 0.5f
                                                                ),
                                                        focusedBorderColor =
                                                                MaterialTheme.colorScheme.primary,
                                                        unfocusedBorderColor =
                                                                androidx.compose.ui.graphics.Color
                                                                        .Transparent
                                                )
                                )
                        }

                        val currentDebts =
                                if (uiState.selectedTab == 0) uiState.activeDebts
                                else uiState.completedDebts

                        if (currentDebts.isEmpty()) {
                                Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Surface(
                                                modifier = Modifier.size(120.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.large
                                        ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                        Icon(
                                                                Icons.AutoMirrored.Filled
                                                                        .ReceiptLong,
                                                                contentDescription = null,
                                                                modifier = Modifier.size(48.dp),
                                                                tint =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                                if (uiState.selectedTab == 0)
                                                        "Tidak ada Kasbon aktif"
                                                else "Belum ada riwayat lunas",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                if (uiState.selectedTab == 0)
                                                        "Semua tagihan sudah lunas. Catatan kasbon baru akan muncul di sini jika ada pembayaran yang belum selesai."
                                                else
                                                        "Riwayat kasbon yang sudah lunas akan muncul di sini.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else {
                                LazyColumn(
                                        modifier = Modifier.fillMaxSize(),
                                        contentPadding = PaddingValues(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        items(currentDebts, key = { it.id }) { record ->
                                                Card(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        shape = MaterialTheme.shapes.medium,
                                                        colors =
                                                                CardDefaults.cardColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .surfaceVariant
                                                                                        .copy(
                                                                                                alpha =
                                                                                                        0.5f
                                                                                        )
                                                                ),
                                                        elevation =
                                                                CardDefaults.cardElevation(
                                                                        defaultElevation = 0.dp
                                                                )
                                                ) {
                                                        Column(modifier = Modifier.padding(16.dp)) {
                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        verticalAlignment =
                                                                                Alignment
                                                                                        .CenterVertically
                                                                ) {
                                                                        Surface(
                                                                                modifier =
                                                                                        Modifier.size(
                                                                                                40.dp
                                                                                        ),
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .errorContainer
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.2f
                                                                                                ),
                                                                                shape =
                                                                                        MaterialTheme
                                                                                                .shapes
                                                                                                .small
                                                                        ) {
                                                                                Box(
                                                                                        contentAlignment =
                                                                                                Alignment
                                                                                                        .Center
                                                                                ) {
                                                                                        Text(
                                                                                                "!",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .titleLarge,
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .ExtraBold,
                                                                                                color =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .error
                                                                                        )
                                                                                }
                                                                        }

                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.width(
                                                                                                16.dp
                                                                                        )
                                                                        )

                                                                        Column(
                                                                                modifier =
                                                                                        Modifier.weight(
                                                                                                1f
                                                                                        )
                                                                        ) {
                                                                                Text(
                                                                                        record.customerName,
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .titleMedium,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .Bold,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurface
                                                                                )
                                                                                Text(
                                                                                        dateFormat
                                                                                                .format(
                                                                                                        Date(
                                                                                                                record.timestamp
                                                                                                        )
                                                                                                ),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .labelSmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }

                                                                        Column(
                                                                                horizontalAlignment =
                                                                                        Alignment
                                                                                                .End
                                                                        ) {
                                                                                Text(
                                                                                        currencyFormat
                                                                                                .format(
                                                                                                        record.remainingDebt
                                                                                                ),
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .titleMedium,
                                                                                        fontWeight =
                                                                                                FontWeight
                                                                                                        .ExtraBold,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .error
                                                                                )
                                                                                Text(
                                                                                        "Sisa Tagihan",
                                                                                        style =
                                                                                                MaterialTheme
                                                                                                        .typography
                                                                                                        .labelSmall,
                                                                                        color =
                                                                                                MaterialTheme
                                                                                                        .colorScheme
                                                                                                        .onSurfaceVariant
                                                                                )
                                                                        }
                                                                }

                                                                if (!record.customerPhone
                                                                                .isNullOrBlank()
                                                                ) {
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                12.dp
                                                                                        )
                                                                        )
                                                                        Text(
                                                                                record.customerPhone,
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .bodySmall,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Medium,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .primary,
                                                                        )
                                                                }

                                                                val recordPayments =
                                                                        uiState.payments[record.id]
                                                                                ?: emptyList()
                                                                if (recordPayments.isNotEmpty()) {
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                16.dp
                                                                                        )
                                                                        )
                                                                        HorizontalDivider(
                                                                                thickness = 0.5.dp,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                                                .copy(
                                                                                                        alpha =
                                                                                                                0.2f
                                                                                                )
                                                                        )
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                8.dp
                                                                                        )
                                                                        )
                                                                        Text(
                                                                                "History Pembayaran:",
                                                                                style =
                                                                                        MaterialTheme
                                                                                                .typography
                                                                                                .labelMedium,
                                                                                fontWeight =
                                                                                        FontWeight
                                                                                                .Bold,
                                                                                color =
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                        )
                                                                        for (payment in
                                                                                recordPayments) {
                                                                                Row(
                                                                                        modifier =
                                                                                                Modifier.fillMaxWidth()
                                                                                                        .padding(
                                                                                                                vertical =
                                                                                                                        4.dp
                                                                                                        ),
                                                                                        horizontalArrangement =
                                                                                                Arrangement
                                                                                                        .SpaceBetween,
                                                                                        verticalAlignment =
                                                                                                Alignment
                                                                                                        .CenterVertically
                                                                                ) {
                                                                                        Text(
                                                                                                dateFormat
                                                                                                        .format(
                                                                                                                Date(
                                                                                                                        payment.timestamp
                                                                                                                )
                                                                                                        ),
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodySmall,
                                                                                                color =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .onSurfaceVariant
                                                                                        )
                                                                                        Text(
                                                                                                "+ ${currencyFormat.format(payment.amount)}",
                                                                                                style =
                                                                                                        MaterialTheme
                                                                                                                .typography
                                                                                                                .bodySmall,
                                                                                                fontWeight =
                                                                                                        FontWeight
                                                                                                                .Bold,
                                                                                                color =
                                                                                                        MaterialTheme
                                                                                                                .colorScheme
                                                                                                                .primary
                                                                                        )
                                                                                }
                                                                        }
                                                                }

                                                                Spacer(
                                                                        modifier =
                                                                                Modifier.height(
                                                                                        16.dp
                                                                                )
                                                                )

                                                                Row(
                                                                        modifier =
                                                                                Modifier.fillMaxWidth(),
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        if (uiState.selectedTab == 0
                                                                        ) {
                                                                                Row(
                                                                                        modifier =
                                                                                                Modifier.fillMaxWidth()
                                                                                                        .height(
                                                                                                                IntrinsicSize
                                                                                                                        .Min
                                                                                                        ),
                                                                                        horizontalArrangement =
                                                                                                Arrangement
                                                                                                        .spacedBy(
                                                                                                                8.dp
                                                                                                        )
                                                                                ) {
                                                                                        if (!record.customerPhone
                                                                                                        .isNullOrBlank()
                                                                                        ) {
                                                                                                OutlinedButton(
                                                                                                        modifier =
                                                                                                                Modifier.weight(
                                                                                                                                1f
                                                                                                                        )
                                                                                                                        .fillMaxHeight(),
                                                                                                        shape =
                                                                                                                MaterialTheme
                                                                                                                        .shapes
                                                                                                                        .medium,
                                                                                                        onClick = {
                                                                                                                val phone =
                                                                                                                        record.customerPhone
                                                                                                                val formattedPhone =
                                                                                                                        if (phone.startsWith(
                                                                                                                                        "0"
                                                                                                                                )
                                                                                                                        ) {
                                                                                                                                "62" +
                                                                                                                                        phone.drop(
                                                                                                                                                1
                                                                                                                                        )
                                                                                                                        } else {
                                                                                                                                phone
                                                                                                                        }
                                                                                                                val whatsappTemplate =
                                                                                                                        whatsappPrompt
                                                                                                                val message =
                                                                                                                        whatsappTemplate
                                                                                                                                .replace(
                                                                                                                                        "{nama}",
                                                                                                                                        record.customerName
                                                                                                                                )
                                                                                                                                .replace(
                                                                                                                                        "{tagihan}",
                                                                                                                                        currencyFormat
                                                                                                                                                .format(
                                                                                                                                                        record.remainingDebt
                                                                                                                                                )
                                                                                                                                ) +
                                                                                                                                id.my.santosa
                                                                                                                                        .notagampang
                                                                                                                                        .data
                                                                                                                                        .PreferenceManager
                                                                                                                                        .WHATSAPP_AUTOMATIC_FOOTER
                                                                                                                val intent =
                                                                                                                        Intent(
                                                                                                                                Intent.ACTION_VIEW
                                                                                                                        )
                                                                                                                intent.data =
                                                                                                                        Uri.parse(
                                                                                                                                "https://api.whatsapp.com/send?phone=$formattedPhone&text=${Uri.encode(message)}"
                                                                                                                        )
                                                                                                                context.startActivity(
                                                                                                                        intent
                                                                                                                )
                                                                                                        }
                                                                                                ) {
                                                                                                        Text(
                                                                                                                "Tagih Lewat WhatsApp",
                                                                                                                textAlign =
                                                                                                                        TextAlign
                                                                                                                                .Center
                                                                                                        )
                                                                                                }
                                                                                        }

                                                                                        Button(
                                                                                                modifier =
                                                                                                        Modifier.weight(
                                                                                                                        1f
                                                                                                                )
                                                                                                                .fillMaxHeight(),
                                                                                                shape =
                                                                                                        MaterialTheme
                                                                                                                .shapes
                                                                                                                .medium,
                                                                                                colors =
                                                                                                        ButtonDefaults
                                                                                                                .buttonColors(
                                                                                                                        containerColor =
                                                                                                                                MaterialTheme
                                                                                                                                        .colorScheme
                                                                                                                                        .secondary,
                                                                                                                        contentColor =
                                                                                                                                MaterialTheme
                                                                                                                                        .colorScheme
                                                                                                                                        .onSecondary
                                                                                                                ),
                                                                                                onClick = {
                                                                                                        selectedRecordForPayment =
                                                                                                                record
                                                                                                        paymentAmountStr =
                                                                                                                ""
                                                                                                }
                                                                                        ) {
                                                                                                Text(
                                                                                                        "Terima Pembayaran",
                                                                                                        textAlign =
                                                                                                                TextAlign
                                                                                                                        .Center
                                                                                                )
                                                                                        }
                                                                                }
                                                                        }
                                                                }

                                                                if (record.groupId != null) {
                                                                        Spacer(
                                                                                modifier =
                                                                                        Modifier.height(
                                                                                                8.dp
                                                                                        )
                                                                        )
                                                                        TextButton(
                                                                                modifier =
                                                                                        Modifier.fillMaxWidth(),
                                                                                onClick = {
                                                                                        onViewNote(
                                                                                                record.groupId
                                                                                        )
                                                                                },
                                                                                shape =
                                                                                        MaterialTheme
                                                                                                .shapes
                                                                                                .medium
                                                                        ) {
                                                                                Icon(
                                                                                        Icons.Default
                                                                                                .Visibility,
                                                                                        contentDescription =
                                                                                                null
                                                                                )
                                                                                Spacer(
                                                                                        modifier =
                                                                                                Modifier.width(
                                                                                                        8.dp
                                                                                                )
                                                                                )
                                                                                Text("Lihat Nota")
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                }
                        }
                }

                if (selectedRecordForPayment != null) {
                        ModalBottomSheet(
                                onDismissRequest = { selectedRecordForPayment = null },
                                sheetState = sheetState,
                                containerColor = MaterialTheme.colorScheme.surface,
                                dragHandle = { BottomSheetDefaults.DragHandle() },
                        ) {
                                Column(
                                        modifier =
                                                Modifier.fillMaxWidth()
                                                        .padding(
                                                                start = 24.dp,
                                                                end = 24.dp,
                                                                bottom = 48.dp
                                                        )
                                                        .imePadding()
                                ) {
                                        Text(
                                                "Terima Pembayaran",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                "Sisa tagihan: ${currencyFormat.format(selectedRecordForPayment!!.remainingDebt)}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        OutlinedTextField(
                                                value = paymentAmountStr,
                                                onValueChange = {
                                                        if (it.all { char -> char.isDigit() })
                                                                paymentAmountStr = it
                                                },
                                                label = { Text("Jumlah Dibayar") },
                                                prefix = { Text("Rp ") },
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Number
                                                        ),
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = MaterialTheme.shapes.medium
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                OutlinedButton(
                                                        onClick = {
                                                                selectedRecordForPayment = null
                                                        },
                                                        modifier = Modifier.weight(1f),
                                                        shape = MaterialTheme.shapes.medium
                                                ) { Text("Batal") }
                                                Button(
                                                        onClick = {
                                                                val amount =
                                                                        paymentAmountStr
                                                                                .toIntOrNull()
                                                                                ?: 0
                                                                if (amount > 0) {
                                                                        viewModel
                                                                                .receiveInstallment(
                                                                                        selectedRecordForPayment!!,
                                                                                        amount
                                                                                )
                                                                }
                                                                selectedRecordForPayment = null
                                                        },
                                                        enabled = (paymentAmountStr.toIntOrNull()
                                                                        ?: 0) > 0,
                                                        modifier = Modifier.weight(1f),
                                                        shape = MaterialTheme.shapes.medium,
                                                        colors =
                                                                ButtonDefaults.buttonColors(
                                                                        containerColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .secondary,
                                                                        contentColor =
                                                                                MaterialTheme
                                                                                        .colorScheme
                                                                                        .onSecondary
                                                                )
                                                ) { Text("Simpan") }
                                        }
                                }
                        }
                }
        }
}
