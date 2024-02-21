package eth.social.sefer.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import eth.social.sefer.MyApp
import eth.social.sefer.callbacks.OnLoadMoreListener
import eth.social.sefer.ui.adapters.vh.ShortVideoVH
import eth.social.sefer.ui.vm.ShortVideoVM

@OptIn(UnstableApi::class)
class SwipeVideoPlayerView : RecyclerView {
  enum class VolumeState { ON, OFF }

  // ui
  private var videoPlayer: ExoPlayer? = null
  private lateinit var volumeState: VolumeState
  private var prevPosition: Int = -1
  var loadMore: OnLoadMoreListener? = null

  // vars
  private var pausedByUser = false
  var vm: ShortVideoVM? = null
  private lateinit var pauseIcon: ImageView
  private lateinit var loading: ProgressBar

  constructor(context: Context) : super(context) {
    init()
  }

  constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
    init()
  }

  private fun init() {
    // Bind the player to the view.
    setVolumeControl(VolumeState.ON, 1.0f)

    // event
    addOnScrollListener(object : OnScrollListener() {
      override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == SCROLL_STATE_IDLE) {
          // There's a special case when the end of the list has been reached.
          // Need to handle that with this bit of logic
          if (!recyclerView.canScrollVertically(LinearLayoutManager.VERTICAL)) {
            loadMore?.loadMore()
          }

//          playVideo()
        }
      }
    })

    addOnChildAttachStateChangeListener(object : OnChildAttachStateChangeListener {
      override fun onChildViewAttachedToWindow(view: View) {
        if (prevPosition == -1) {
          playVideo()
        }
      }

      override fun onChildViewDetachedFromWindow(view: View) {}
    })
  }

  private fun setVolumeControl(state: VolumeState, volume: Float) {
    volumeState = state
    videoPlayer?.let { it.volume = volume }
  }

  private fun playVideo() {
    val lm: LinearLayoutManager = layoutManager as LinearLayoutManager
    val currentVisiblePosition: Int = lm.findFirstCompletelyVisibleItemPosition()

    // video is already playing so return
    if (currentVisiblePosition < 0 || prevPosition == currentVisiblePosition) {
      return
    }

    // prev player should be dismissed
    if (prevPosition != -1) {
      releasePlayer()
    }

    val child: View = lm.getChildAt(0)!!
    val holder: ShortVideoVH = child.tag as ShortVideoVH
    videoPlayer = holder.initVideoView()
    pauseIcon = holder.binding.pauseResume
    loading = holder.binding.loading
    holder.binding.videoView.setOnClickListener(videoViewClickListener)

    videoPlayer?.setMediaSource(getMediaSource(holder.videoUrl))
    videoPlayer?.seekTo(0, 0)
    videoPlayer?.volume = if (isVolumeStateMuted()) 0.0f else 1.0f

    prevPosition = currentVisiblePosition
  }

  private fun getMediaSource(uri: String): ProgressiveMediaSource {
    val userAgent = Util.getUserAgent(context, "ExoPlayer")

    return ProgressiveMediaSource.Factory(
      CacheDataSource.Factory().setCache(MyApp.simpleCache).setUpstreamDataSourceFactory(
        DefaultHttpDataSource.Factory().setUserAgent(userAgent)
      ).setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
    ).createMediaSource(MediaItem.fromUri(uri))
  }

  fun releasePlayer() {
    videoPlayer?.release()
    videoPlayer = null
  }

  fun startPlaying() {
    if (!pausedByUser) {
      videoPlayer?.play()
    }
  }

  fun pauseVideo() {
    videoPlayer?.pause()
  }

  fun toggleVolume() {
    if (isVolumeStateMuted()) {
      setVolumeControl(VolumeState.ON, 1.0f)
    } else if (volumeState == VolumeState.ON) {
      setVolumeControl(VolumeState.OFF, 0.0f)
    }
  }

  fun isVolumeStateMuted(): Boolean {
    return volumeState == VolumeState.OFF
  }

  private val videoViewClickListener: OnClickListener = OnClickListener {
    videoPlayer?.let { vPlayer ->
      pauseIcon.visibility = if (vPlayer.isPlaying) {
        vPlayer.pause()
        View.VISIBLE
      } else {
        vPlayer.play()
        View.GONE
      }
      loading.isVisible = false

      pausedByUser = !vPlayer.isPlaying
    }
  }
}
