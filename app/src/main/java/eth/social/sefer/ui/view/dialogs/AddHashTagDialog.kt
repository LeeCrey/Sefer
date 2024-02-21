package eth.social.sefer.ui.view.dialogs

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import eth.social.sefer.data.models.HashTag
import eth.social.sefer.databinding.DHashtagBinding
import eth.social.sefer.databinding.LHashTagBinding
import eth.social.sefer.helpers.KeyboardUtils
import eth.social.sefer.ui.vm.NewResourceVM

class AddHashTagDialog : BottomSheetDialogFragment() {
  private var _binding: DHashtagBinding? = null

  private val binding get() = _binding!!
  private val vm: NewResourceVM by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
  ): View {
    _binding = DHashtagBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    val inflater = layoutInflater
    val editor: EditText = binding.hashTagEditor

    // observer
    vm.hashtags.observe(viewLifecycleOwner) { list ->
      if (list == null) {
        return@observe
      }

      showHashTags(list, inflater)
    }

    // event
    editor.setOnEditorActionListener { _, actionId, event ->
      if (actionId == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
        addHashTag()
        true
      } else {
        false
      }
    }
    binding.done.setOnClickListener {
      dismiss()
    }
    KeyboardUtils.requestFocusAndShowKeyboard(editor, true)
  }

  override fun onDestroyView() {
    super.onDestroyView()

    _binding = null
  }

  private fun addHashTag() {
    val hashTagName = hashTagName() ?: return

    val hashTag = HashTag().apply {
      id = ViewCompat.generateViewId()
      name = hashTagName
    }
    vm.appendHashTag(hashTag)
  }

  private fun hashTagName(): String? {
    val hashTagName = binding.hashTagEditor.text.toString().trim()
    binding.hashTagEditor.text.clear()
    if (hashTagName.isEmpty()) {
      KeyboardUtils.hide(binding.hashTagEditor)
      return null
    }

    return hashTagName
  }

  private fun showHashTags(list: Set<HashTag>, inflater: LayoutInflater) {
    binding.hashTagGroup.removeAllViews()

    for (tag in list) {
      addHashTagToView(inflater, tag)
    }
  }

  private fun addHashTagToView(inflater: LayoutInflater, hashTag: HashTag): Chip {
    val chip: LHashTagBinding = LHashTagBinding.inflate(inflater)
    val chipView = chip.root
    chipView.id = ViewCompat.generateViewId()
    chipView.text = hashTag.name
    chipView.setOnCloseIconClickListener {
      vm.removeHashTag(hashTag)
    }
    chipView.setOnLongClickListener {
      binding.hashTagEditor.setText(hashTag.name)
      vm.removeHashTag(hashTag)

      true
    }
    binding.hashTagGroup.addView(chipView)

    return chipView
  }
}