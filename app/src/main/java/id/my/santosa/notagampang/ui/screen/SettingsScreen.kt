package id.my.santosa.notagampang.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import id.my.santosa.notagampang.data.ThemeMode
import id.my.santosa.notagampang.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
        val currentTheme by viewModel.themeMode.collectAsState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text(
                        text = "Tema Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
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
                        Column(modifier = Modifier.selectableGroup()) {
                                ThemeOption(
                                        mode = ThemeMode.LIGHT,
                                        selected = currentTheme == ThemeMode.LIGHT,
                                        onSelect = { viewModel.setThemeMode(ThemeMode.LIGHT) },
                                        icon = Icons.Default.BrightnessHigh,
                                        label = "Terang"
                                )
                                HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color =
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.1f
                                                )
                                )
                                ThemeOption(
                                        mode = ThemeMode.DARK,
                                        selected = currentTheme == ThemeMode.DARK,
                                        onSelect = { viewModel.setThemeMode(ThemeMode.DARK) },
                                        icon = Icons.Default.Brightness4,
                                        label = "Gelap"
                                )
                                HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        color =
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                        alpha = 0.1f
                                                )
                                )
                                ThemeOption(
                                        mode = ThemeMode.SYSTEM,
                                        selected = currentTheme == ThemeMode.SYSTEM,
                                        onSelect = { viewModel.setThemeMode(ThemeMode.SYSTEM) },
                                        icon = Icons.Default.BrightnessAuto,
                                        label = "Ikuti Sistem"
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Versi Info
                Text(
                        text = "Informasi Aplikasi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                        text = "NotaGampang v1.0.0",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                )
        }
}

@Composable
fun ThemeOption(
        mode: ThemeMode,
        selected: Boolean,
        onSelect: () -> Unit,
        icon: ImageVector,
        label: String
) {
        Row(
                modifier =
                        Modifier.fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                        selected = selected,
                                        onClick = onSelect,
                                        role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
                Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint =
                                if (selected) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                        text = label,
                        style = MaterialTheme.typography.bodyLarge,
                        color =
                                if (selected) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                )
                RadioButton(
                        selected = selected,
                        onClick = null, // handled by row selectable
                        colors =
                                RadioButtonDefaults.colors(
                                        selectedColor = MaterialTheme.colorScheme.secondary
                                )
                )
        }
}
