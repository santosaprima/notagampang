package id.my.santosa.notagampang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.repository.CustomerGroupRepository
import id.my.santosa.notagampang.ui.screen.FloatingTabsScreen
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModel
import id.my.santosa.notagampang.viewmodel.FloatingTabsViewModelFactory

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // In a real app we would use Dagger/Hilt. For this MVP we instantiate directly.
    val database = AppDatabase.getDatabase(this)
    val repository = CustomerGroupRepository(database.customerGroupDao(), database.orderItemDao())

    setContent {
      MaterialTheme {
        Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
        ) {
          val floatingTabsViewModel: FloatingTabsViewModel =
                  viewModel(factory = FloatingTabsViewModelFactory(repository))

          FloatingTabsScreen(
                  viewModel = floatingTabsViewModel,
                  onTabClick = { groupId ->
                    // TODO: Navigate to Order Entry Screen for this groupId!
                    // For example: navController.navigate("order_entry/$groupId")
                  }
          )
        }
      }
    }
  }
}
