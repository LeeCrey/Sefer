package eth.social.sefer.lib

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView
import eth.social.sefer.R

class ResourceReadMore(
  context: Context,
  private val callback: ReadMore
) {
  // optional
  private val textLength: Int = 300
  private val moreLabel: String = context.getString(R.string.read_more)
  private val moreLabelColor: Int = context.getColor(R.color.link)
  private val labelUnderLine: Boolean = true
  private val moreString = "...\n\n$moreLabel"
  private val txtEnd = textLength + moreString.length
  private val txtStart = txtEnd - moreLabel.length

  fun addReadMoreTo(textView: TextView, text: String, position: Int) {
    if (text.length <= textLength) {
      textView.text = text
      return
    }
    val ss = SpannableString(text.substring(0, textLength) + moreString)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
      override fun onClick(view: View) {
        callback.openDetailActivity(position)
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = labelUnderLine
        ds.color = moreLabelColor
      }
    }
    ss.setSpan(clickableSpan, txtStart, txtEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    textView.text = ss
    textView.movementMethod = LinkMovementMethod.getInstance()
  }

  interface ReadMore {
    fun openDetailActivity(position: Int)
  }
}