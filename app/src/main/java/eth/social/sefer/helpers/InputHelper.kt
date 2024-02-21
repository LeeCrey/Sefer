package eth.social.sefer.helpers

import android.content.Context
import android.util.Patterns
import androidx.core.content.res.ResourcesCompat
import com.bumptech.glide.Glide
import eth.social.sefer.R
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.image.ImagesPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin


object InputHelper {

  @JvmStatic
  fun checkInput(data: String, context: Context): String? {
    return if (data.trim().isEmpty()) {
      context.getString(R.string.required)
    } else null
  }

  @JvmStatic
  fun checkEmail(email: String, context: Context): String? {
    return checkInput(email, context)
      ?: if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        null
      } else context.getString(R.string.invalid_email)
  }

  fun buildMarkWon(context: Context): Markwon {
    val typeface = ResourcesCompat.getFont(context, R.font.roboto_regular)
    val heading = ResourcesCompat.getFont(context, R.font.roboto_bold)
//    val color = context.getColor(android.R.color.codeBackground)
//    val txtColor = context.getColor(android.R.color.codeText)
    return Markwon.builder(context)
      .usePlugin(CorePlugin.create())
      .usePlugin(object : AbstractMarkwonPlugin() {
        override fun configureTheme(builder: MarkwonTheme.Builder) {
//          builder.codeTextColor(txtColor).codeBackgroundColor(color)
          if (typeface != null) {
            builder.codeTypeface(typeface)
          }
          if (heading != null) {
            builder.headingTypeface(heading)
          }
        }
      })
      .usePlugin(ImagesPlugin.create())
//      .usePlugin(HtmlPlugin.create())
      .usePlugin(LinkifyPlugin.create())
      .usePlugin(GlideImagesPlugin.create(Glide.with(context)))
      .usePlugin(StrikethroughPlugin.create())
//      .usePlugin(TaskListPlugin.create(context))
      .build()
  }
}