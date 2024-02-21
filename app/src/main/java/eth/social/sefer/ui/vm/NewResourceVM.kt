package eth.social.sefer.ui.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import eth.social.sefer.data.models.Comment
import eth.social.sefer.data.models.HashTag
import eth.social.sefer.data.models.Post

class NewResourceVM : OperationVM() {
  private var _mEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
  private var _mHashTags: MutableLiveData<HashSet<HashTag>> = MutableLiveData()

  val hashtags: LiveData<HashSet<HashTag>> = _mHashTags
  val enabled: LiveData<Boolean> = _mEnabled

  fun updatePost(id: Long, post: Post) {
    repo.updatePost(id, post)
  }

  fun createPost(post: Post) {
    post.hashTags = hashtags.value?.joinToString(", ")
    repo.createPost(post)
  }

  // comment
  fun submitComment(comment: Comment) {
    repo.submitComment(comment)
  }

  fun updateComment(id: Long, comment: Comment) {
    repo.updateComment(id, comment)
  }

  fun appendHashTag(hashTag: HashTag) {
    val list = currentHashTags()
    list.add(hashTag)
    _mHashTags.postValue(list as HashSet<HashTag>?)
  }

  fun removeHashTag(hashTag: HashTag) {
    val list = currentHashTags()

    list.remove(hashTag)
    _mHashTags.postValue(list as HashSet<HashTag>?)
  }

  private fun currentHashTags(): MutableSet<HashTag> {
    val currentList: HashSet<HashTag>? = hashtags.value
    val list: MutableSet<HashTag> = if (currentList == null) {
      HashSet()
    } else {
      HashSet(currentList)
    }

    return list
  }

  // either of those should not be empty
  fun enableStatus(isNotEmpty: Boolean) {
    _mEnabled.postValue(isNotEmpty)
  }

  val shouldShowDialog: Boolean
    get() {
      return enabled.value!! || !hashtags.value.isNullOrEmpty()
    }
}