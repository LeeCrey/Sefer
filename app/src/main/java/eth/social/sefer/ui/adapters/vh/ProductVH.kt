package eth.social.sefer.ui.adapters.vh

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.data.models.Product
import eth.social.sefer.databinding.LProductBinding

class ProductVH(root: View) : RecyclerView.ViewHolder(root) {
    private lateinit var binding: LProductBinding

    constructor(binding: LProductBinding) : this(binding.root) {
        this.binding = binding
    }

    // if it is search layout attach only image.
    fun bindView(product: Product, glide: RequestManager) {
        binding.apply {
            productName.text = product.name

            glide.load(product.thumbNail)
                .centerCrop()
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.photo_load_error)
                .into(productImage)
        }

        product.rate?.let {
            binding.ratingsValue.rating = product.rate
        }

        // calculate discount
//        newPrice.text = itemView.context.getString(R.string.readable_price, product.price)
    }
}