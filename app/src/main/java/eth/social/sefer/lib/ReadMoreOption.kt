package eth.social.sefer.lib

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.TextView


class ReadMoreOption private constructor(builder: Builder) {
  // optional
  private val textLength: Int = builder.textLength
  private val moreLabel: String = builder.moreLabel
  private val lessLabel: String = builder.lessLabel
  private val moreLabelColor: Int = builder.moreLabelColor
  private val lessLabelColor: Int = builder.lessLabelColor
  private val labelUnderLine: Boolean = builder.labelUnderLine
  private val moreButton = "...\n\n$moreLabel"
  private val txtEnd = textLength + moreButton.length
  private val txtStart = txtEnd - moreLabel.length

  fun addReadMoreTo(textView: TextView, text: String) {
    if (text.length <= textLength) {
      textView.text = text
      return
    }
    val ss = SpannableString(text.substring(0, textLength) + moreButton)
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
      override fun onClick(view: View) {
        addReadLess(textView, text)
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

  private fun addReadLess(textView: TextView, text: String) {
    val ss = SpannableString("$text $lessLabel")
    val clickableSpan: ClickableSpan = object : ClickableSpan() {
      override fun onClick(view: View) {
        // TODO remove with coroutine
        Handler().post { addReadMoreTo(textView, text) }
      }

      override fun updateDrawState(ds: TextPaint) {
        super.updateDrawState(ds)
        ds.isUnderlineText = labelUnderLine
        ds.color = lessLabelColor
      }
    }
    ss.setSpan(
      clickableSpan,
      ss.length - lessLabel.length,
      ss.length,
      Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    textView.text = ss
    textView.movementMethod = LinkMovementMethod.getInstance()
  }

  class Builder(val context: Context) {
    // optional
    var textLength = 100
    var moreLabel = "More"
    var lessLabel = "Less"
    var moreLabelColor = Color.parseColor("#ff00ff")
    var lessLabelColor = Color.parseColor("#ff00ff")
    var labelUnderLine = false
    fun textLength(length: Int): Builder {
      textLength = length
      return this
    }

    fun moreLabel(moreLabel: String): Builder {
      this.moreLabel = moreLabel
      return this
    }

    fun lessLabel(lessLabel: String): Builder {
      this.lessLabel = lessLabel
      return this
    }

    fun moreLabelColor(moreLabelColor: Int): Builder {
      this.moreLabelColor = moreLabelColor
      return this
    }

    fun lessLabelColor(lessLabelColor: Int): Builder {
      this.lessLabelColor = lessLabelColor
      return this
    }

    fun labelUnderLine(labelUnderLine: Boolean): Builder {
      this.labelUnderLine = labelUnderLine
      return this
    }

    fun build(): ReadMoreOption {
      return ReadMoreOption(this)
    }
  }
}
