package eth.social.sefer.ui.vm

import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eth.social.like_button.LikeButton
import eth.social.sefer.data.models.Post
import eth.social.sefer.data.models.api_response.ApiResponse
import eth.social.sefer.data.repos.remote.PostRepo
import kotlinx.coroutines.flow.SharedFlow
import java.text.NumberFormat

open class PostVM(
  url: String
) : ViewModel() {
  private val repo: PostRepo = PostRepo(viewModelScope, url)
  val posts: LiveData<List<Post>> = repo.posts
  val response: SharedFlow<ApiResponse> = repo.response
  var isLoaded: Boolean = false

  init {
    listOfPosts(null, false)
  }

  fun listOfPosts(page: Int?, append: Boolean) = repo.listOfPosts(page, append)

  fun votePost(
    post: Post,
    voteButton: LikeButton,
    numberOfLikes: TextView,
    numberFormatter: NumberFormat,
  ) = repo.toggleVote(post, voteButton, numberOfLikes, numberFormatter)

  fun reportPost(post: Post) = repo.reportPost(post)

  fun deletePost(post: Post) = repo.deletePost(post)

  fun loadMorePosts() = repo.loadMorePosts()

  fun isNextAvailable(): Boolean = repo.isNextAvailable()

  fun retry() = repo.retryCurrentRequest()
}