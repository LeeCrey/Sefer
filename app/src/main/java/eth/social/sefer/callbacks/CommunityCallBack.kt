package eth.social.sefer.callbacks

import eth.social.sefer.data.models.Community

interface CommunityCallBack : OnLoadMoreListener {
  fun onCommunityClick(community: Community)

  fun onLongClick(community: Community)
}