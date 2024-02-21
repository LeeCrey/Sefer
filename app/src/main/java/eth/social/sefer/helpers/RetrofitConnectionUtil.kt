package eth.social.sefer.helpers

import android.content.Context
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import eth.social.sefer.BuildConfig
import eth.social.sefer.Constants
import eth.social.sefer.MyApp
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit


object RetrofitConnectionUtil {
  private const val HEADER_CACHE_CONTROL = "Cache-Control"
  private const val HEADER_PRAGMA = "Pragma"
  private const val TAG = "ServiceGenerator"
  private const val cacheSize = (30 * 1024 * 1024).toLong()// 30 MB
  private var retrofit: Retrofit? = null

  @get:Synchronized
  val retrofitInstance: Retrofit
    get() {
      if (null == retrofit) {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
          .followRedirects(false)
          .connectTimeout(30, TimeUnit.SECONDS)
          .cache(cache(MyApp.instance))
          .addInterceptor(offlineInterceptor())
          .addNetworkInterceptor(networkInterceptor()) // only used when network is on

        if (BuildConfig.DEBUG) {
          builder.addInterceptor(httpLoggingInterceptor()) // used if network off OR on
        }

        val client = builder.build()
        val mapper = ObjectMapper().enable(SerializationFeature.WRAP_ROOT_VALUE)
        mapper.registerModule(JavaTimeModule())

        retrofit = Retrofit.Builder()
          .baseUrl(Constants.DOMAIN)
          .addConverterFactory(JacksonConverterFactory.create(mapper))
          .client(client).build()
      }

      return retrofit!!
    }

  private fun offlineInterceptor(): Interceptor {
    return Interceptor { chain: Interceptor.Chain ->
      var request = chain.request()

      if (!isInternetAvailable()) {
        val cacheControl: CacheControl = CacheControl.Builder()
          .maxStale(7, TimeUnit.DAYS)
          .build()
        request = request.newBuilder()
          .removeHeader(HEADER_PRAGMA)
          .removeHeader(HEADER_CACHE_CONTROL)
          .cacheControl(cacheControl)
          .build()
      }
      chain.proceed(request)
    }
  }

  private fun networkInterceptor(): Interceptor {
    return Interceptor { chain: Interceptor.Chain ->
      val response = chain.proceed(chain.request())
      val cacheControl: CacheControl = CacheControl.Builder()
        .maxAge(5, TimeUnit.SECONDS)
        .build()
      response.newBuilder()
        .removeHeader(HEADER_PRAGMA)
        .removeHeader(HEADER_CACHE_CONTROL)
        .header(HEADER_CACHE_CONTROL, cacheControl.toString())
        .build()
    }
  }

  private fun httpLoggingInterceptor(): HttpLoggingInterceptor {
    val httpLoggingInterceptor =
      HttpLoggingInterceptor { message: String -> Log.d(TAG, "log: http log: $message") }
    httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    return httpLoggingInterceptor
  }

  private fun cache(application: Context): Cache {
    return Cache(File(application.cacheDir, "sefer_cache_file"), cacheSize)
  }
}