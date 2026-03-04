package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
        bottomPadding: androidx.compose.ui.unit.Dp = 0.dp,
        onTabClick: (Long, Boolean) -> Unit
) {
        val filteredGroups by viewModel.filteredGroups.collectAsState()
        val searchQuery by viewModel.searchQuery.collectAsState()
        val selectedTab by viewModel.selectedTab.collectAsState()
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

                                Spacer(modifier = Modifier.height(24.dp))

                                val tabs = listOf("Aktif", "Selesai")
                                PrimaryTabRow(
                                        selectedTabIndex = selectedTab,
                                        containerColor =
                                                androidx.compose.ui.graphics.Color.Transparent,
                                        contentColor = MaterialTheme.colorScheme.secondary,
                                        divider = {},
                                        indicator = {
                                                TabRowDefaults.PrimaryIndicator(
                                                        modifier =
                                                                Modifier.tabIndicatorOffset(
                                                                        selectedTab
                                                                ),
                                                        width = 64.dp,
                                                        color = MaterialTheme.colorScheme.secondary
                                                )
                                        }
                                ) {
                                        tabs.forEachIndexed { index, title ->
                                                Tab(
                                                        selected = selectedTab == index,
                                                        onClick = {
                                                                viewModel.onTabSelected(index)
                                                        },
                                                        text = {
                                                                Text(
                                                                        text = title,
                                                                        style =
                                                                                MaterialTheme
                                                                                        .typography
                                                                                        .titleMedium,
                                                                        fontWeight =
                                                                                if (selectedTab ==
                                                                                                index
                                                                                )
                                                                                        FontWeight
                                                                                                .ExtraBold
                                                                                else
                                                                                        FontWeight
                                                                                                .SemiBold,
                                                                        color =
                                                                                if (selectedTab ==
                                                                                                index
                                                                                )
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .secondary
                                                                                else
                                                                                        MaterialTheme
                                                                                                .colorScheme
                                                                                                .onSurfaceVariant
                                                                )
                                                        }
                                                )
                                        }
                                }

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
                                modifier = Modifier.offset(y = (-30).dp - bottomPadding),
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor =
                                        MaterialTheme.colorScheme.onSecondary, // White text/icon
                                shape = MaterialTheme.shapes.large,
                                content = {
                                        Icon(Icons.Default.Add, contentDescription = "Tambah Nota")
                                }
                        )
                },
                contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                        if (filteredGroups.isEmpty() && searchQuery.isEmpty()) {
                                Column(
                                        modifier =
                                                Modifier.fillMaxSize()
                                                        .padding(
                                                                start = 32.dp,
                                                                end = 32.dp,
                                                                top = 32.dp,
                                                                bottom = 140.dp
                                                        ),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
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
                                                if (selectedTab == 0) "Belum ada nota aktif"
                                                else "Belum ada nota selesai",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                                if (selectedTab == 0)
                                                        "Ketuk tombol + di pojok bawah untuk membuat nota baru bagi pelanggan Anda."
                                                else
                                                        "Nota yang sudah dibayar lunas akan muncul di sini.",
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.Center,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                }
                        } else if (filteredGroups.isEmpty() && searchQuery.isNotEmpty()) {
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
                                        contentPadding =
                                                PaddingValues(
                                                        start = 20.dp,
                                                        top = 20.dp,
                                                        end = 20.dp,
                                                        bottom = bottomPadding + 20.dp
                                                ),
                                        verticalArrangement = Arrangement.spacedBy(12.dp),
                                        modifier = Modifier.fillMaxSize()
                                ) {
                                        items(
                                                items = filteredGroups,
                                                key = { item: CustomerGroupWithTotal ->
                                                        item.group.id
                                                }
                                        ) { groupWithTotal: CustomerGroupWithTotal ->
                                                NotaCard(
                                                        groupWithTotal = groupWithTotal,
                                                        onClick = {
                                                                onTabClick(
                                                                        groupWithTotal.group.id,
                                                                        groupWithTotal
                                                                                .group
                                                                                .status == "Paid" ||
                                                                                groupWithTotal
                                                                                        .group
                                                                                        .status ==
                                                                                        "Kasbon"
                                                                )
                                                        }
                                                )
                                        }
                                }
                        }
                }
        }

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        if (showAddDialog) {
                ModalBottomSheet(
                        onDismissRequest = {
                                showAddDialog = false
                                newGroupName = ""
                        },
                        sheetState = sheetState,
                        containerColor = MaterialTheme.colorScheme.surface,
                        dragHandle = { BottomSheetDefaults.DragHandle() },
                ) {
                        Column(
                                modifier =
                                        Modifier.fillMaxWidth()
                                                .padding(start = 24.dp, end = 24.dp, bottom = 48.dp)
                                                .imePadding()
                        ) {
                                Column(
                                        modifier =
                                                Modifier.weight(1f, fill = false)
                                                        .verticalScroll(rememberScrollState())
                                ) {
                                        Text(
                                                "Tambah Nota Baru",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                        OutlinedTextField(
                                                value = newGroupName,
                                                onValueChange = { newGroupName = it },
                                                label = { Text("Nama Nota (misal: Meja 1)") },
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .heightIn(max = 120.dp),
                                                shape = MaterialTheme.shapes.medium,
                                                singleLine = false,
                                                minLines = 3,
                                                maxLines = 3
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Text(
                                                "Pilihan Cepat:",
                                                style = MaterialTheme.typography.labelLarge,
                                                color = MaterialTheme.colorScheme.primary
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        val chunkedSuggestions =
                                                suggestions.chunked((suggestions.size + 2) / 3)
                                        Box(
                                                modifier =
                                                        Modifier.fillMaxWidth()
                                                                .horizontalScroll(
                                                                        rememberScrollState()
                                                                )
                                        ) {
                                                Column(
                                                        verticalArrangement =
                                                                Arrangement.spacedBy(8.dp)
                                                ) {
                                                        chunkedSuggestions.forEach { rowSuggestions
                                                                ->
                                                                Row(
                                                                        horizontalArrangement =
                                                                                Arrangement
                                                                                        .spacedBy(
                                                                                                8.dp
                                                                                        )
                                                                ) {
                                                                        rowSuggestions.forEach {
                                                                                suggestion ->
                                                                                FilterChip(
                                                                                        selected =
                                                                                                false,
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
                                                                                        label = {
                                                                                                Text(
                                                                                                        suggestion
                                                                                                )
                                                                                        },
                                                                                        shape =
                                                                                                MaterialTheme
                                                                                                        .shapes
                                                                                                        .medium
                                                                                )
                                                                        }
                                                                }
                                                        }
                                                }
                                        }
                                        Spacer(modifier = Modifier.height(32.dp))
                                }

                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        OutlinedButton(
                                                onClick = {
                                                        showAddDialog = false
                                                        newGroupName = ""
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = MaterialTheme.shapes.medium
                                        ) { Text("Batal") }
                                        Button(
                                                onClick = {
                                                        if (newGroupName.isNotBlank()) {
                                                                viewModel.createNewTab(newGroupName)
                                                                showAddDialog = false
                                                                newGroupName = ""
                                                        }
                                                },
                                                enabled = newGroupName.isNotBlank(),
                                                modifier = Modifier.weight(1f),
                                                shape = MaterialTheme.shapes.medium,
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor =
                                                                        MaterialTheme.colorScheme
                                                                                .secondary,
                                                                contentColor =
                                                                        MaterialTheme.colorScheme
                                                                                .onSecondary
                                                        )
                                        ) { Text("Buat Nota") }
                                }
                        }
                }
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
                                        maxLines = 3,
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
                                                else ->
                                                        MaterialTheme.colorScheme
                                                                .onSecondaryContainer
                                        }
                                val containerColor =
                                        when (groupWithTotal.group.status) {
                                                "Paid" ->
                                                        MaterialTheme.colorScheme.primary.copy(
                                                                alpha = 0.1f
                                                        )
                                                "Kasbon" ->
                                                        MaterialTheme.colorScheme.error.copy(
                                                                alpha = 0.1f
                                                        )
                                                else ->
                                                        MaterialTheme.colorScheme.secondaryContainer
                                                                .copy(alpha = 0.7f)
                                        }
                                Surface(
                                        color = containerColor,
                                        shape = MaterialTheme.shapes.extraSmall
                                ) {
                                        Text(
                                                statusText,
                                                modifier =
                                                        Modifier.padding(
                                                                horizontal = 6.dp,
                                                                vertical = 2.dp
                                                        ),
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = statusColor
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
