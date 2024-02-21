package eth.social.sefer.ui.view.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eth.social.sefer.R
import eth.social.sefer.callbacks.ProfileCallBack
import eth.social.sefer.data.models.ShortVideo
import eth.social.sefer.helpers.ToastUtility

class ProfileVideosFragment : VideosFragment() {
  private var callback: ProfileCallBack? = null

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View = inflater.inflate(R.layout.f_list, container, false)

  override fun initRefreshLayout(isProfile: Boolean) {
    super.initRefreshLayout(true)
  }

  override fun onAttach(context: Context) {
    super.onAttach(context)

    try {
      callback = parentFragment as ProfileCallBack
    } catch (msg: Exception) {
      msg.printStackTrace()
      ToastUtility.showToast(msg.message!!, requireContext())
    }
  }

  override fun videoClicked(video: ShortVideo) {
    callback?.openVideoShow(video)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    callback = null
  }
}