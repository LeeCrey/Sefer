package eth.social.sefer.ui.view.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.BuildConfig
import eth.social.sefer.Constants
import eth.social.sefer.R
import eth.social.sefer.databinding.SettingsLandBinding
import eth.social.sefer.helpers.ApplicationHelper.setUpToolBar
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility


class SettingsLand : Fragment() {
  private var _binding: SettingsLandBinding? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = SettingsLandBinding.inflate(inflater, container, false)

    return binding.root
  }

  @RequiresApi(Build.VERSION_CODES.TIRAMISU)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val navController = setUpToolBar(view, R.id.app_bar_container, this)
    val toolbar: Toolbar = view.findViewById(R.id.app_bar_container)
    val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_close)
    drawable?.setTint(requireContext().getColor(R.color.white))
    toolbar.navigationIcon = drawable

    val glide: RequestManager = Glide.with(requireContext())
    val user = PrefHelper.currentUser

    val appVersion = "Sefer Version ${BuildConfig.VERSION_NAME}"
    binding.appVersion.text = appVersion

    // apply info
    binding.apply {
      profileName.text = user.fullName
      glide.load(user.profileUrl).placeholder(R.drawable.placeholder)
        .into(profilePic)

      // event
      openAccountSettings.setOnClickListener {
        navController.navigate(SettingsLandDirections.openAccountSettings())
      }
      blockedUserList.setOnClickListener {
        navController.navigate(SettingsLandDirections.openBlockedUserList())
      }
      feedback.setOnClickListener {
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
    binding.language.setOnClickListener {
      navController.navigate(R.id.navigation_language)
    }

    // event
    toolbar.setNavigationOnClickListener { requireActivity().finish() }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }
}