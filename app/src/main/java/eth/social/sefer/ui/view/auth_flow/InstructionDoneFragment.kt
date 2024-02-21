package eth.social.sefer.ui.view.auth_flow


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.databinding.FInstructionDoneBinding
import eth.social.sefer.helpers.ToastUtility

class InstructionDoneFragment : Fragment() {
  private var _binding: FInstructionDoneBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FInstructionDoneBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val navController = findNavController()
    val arg: InstructionDoneFragmentArgs by navArgs()

    binding.msg.text = arg.msg

    // event
    binding.skip.setOnClickListener { navController.navigateUp() }
    binding.openMail.setOnClickListener { openEmail() }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }

  private fun openEmail() {
    val intent = Intent(
      Intent.ACTION_SENDTO,
      Uri.fromParts("mailto", Constants.EMAIL_VALUE, null)
    )

    try {
      startActivity(
        Intent.createChooser(
          intent,
          getString(R.string.choose_email_client)
        )
      )
    } catch (e: Exception) {
      ToastUtility.showToast(R.string.gmail_not_installed, requireContext())
    }
  }
}