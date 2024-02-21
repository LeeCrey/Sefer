package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import eth.social.sefer.databinding.FUserInfoBinding

class InfoFragment : Fragment() {
  private var _binding: FUserInfoBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FUserInfoBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}