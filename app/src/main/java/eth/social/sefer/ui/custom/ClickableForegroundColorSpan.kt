package eth.social.sefer.ui.custom

import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView

/**
 * Created by danylo.volokh on 12/22/2015.
 * This class is a combination of [android.text.style.ForegroundColorSpan]
 * and [ClickableSpan].
 *
 * You can set a color of this span plus set a click listener
 *
 * Part of obsolete project. Converted to kotlin by Solomon Boloshe Jan 23, 2024
 */
class ClickableForegroundColorSpan(
  private val mColor: Int,
  private val mOnHashTagClickListener: OnHashTagClickListener?
) : ClickableSpan() {
  interface OnHashTagClickListener {
    fun onHashTagClicked(hashTag: String?)
  }

  init {
    if (mOnHashTagClickListener == null) {
      throw RuntimeException("constructor, click listener not specified. Are you sure you need to use this class?")
    }
  }

  override fun updateDrawState(ds: TextPaint) {
    ds.color = mColor
  }

  override fun onClick(widget: View) {
    val text = (widget as TextView).text
    val s = text as Spanned
    val start = s.getSpanStart(this)
    val end = s.getSpanEnd(this)
    mOnHashTagClickListener!!.onHashTagClicked(
      text.subSequence(start + 1 /*skip "#" sign*/, end).toString()
    )
  }
}
