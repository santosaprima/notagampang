package id.my.santosa.notagampang.ui.util

import android.os.Build
import android.window.OnBackInvokedCallback
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

/**
 * A BackHandler that uses PRIORITY_OVERLAY on API 33+ to intercept back gestures before the IME
 * (soft keyboard) consumes them.
 */
@Composable
fun PriorityBackHandler(enabled: Boolean = true, onBack: () -> Unit) {
    val currentOnBack by rememberUpdatedState(onBack)
    val context = LocalContext.current

    // For API < 33, use standard BackHandler
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        BackHandler(enabled = enabled, onBack = currentOnBack)
        return
    }

    val activity =
            remember(context) {
                var c = context
                while (c is android.content.ContextWrapper) {
                    if (c is ComponentActivity) break
                    c = c.baseContext
                }
                c as? ComponentActivity
            }

    val backCallback = remember { OnBackInvokedCallback { currentOnBack() } }

    DisposableEffect(activity, enabled) {
        if (enabled && activity != null) {
            val dispatcher = activity.onBackInvokedDispatcher
            dispatcher.registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_OVERLAY,
                    backCallback
            )
            onDispose { dispatcher.unregisterOnBackInvokedCallback(backCallback) }
        } else {
            onDispose {}
        }
    }
}
