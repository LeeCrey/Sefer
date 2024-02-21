package eth.social.sefer.ui.custom

import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.widget.TextView

/**
 * This is a helper class that should be used with [android.widget.EditText] or [TextView]
 * In order to have hash-tagged words highlighted. It also provides a click listeners for every hashtag
 *
 * Example :
 * #ThisIsHashTagWord
 * #ThisIsFirst#ThisIsSecondHashTag
 * #hashtagendsifitfindsnotletterornotdigitsignlike_thisIsNotHighlithedArea
 *
 */
class HashTagHelper
private constructor(
  private val mHashTagWordColor: Int,
  listener: OnHashTagClickListener?,
  additionalHashTagCharacters: List<Char>?
) : ClickableForegroundColorSpan.OnHashTagClickListener {
  /**
   * If this is not null then  all of the symbols in the List will be considered as valid symbols of hashtag
   * For example :
   * mAdditionalHashTagChars = {'$','_','-'}
   * it means that hashtag: "#this_is_hashtag-with$dollar-sign" will be highlighted.
   *
   * Note: if mAdditionalHashTagChars would be "null" only "#this" would be highlighted
   *
   */
  private var mAdditionalHashTagChars: MutableList<Char> = mutableListOf()
  private var mTextView: TextView? = null
  private val mOnHashTagClickListener: OnHashTagClickListener?

  init {
    mOnHashTagClickListener = listener
    if (additionalHashTagCharacters != null) {
      mAdditionalHashTagChars = mutableListOf()
      mAdditionalHashTagChars.addAll(additionalHashTagCharacters)
    }
  }

  object Creator {
    fun create(color: Int, listener: OnHashTagClickListener?): HashTagHelper {
      return HashTagHelper(color, listener, null)
    }

    fun create(
      color: Int,
      listener: OnHashTagClickListener,
      additionalHashTagChars: List<Char>
    ): HashTagHelper {
      return HashTagHelper(color, listener, additionalHashTagChars)
    }
  }

  interface OnHashTagClickListener {
    fun onHashTagClicked(hashTag: String?)
  }

  private val mTextWatcher: TextWatcher = object : TextWatcher {
    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
      if (text.isNotEmpty()) {
        eraseAndColorizeAllText(text)
      }
    }

    override fun afterTextChanged(s: Editable) {}
  }

  fun handle(textView: TextView?) {
    if (mTextView == null) {
      mTextView = textView
      mTextView!!.addTextChangedListener(mTextWatcher)

      // in order to use spannable we have to set buffer type
      mTextView!!.setText(mTextView!!.text, TextView.BufferType.SPANNABLE)
      if (mOnHashTagClickListener != null) {
        // we need to set this in order to get onClick event
        mTextView!!.movementMethod = LinkMovementMethod.getInstance()

        // after onClick clicked text become highlighted
        mTextView!!.highlightColor = Color.TRANSPARENT
      } else {
        // hash tags are not clickable, no need to change these parameters
      }
      setColorsToAllHashTags(mTextView!!.text)
    } else {
      throw RuntimeException("TextView is not null. You need to create a unique HashTagHelper for every TextView")
    }
  }

  private fun eraseAndColorizeAllText(text: CharSequence) {
    val spannable = mTextView!!.text as Spannable
    val spans = spannable.getSpans(0, text.length, CharacterStyle::class.java)
    for (span in spans) {
      spannable.removeSpan(span)
    }
    setColorsToAllHashTags(text)
  }

  private fun setColorsToAllHashTags(text: CharSequence) {
    var startIndexOfNextHashSign: Int
    var index = 0
    while (index < text.length - 1) {
      val sign = text[index]
      var nextNotLetterDigitCharIndex =
        index + 1 // we assume it is next. if if was not changed by findNextValidHashTagChar then index will be incremented by 1
      if (sign == '#') {
        startIndexOfNextHashSign = index
        nextNotLetterDigitCharIndex = findNextValidHashTagChar(text, startIndexOfNextHashSign)
        setColorForHashTagToTheEnd(startIndexOfNextHashSign, nextNotLetterDigitCharIndex)
      }
      index = nextNotLetterDigitCharIndex
    }
  }

  private fun findNextValidHashTagChar(text: CharSequence, start: Int): Int {
    var nonLetterDigitCharIndex = -1 // skip first sign '#"
    for (index in start + 1 until text.length) {
      val sign = text[index]
      val isValidSign = Character.isLetterOrDigit(sign) || mAdditionalHashTagChars.contains(sign)
      if (!isValidSign) {
        nonLetterDigitCharIndex = index
        break
      }
    }
    if (nonLetterDigitCharIndex == -1) {
      // we didn't find non-letter. We are at the end of text
      nonLetterDigitCharIndex = text.length
    }
    return nonLetterDigitCharIndex
  }

  private fun setColorForHashTagToTheEnd(startIndex: Int, nextNotLetterDigitCharIndex: Int) {
    val s = mTextView!!.text as Spannable
    val span: CharacterStyle = if (mOnHashTagClickListener != null) {
      ClickableForegroundColorSpan(mHashTagWordColor, this)
    } else {
      // no need for clickable span because it is messing with selection when click
      ForegroundColorSpan(mHashTagWordColor)
    }
    s.setSpan(span, startIndex, nextNotLetterDigitCharIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
  }

  override fun onHashTagClicked(hashTag: String?) {
    mOnHashTagClickListener!!.onHashTagClicked(hashTag)
  }
}
