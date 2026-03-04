package id.my.santosa.notagampang.ui.util

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

/**
 * A wrapper around BackHandler that only activates when the keyboard is closed. This ensures the
 * soft keyboard closes first before the application intercepts the back gesture.
 */
@Composable
fun PriorityBackHandler(
  enabled: Boolean = true,
  onBack: () -> Unit,
) {
  val isKeyboardVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
  // Only enable the handler if the keyboard is NOT visible.
  // This allows the system to handle the first back gesture to close the keyboard.
  BackHandler(enabled = enabled && !isKeyboardVisible, onBack = onBack)
}
