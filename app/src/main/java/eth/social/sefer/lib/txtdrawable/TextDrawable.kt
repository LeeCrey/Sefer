package eth.social.sefer.lib.txtdrawable

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import java.util.Locale
import kotlin.math.min

// Basically from https://github.com/amulyakhare/TextDrawable
// Changed to kotlin in Jan 15, 2024
// @author Solomon Boloshe(Lee Crey)
class TextDrawable private constructor(builder: Builder) : ShapeDrawable(builder.shape) {
  private val textPaint: Paint = Paint()
  private val borderPaint: Paint
  private val text: String
  private val color: Int = builder.color
  private val shape: RectShape = builder.shape
  private val height: Int = builder.height
  private val width: Int = builder.width
  private val fontSize: Int = builder.fontSize
  private val radius: Float = builder.radius
  private val borderThickness: Int

  init {
    // text and color
    text =
      if (builder.toUpperCase) builder.text.uppercase(Locale.getDefault()) else builder.text

    // text paint settings
    textPaint.color = builder.textColor
    textPaint.isAntiAlias = true
    textPaint.isFakeBoldText = builder.isBold
    textPaint.style = Paint.Style.FILL
    textPaint.setTypeface(builder.font)
    textPaint.textAlign = Paint.Align.CENTER
    textPaint.strokeWidth = builder.borderThickness.toFloat()

    // border paint settings
    borderThickness = builder.borderThickness
    borderPaint = Paint()
    borderPaint.color = getDarkerShade(color)
    borderPaint.style = Paint.Style.STROKE
    borderPaint.strokeWidth = borderThickness.toFloat()

    // drawable paint color
    val paint = paint
    paint.color = color
  }

  private fun getDarkerShade(color: Int): Int {
    return Color.rgb(
      (SHADE_FACTOR * Color.red(color)).toInt(),
      (SHADE_FACTOR * Color.green(color)).toInt(),
      (SHADE_FACTOR * Color.blue(color)).toInt()
    )
  }

  override fun draw(canvas: Canvas) {
    super.draw(canvas)
    val r = bounds

    // draw border
    if (borderThickness > 0) {
      drawBorder(canvas)
    }
    val count = canvas.save()
    canvas.translate(r.left.toFloat(), r.top.toFloat())

    // draw text
    val width = if (width < 0) r.width() else width
    val height = if (height < 0) r.height() else height
    val fontSize = if (fontSize < 0) min(width, height) / 2 else fontSize
    textPaint.textSize = fontSize.toFloat()
    canvas.drawText(
      text,
      (width / 2).toFloat(),
      height / 2 - (textPaint.descent() + textPaint.ascent()) / 2,
      textPaint
    )
    canvas.restoreToCount(count)
  }

  private fun drawBorder(canvas: Canvas) {
    val rect = RectF(bounds)
    rect.inset((borderThickness / 2).toFloat(), (borderThickness / 2).toFloat())
    when (shape) {
      is OvalShape -> canvas.drawOval(rect, borderPaint)
      is RoundRectShape -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
      else -> canvas.drawRect(rect, borderPaint)
    }
  }

  override fun setAlpha(alpha: Int) {
    textPaint.alpha = alpha
  }

  override fun setColorFilter(cf: ColorFilter?) {
    textPaint.setColorFilter(cf)
  }

  @Deprecated(
    "Deprecated in Java",
    ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
  )
  override fun getOpacity(): Int {
    return PixelFormat.TRANSLUCENT
  }

  override fun getIntrinsicWidth(): Int {
    return width
  }

  override fun getIntrinsicHeight(): Int {
    return height
  }

  class Builder : IConfigBuilder, IShapeBuilder, IBuilder {
    var text = ""
    var color: Int
    var borderThickness = 0
    var width: Int
    var height: Int
    var font: Typeface
    var shape: RectShape
    var textColor: Int
    var fontSize: Int
    var isBold = false
    var toUpperCase = false
    var radius = 0f

    init {
      color = Color.GRAY
      textColor = Color.WHITE
      width = -1
      height = -1
      shape = RectShape()
      font = Typeface.create("sans-serif-light", Typeface.NORMAL)
      fontSize = -1
    }

    override fun width(width: Int): IConfigBuilder {
      this.width = width
      return this
    }

    override fun height(height: Int): IConfigBuilder {
      this.height = height
      return this
    }

    override fun textColor(color: Int): IConfigBuilder {
      textColor = color
      return this
    }

    override fun withBorder(thickness: Int): IConfigBuilder {
      borderThickness = thickness
      return this
    }

    override fun useFont(font: Typeface): IConfigBuilder {
      this.font = font
      return this
    }

    override fun fontSize(size: Int): IConfigBuilder {
      fontSize = size
      return this
    }

    override fun bold(): IConfigBuilder {
      isBold = true
      return this
    }

    override fun toUpperCase(): IConfigBuilder {
      toUpperCase = true
      return this
    }

    override fun beginConfig(): IConfigBuilder {
      return this
    }

    override fun endConfig(): IShapeBuilder {
      return this
    }

    override fun rect(): IBuilder {
      shape = RectShape()
      return this
    }

    override fun round(): IBuilder {
      shape = OvalShape()
      return this
    }

    override fun roundRect(radius: Int): IBuilder {
      this.radius = radius.toFloat()
      val radii = floatArrayOf(
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat(),
        radius.toFloat()
      )
      shape = RoundRectShape(radii, null, null)
      return this
    }

    override fun buildRect(text: String, color: Int): TextDrawable {
      rect()
      return build(text, color)
    }

    override fun buildRoundRect(text: String, color: Int, radius: Int): TextDrawable {
      roundRect(radius)
      return build(text, color)
    }

    override fun buildRound(text: String, color: Int): TextDrawable {
      round()
      return build(text, color)
    }

    override fun build(text: String, color: Int): TextDrawable {
      this.color = color
      this.text = text
      return TextDrawable(this)
    }
  }

  interface IConfigBuilder {
    fun width(width: Int): IConfigBuilder
    fun height(height: Int): IConfigBuilder
    fun textColor(color: Int): IConfigBuilder
    fun withBorder(thickness: Int): IConfigBuilder
    fun useFont(font: Typeface): IConfigBuilder
    fun fontSize(size: Int): IConfigBuilder
    fun bold(): IConfigBuilder
    fun toUpperCase(): IConfigBuilder
    fun endConfig(): IShapeBuilder
  }

  interface IBuilder {
    fun build(text: String, color: Int): TextDrawable
  }

  interface IShapeBuilder {
    fun beginConfig(): IConfigBuilder
    fun rect(): IBuilder
    fun round(): IBuilder
    fun roundRect(radius: Int): IBuilder
    fun buildRect(text: String, color: Int): TextDrawable
    fun buildRoundRect(text: String, color: Int, radius: Int): TextDrawable
    fun buildRound(text: String, color: Int): TextDrawable
  }

  companion object {
    private const val SHADE_FACTOR = 0.9f

    fun builder(): IShapeBuilder {
      return Builder()
    }
  }
}