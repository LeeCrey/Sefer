package eth.social.sefer.ui.view

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.navigation.navArgs
import eth.social.sefer.R
import eth.social.sefer.data.models.Comment
import eth.social.sefer.ui.view.common.ResourceCommon

class NewCommentActivity : ResourceCommon() {
  private val arg: NewCommentActivityArgs by navArgs()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    //
    binding.hashTagGroup.isVisible = false
    binding.addHashTag.visibility = View.INVISIBLE
    submit.text = getString(R.string.post_comment)
    editor.hint = getString(R.string.thought_msg)

    val postContent = binding.parentResource
    arg.post?.content?.let { content ->
      postContent.isVisible = true
      markdown.setMarkdown(postContent, content)
    }

    arg.comment?.let { comment ->
      isUpdate = true
      comment.content?.let(binding.editor::renderMD)
    }
  }

  override fun submitResource() {
    val comment = Comment().apply {
      commentableId = arg.post?.id
      content = binding.editor.getMD()
    }

    if (isUpdate) {
      vm.updateComment(arg.comment!!.id, comment)
    } else {
      vm.submitComment(comment)
    }

    super.submitResource()
  }
}