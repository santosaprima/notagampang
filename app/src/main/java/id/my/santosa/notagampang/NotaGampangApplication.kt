package id.my.santosa.notagampang

import android.app.Application
import id.my.santosa.notagampang.di.AppContainer
import id.my.santosa.notagampang.di.AppDataContainer

class NotaGampangApplication : Application() {
  lateinit var container: AppContainer

  override fun onCreate() {
    super.onCreate()
    container = AppDataContainer(this)
  }
}
