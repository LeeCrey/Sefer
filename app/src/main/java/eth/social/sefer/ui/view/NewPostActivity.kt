package eth.social.sefer.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import androidx.core.view.ViewCompat
import androidx.navigation.navArgs
import com.google.android.material.chip.Chip
import eth.social.sefer.data.models.HashTag
import eth.social.sefer.data.models.Post
import eth.social.sefer.databinding.LHashTagBinding
import eth.social.sefer.ui.view.common.ResourceCommon
import eth.social.sefer.ui.view.dialogs.AddHashTagDialog

class NewPostActivity : ResourceCommon() {
  private val args: NewPostActivityArgs by navArgs()

  // community id
  private var comId: Long? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    comId = if (args.communityId == -1L) null else args.communityId

    args.post?.let { p ->
      p.content?.let(editor::renderMD)
      isUpdate = true
    }

    // event
    binding.addHashTag.setOnClickListener {
      AddHashTagDialog().show(supportFragmentManager, null)
    }

    // observer
    vm.hashtags.observe(this) { list ->
      if (list == null) {
        return@observe
      }

      backPressCB.isEnabled = !vm.shouldShowDialog
      showHashTags(list)
    }
  }

  override fun submitResource() {
    val post = Post().apply {
      content = binding.editor.getMD()
      communityId = comId
    }

    if (isUpdate) {
      vm.updatePost(args.post?.id!!, post)
    } else {
      vm.createPost(post)
    }

    super.submitResource()
  }

  private fun showHashTags(list: Set<HashTag>) {
    binding.hashTagGroup.removeAllViews()

    for (tag in list) {
      addHashTagToView(layoutInflater, tag)
    }
  }

  private fun addHashTagToView(inflater: LayoutInflater, hashTag: HashTag): Chip {
    val chip: LHashTagBinding = LHashTagBinding.inflate(inflater)
    val chipView = chip.root
    chipView.id = ViewCompat.generateViewId()
    chipView.text = hashTag.name
    chipView.isCloseIconVisible = false
    binding.hashTagGroup.addView(chipView)

    return chipView
  }
}