package eth.social.sefer.ui.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.MenuProvider
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import eth.social.sefer.R
import eth.social.sefer.databinding.FEditBioBinding
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.KeyboardUtils.closeKeyboard
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.RegistrationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class EditBioFragment : Fragment(), MenuProvider {
  private val vm: RegistrationVM by viewModels()
  private val arg: EditBioFragmentArgs by navArgs()

  private var _saveIcon: ImageView? = null
  private var _binding: FEditBioBinding? = null

  private val binding get() = _binding!!
  private val saveIcon get() = _saveIcon!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FEditBioBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

    // when is set text, it should update number of chars indicator
    registerForInput()

    binding.bioContent.setText(arg.user.bio)

    // observer
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          if (it.okay) {
            PrefHelper.updateBio(binding.bioContent.text.toString())
          }

          it.message?.let { msg ->
            ToastUtility.showToast(msg, requireContext())
          }

          binding.bioContent.isEnabled = true
          saveIcon.apply {
            animation = null
            setImageDrawable(
              ContextCompat.getDrawable(requireContext(), R.drawable.ic_save)
            )
            isEnabled = true
          }
        }
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }

  override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
    menuInflater.inflate(R.menu.bio_menu, menu)

    val animation: Animation = AnimationUtils.loadAnimation(requireContext(), R.anim.loading)
    val saveItem: MenuItem = menu.findItem(R.id.save)
    _saveIcon = saveItem.actionView!!.findViewById(R.id.save_icon)

    saveIcon.isEnabled = false
    saveIcon.setOnClickListener {
      closeKeyboard(requireActivity())

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      saveIcon.apply {
        setImageDrawable(
          ContextCompat.getDrawable(requireContext(), R.drawable.ic_loading)
        )
        isEnabled = false
      }
      binding.bioContent.isEnabled = false
      saveIcon.startAnimation(animation)

      onMenuItemSelected(saveItem)
    }
  }

  override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
    return when (menuItem.itemId) {
      R.id.save -> {
        vm.updateBioMessage(binding.bioContent.text.toString())

        true
      }

      else -> false
    }
  }

  private fun registerForInput() {
    val numberOfChars: TextView = binding.bioCountValue
    binding.bioContent.doAfterTextChanged {
      val count = it!!.length
      _saveIcon?.isEnabled = count > 0

      val value = "$count/200"
      numberOfChars.text = value
    }
  }
}