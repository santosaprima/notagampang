package id.my.santosa.notagampang.ui.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtil {
  private val locale = Locale.forLanguageTag("id-ID")
  private val currencyFormat =
    NumberFormat.getCurrencyInstance(locale).apply { maximumFractionDigits = 0 }

  fun formatCurrency(amount: Int): String {
    return currencyFormat.format(amount)
  }
}
