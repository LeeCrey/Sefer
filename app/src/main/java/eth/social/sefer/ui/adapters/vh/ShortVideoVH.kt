package eth.social.sefer.ui.adapters.vh

import android.icu.text.CompactDecimalFormat
import android.view.View
import androidx.annotation.OptIn
import androidx.core.view.isVisible
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import eth.social.sefer.MyApp
import eth.social.sefer.R
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.databinding.LShortVideoBinding
import eth.social.sefer.helpers.ToastUtility


@OptIn(UnstableApi::class)
open class ShortVideoVH(
  itemView: View
) : RecyclerView.ViewHolder(itemView) {
  lateinit var binding: LShortVideoBinding
  var player: ExoPlayer? = null
  lateinit var videoUrl: String

  constructor(binding: LShortVideoBinding) : this(binding.root) {
    this.binding = binding
  }

  fun bindView(
    video: ShortVideo,
    manager: RequestManager,
    formatter: CompactDecimalFormat,
  ) {
    binding.apply {
      if (video.numberOfComments != 0) {
        numberOfComments.text = formatter.format(video.numberOfComments)
      }
      if (video.likesCount != 0) {
        numberOfLikes.text = formatter.format(video.likesCount)
      }

      userFullName.text = video.user.fullName
      userName.text = video.user.username
      if (video.user.verified) {
        badge.visibility = View.VISIBLE
      }
    }
    videoUrl = video.url!!
    manager.load(video.user.profileUrl)
      .placeholder(R.drawable.profile_placeholder)
      .into(binding.userProfilePicture)
  }

  fun initVideoView(): ExoPlayer? {
    player = ExoPlayer.Builder(MyApp.instance).build().also { exoPlayer ->
      exoPlayer.prepare()
      exoPlayer.playWhenReady = true
      exoPlayer.seekTo(0, 0)
    }
    binding.videoView.player = player!!

    player?.addListener(object : Player.Listener {
      override fun onPlayerError(error: PlaybackException) {
        ToastUtility.showToast(R.string.video_play_error, itemView.context)
//                val cause = error.cause
//                if (cause is HttpDataSource.HttpDataSourceException) {
//                    cause.message?.let { msg ->
//                        MyAppUtility.showCustomToast(
//                            msg,
//                            itemView.context
//                        )
//                    }
//                }
      }

      override fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
          Player.STATE_ENDED -> player?.seekTo(0)
          Player.STATE_READY -> {
            binding.loading.isVisible = false
          }

          else -> {}
        }
      }

      override fun onIsPlayingChanged(isPlaying: Boolean) {
        binding.loading.isVisible = !isPlaying
      }
    })

    return player
  }
}

