package eth.social.sefer.callbacks

import eth.social.sefer.data.models.Product


interface MarketCallBack : OnLoadMoreListener {
  fun openDetailScreen(product: Product)
}