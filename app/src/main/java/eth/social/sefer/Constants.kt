package eth.social.sefer

object Constants {
  // Strings don not need translation
  const val DOMAIN = "https://sefer-xnwh.onrender.com"
//  const val SOCKET_URL: String = "wss://sefer-xnwh.onrender.com/socket"

  const val VIDEO: String = "video"
  const val COMMENT: String = "comment"
  const val IS_SEARCH: String = "is_search"
  const val USERNAME: String = "user_name"
  const val COMMUNITY_ID: String = "communityId"
  const val POST: String = "post"
  const val IS_ME: String = "is_me"
  const val URL: String = "url"
  const val LBL_NAME: String = "lbl_name"
  const val COMMUNITY: String = "community"
  const val USER: String = "user"
  const val EMAIL_VALUE: String = "sefer_team@proton.me"

  val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
  const val LANG = "language"
  const val THEME_TYPE = "dark_mode"

  var VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
  const val CHANNEL_ID = "VERBOSE_NOTIFICATION"
  const val NOTIFICATION_ID = 1
  const val PREFS_FILE = "eth.social.sefer.matrix_pref"
  const val USER_ID = "eth.social.sefer.student_id"
  const val FULL_NAME = "eth.social.sefer.full_name"
  const val PROFILE_URL = "eth.social.sefer.profile_url"
  const val EMAIL = "eth.social.sefer.email"

//    const val DOMAIN = "https://sefer.up.railway.app/"

  const val DISCOVER_COMMUNITY_URL: String = "api/v1/communities/discover"
  const val BLOCKED_USERS: String = "api/v1/users/blocked"
  const val COMMUNITIES_URL: String = "api/v1/communities"
  const val SHORT_VIDEOS: String = "api/v1/short_videos"
  const val UNLOCK: String = "users/unlocks"
  const val CONFIRM: String = "users/confirmation"
  const val PASSWORD: String = "users/passwords"
  const val USERS_URL: String = "api/v1/users"
  const val POSTS: String = "api/v1/posts"
  const val LIKED_POSTS_URL: String = "api/v1/posts/liked"

  fun commentCreateUrl(commentableId: Long): String {
    return "api/v1/posts/${commentableId}/comments"
  }

  fun videoCommentsUrl(videoId: Long): String {
    return "api/v1/videos/$videoId/comments"
  }

  fun postCommentsUrl(postId: Long): String {
    return "api/v1/posts/$postId/comments"
  }

  fun communityPosts(communityId: Long): String {
    return "api/v1/communities/$communityId/posts"
  }

  fun postLikedUsers(postId: Long): String {
    return "api/v1/posts/$postId/users"
  }

  fun userPosts(userId: Long): String {
    return "api/v1/users/$userId/posts"
  }

  fun userVideos(userId: Long): String {
    return "api/v1/users/$userId/videos"
  }

  fun postUrl(postId: Long): CharSequence {
    return DOMAIN + "api/v1/posts/$postId"
  }
}
