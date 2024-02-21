package eth.social.sefer.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.callbacks.MarketCallBack
import eth.social.sefer.data.models.Product
import eth.social.sefer.databinding.LProductBinding
import eth.social.sefer.ui.adapters.vh.ProductVH


class ProductAdapter(
  context: Context,
  private val callBack: MarketCallBack
) : ListAdapter<Product, ProductVH>(ProductItemCallBack()) {
  private val inflater: LayoutInflater = LayoutInflater.from(context)
  private val glide: RequestManager = Glide.with(context)

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
    val binding = LProductBinding.inflate(inflater, parent, false)

    val vh = ProductVH(binding)

    // event listener
    vh.itemView.setOnClickListener {
      callBack.openDetailScreen(getItem(vh.bindingAdapterPosition))
    }

    return vh
  }

  override fun onBindViewHolder(holder: ProductVH, position: Int) {
    val product = getItem(position)
    if (product.id == -1L) {
      callBack.loadMore()
    } else {
      holder.bindView(product, glide)
    }
  }

  class ProductItemCallBack : DiffUtil.ItemCallback<Product>() {
    override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
      return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
      return oldItem.isContentTheSame(newItem)
    }
  }
}