package eth.social.sefer.ui.view.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eth.social.sefer.R
import eth.social.sefer.databinding.SettingPostsBinding
import eth.social.sefer.helpers.ApplicationHelper

class PostsSettingFragment : Fragment() {
  private var _binding: SettingPostsBinding? = null

  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = SettingPostsBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    ApplicationHelper.setUpToolBar(view, R.id.tool_bar, this)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}