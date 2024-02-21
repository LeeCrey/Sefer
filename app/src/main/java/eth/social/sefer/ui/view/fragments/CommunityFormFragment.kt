package eth.social.sefer.ui.view.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import eth.social.sefer.R
import eth.social.sefer.callbacks.MediaCallBack
import eth.social.sefer.data.models.Community
import eth.social.sefer.databinding.FCreateCommunityBinding
import eth.social.sefer.helpers.ApplicationHelper
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.FileUtils
import eth.social.sefer.helpers.KeyboardUtils
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.vm.OperationVM
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CommunityFormFragment : Fragment(), MediaCallBack {
  private var _binding: FCreateCommunityBinding? = null
  private var pickMedia: ActivityResultLauncher<PickVisualMediaRequest>? = null
  private val imageLoader: RequestManager by lazy {
    Glide.with(requireContext())
  }

  private var uri: Uri? = null
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = FCreateCommunityBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val vm: OperationVM by viewModels()
    val navController = findNavController()
    val arg: CommunityFormFragmentArgs by navArgs()
    var isUpdate = false

    // observer
    lifecycleScope.launch {
      vm.response.collectLatest {
        it.message?.let { msg ->
          ToastUtility.showToast(msg, requireContext())
        }

        // close loading dialog
        navController.navigateUp()

        if (it.okay) {
          navController.navigateUp()
        }
      }
    }

    var communityType: RadioButton = binding.typePublic

    //
    pickMedia = ApplicationHelper.setupPickMedia(this, this)
    binding.communityDescription.doAfterTextChanged {
      val count = it.toString().length
      binding.communityDescription.isEnabled = count < 255

      val value = "$count/255"
      binding.count.text = value
    }
    binding.fab.setOnClickListener { v ->
      KeyboardUtils.hide(v)

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(childFragmentManager, null)
        return@setOnClickListener
      }

      val community = Community().apply {
        name = binding.communityName.text.toString()
        description = binding.communityDescription.text.toString()
        coverUri = uri
        type = communityType.text.toString()
      }

      if (isUpdate) {
        vm.updateCommunity(arg.community!!.id, community)
      } else {
        vm.createCommunity(community)
      }

      navController.navigate(R.id.navigation_loading)
    }
    binding.communityCover.setOnClickListener {
      pickMedia?.launch(
        PickVisualMediaRequest.Builder()
          .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly).build()
      )
    }
    binding.communityTypeValue.setOnCheckedChangeListener { _, checkedId ->
      communityType = view.findViewById(checkedId)
    }

    binding.communityName.doAfterTextChanged {
      binding.fab.isEnabled = it.toString().isNotEmpty()
    }

    // apply info if any
    arg.community?.let { com ->
      binding.communityName.setText(com.name)
      binding.communityDescription.setText(com.description)
      com.coverUri?.let { imageLoader.load(it).into(binding.communityCover) }

      if (com.type == "Private") {
        binding.typePrivate.isChecked = true
      }
      isUpdate = true
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
    uri = null
    pickMedia = null
  }

  override fun onMediaSelected(uri: Uri) {
    imageLoader.load(uri).into(binding.communityCover)

    val file = FileUtils.getFile(requireContext(), uri)
    val fileSize = file!!.length() / (1024 * 1024)
    if (fileSize > 2L) {
      ToastUtility.showToast(getString(R.string.incorrect_file), requireContext())
      return
    }

    this.uri = uri
  }
}