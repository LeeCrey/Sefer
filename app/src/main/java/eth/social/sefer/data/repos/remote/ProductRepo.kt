package eth.social.sefer.data.repos.remote

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.sefer.data.apis.ProductApi
import eth.social.sefer.data.models.Product
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.RetrofitConnectionUtil.retrofitInstance
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProductRepo(
  private val viewModelScope: CoroutineScope
) : ApplicationRepo() {
  private val api: ProductApi = retrofitInstance.create(ProductApi::class.java)
  private val mProducts: MutableLiveData<List<Product>> = MutableLiveData()
  val products: LiveData<List<Product>> get() = mProducts

  fun loadProducts(page: Int?, append: Boolean) {
    val handler = CoroutineExceptionHandler { _, t ->
      viewModelScope.launch {
        setApiResponse(t, null)
      }
    }

    viewModelScope.launch(Dispatchers.IO + handler) {
      if (!isInternetAvailable()) {
        delay(700)
      } else {
        delay(200)
      }

      val response = api.loadProducts(authorization, page)
      if (response.isSuccessful) {
        meta = response.body()?.meta
        mProducts.postValue(response.body()?.products)
      } else {
        val error = parseRequestResponse(response.errorBody()!!).apply {
          okay = false
          unauthorized = response.code() == unauthorizedCode
        }

        mResponse.emit(error)
      }
    }
  }

  fun loadMore() {
    val page: Int = meta?.page ?: 1
    loadProducts(page + 1, true)
  }
}



