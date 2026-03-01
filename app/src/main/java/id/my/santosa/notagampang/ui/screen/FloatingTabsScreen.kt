package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.repository.CustomerGroupWithTotal
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FloatingTabsScreen(
        viewModel: FloatingTabsViewModel,
        suggestions: List<String>,
        onTabClick: (Long) -> Unit
) {
        val activeGroups by viewModel.activeGroups.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        var showAddDialog by remember { mutableStateOf(false) }
        var newGroupName by remember { mutableStateOf("") }

        Scaffold(
                topBar = {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                                Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        Surface(
                                                modifier = Modifier.size(36.dp),
                                                color = MaterialTheme.colorScheme.primary,
                                                shape = MaterialTheme.shapes.small
                                        ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                        Text(
                                                                "N",
                                                                style =
                                                                        MaterialTheme.typography
                                                                                .titleLarge,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                color =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary // Amber
                                                                // Gold
                                                                )
                                                }
                                        }
                                        Text(
                                                "NotaGampang",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                        )
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Text(
                                        "Nota Aktif",
                                        style = MaterialTheme.typography.displaySmall,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                        "Kelola semua grup dan pesanan aktif di sini",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(20.dp))

                                OutlinedTextField(
                                        value = searchQuery,
                                        onValueChange = { viewModel.onSearchQueryChange(it) },
                                        placeholder = { Text("Cari nota...") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = MaterialTheme.shapes.medium,
                                        leadingIcon = {
                                                Icon(
                                                        Icons.Default.Search,
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
                },
                floatingActionButton = {
                        FloatingActionButton(
                                onClick = { showAddDialog = true },
                                modifier = Modifier.offset(y = 20.dp),
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.secondary, // Amber Gold
                                shape = MaterialTheme.shapes.large
                        ) { Icon(Icons.Default.Add, contentDescription = "Tambah Nota") }
                }
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        if (activeGroups.isEmpty() && searchQuery.isEmpty()) {
                                Column(
                                        modifier = Modifier.fillMaxSize().padding(32.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                ) {
                                        Surface(
                                                modifier = Modifier.size(120.dp),
                                                color =
                                                        MaterialTheme.colorScheme.primaryContainer
                                                                .copy(alpha = 0.3f),
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
                                                                                .primary
                                                        )
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(24.dp))
                                        Text(
                                                "Belum ada nota aktif",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                "Ketuk tombol + di pojok bawah untuk membuat nota baru bagi pelanggan Anda.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else if (activeGroups.isEmpty() && searchQuery.isNotEmpty()) {
                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                "Tidak ditemukan: \"$searchQuery\"",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else {
                                LazyColumn(
                                        contentPadding = PaddingValues(20.dp),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        items(
                                                items = activeGroups,
                                                key = { item: CustomerGroupWithTotal ->
                                                        item.group.id
                                                }
                                        ) { groupWithTotal: CustomerGroupWithTotal ->
                                                NotaCard(
                                                        groupWithTotal = groupWithTotal,
                                                        onClick = {
                                                                onTabClick(groupWithTotal.group.id)
                                                        }
                                                )
                                        }
                                }
                        }
                }
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
                                                shape = MaterialTheme.shapes.medium
                                        )

                                        Text(
                                                "Pilihan Cepat:",
                                                style = MaterialTheme.typography.labelLarge
                                        )

                                        FlowRow(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                suggestions.forEach { suggestion ->
                                                        FilterChip(
                                                                selected = false,
                                                                onClick = {
                                                                        newGroupName =
                                                                                if (newGroupName
                                                                                                .isEmpty()
                                                                                )
                                                                                        suggestion
                                                                                else if (newGroupName
                                                                                                .endsWith(
                                                                                                        " "
                                                                                                )
                                                                                )
                                                                                        "$newGroupName$suggestion"
                                                                                else
                                                                                        "$newGroupName $suggestion"
                                                                },
                                                                label = { Text(suggestion) },
                                                                shape = MaterialTheme.shapes.medium
                                                        )
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
                                        },
                                        shape = MaterialTheme.shapes.medium
                                ) { Text("Buat Nota") }
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

        val dateFormat = remember {
                SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.forLanguageTag("id-ID"))
        }

        Card(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors =
                        CardDefaults.cardColors(
                                containerColor =
                                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
                Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth().height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.Top
                ) {
                        Box(
                                modifier =
                                        Modifier.size(40.dp)
                                                .background(
                                                        MaterialTheme.colorScheme.primary,
                                                        MaterialTheme.shapes.small
                                                ),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ReceiptLong,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp),
                                        tint = MaterialTheme.colorScheme.secondary
                                )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = "Nota #${groupWithTotal.group.id}",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.secondary,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        text = groupWithTotal.group.alias,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                        text = currencyFormat.format(groupWithTotal.totalAmount),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = MaterialTheme.colorScheme.secondary
                                )
                                Text(
                                        text = "${groupWithTotal.itemCount} items",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                        }

                        Column(
                                modifier = Modifier.fillMaxHeight(),
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.SpaceBetween
                        ) {
                                Surface(
                                        color =
                                                MaterialTheme.colorScheme.secondaryContainer.copy(
                                                        alpha = 0.7f
                                                ),
                                        shape = MaterialTheme.shapes.extraSmall
                                ) {
                                        Text(
                                                "AKTIF",
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 6.dp,
                                                                vertical = 2.dp
                                                        ),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color =
                                                        MaterialTheme.colorScheme
                                                                .onSecondaryContainer
                                        )
                                }

                                Text(
                                        text =
                                                dateFormat.format(
                                                        Date(groupWithTotal.group.createdAt)
                                                ),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.End
                                )
                        }
                }
        }
}
