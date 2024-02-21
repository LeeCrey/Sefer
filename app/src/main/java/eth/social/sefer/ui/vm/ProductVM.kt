package eth.social.sefer.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.sefer.data.models.Product
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.ProductRepo
import kotlinx.coroutines.flow.SharedFlow

open class ProductVM : ViewModel() {
  private val repo = ProductRepo(viewModelScope)
  val products: LiveData<List<Product>> = repo.products
  val response: SharedFlow<ApiResponse> = repo.response

  init {
    loadProducts(null, false)
  }

  fun loadProducts(page: Int?, append: Boolean) = repo.loadProducts(page, append)

  fun loadMore() = repo.loadMore()

  fun isNextAvailable(): Boolean = repo.isNextAvailable()
}