package eth.social.sefer

import android.app.Application
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import java.io.File


class MyApp : Application() {

  @UnstableApi
  override fun onCreate() {
    super.onCreate()

    instance = this

    leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(exoCacheSize)
    standaloneDatabaseProvider = StandaloneDatabaseProvider(this)
    simpleCache = SimpleCache(
      File(applicationContext.cacheDir, "media"),
      leastRecentlyUsedCacheEvictor,
      standaloneDatabaseProvider
    )
  }

  companion object {
    lateinit var simpleCache: SimpleCache
    lateinit var leastRecentlyUsedCacheEvictor: LeastRecentlyUsedCacheEvictor
    lateinit var standaloneDatabaseProvider: StandaloneDatabaseProvider
    const val exoCacheSize: Long = 100 * 1024 * 1024 // Setting cache size to be ~ 100 MB

    lateinit var instance: MyApp
      private set
  }
}