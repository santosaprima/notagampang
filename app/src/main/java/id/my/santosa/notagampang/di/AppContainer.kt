package id.my.santosa.notagampang.di

import android.content.Context
import id.my.santosa.notagampang.data.PreferenceManager
import id.my.santosa.notagampang.database.AppDatabase
import id.my.santosa.notagampang.repository.CategoryRepositoryImpl
import id.my.santosa.notagampang.repository.CustomerGroupRepositoryImpl
import id.my.santosa.notagampang.repository.DebtRecordRepositoryImpl
import id.my.santosa.notagampang.repository.ICategoryRepository
import id.my.santosa.notagampang.repository.ICustomerGroupRepository
import id.my.santosa.notagampang.repository.IDebtRecordRepository
import id.my.santosa.notagampang.repository.IMenuItemRepository
import id.my.santosa.notagampang.repository.IOrderRepository
import id.my.santosa.notagampang.repository.ISuggestionPresetRepository
import id.my.santosa.notagampang.repository.MenuItemRepositoryImpl
import id.my.santosa.notagampang.repository.OrderRepositoryImpl
import id.my.santosa.notagampang.repository.SuggestionPresetRepositoryImpl

interface AppContainer {
  val customerGroupRepository: ICustomerGroupRepository
  val menuItemRepository: IMenuItemRepository
  val orderRepository: IOrderRepository
  val debtRecordRepository: IDebtRecordRepository
  val categoryRepository: ICategoryRepository
  val suggestionPresetRepository: ISuggestionPresetRepository
  val preferenceManager: PreferenceManager
}

class AppDataContainer(private val context: Context) : AppContainer {
  private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }

  override val customerGroupRepository: ICustomerGroupRepository by lazy {
    CustomerGroupRepositoryImpl(database.customerGroupDao(), database.orderItemDao())
  }

  override val menuItemRepository: IMenuItemRepository by lazy {
    MenuItemRepositoryImpl(database.menuItemDao())
  }

  override val orderRepository: IOrderRepository by lazy {
    OrderRepositoryImpl(database.orderItemDao())
  }

  override val debtRecordRepository: IDebtRecordRepository by lazy {
    DebtRecordRepositoryImpl(database.debtRecordDao(), database.debtPaymentDao())
  }

  override val categoryRepository: ICategoryRepository by lazy {
    CategoryRepositoryImpl(database.categoryDao(), database.menuItemDao())
  }

  override val suggestionPresetRepository: ISuggestionPresetRepository by lazy {
    SuggestionPresetRepositoryImpl(database.suggestionPresetDao())
  }

  override val preferenceManager: PreferenceManager by lazy { PreferenceManager(context) }
}
