package eth.social.sefer.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class CustomTabAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
  FragmentStateAdapter(fm, lifecycle) {
  private lateinit var list: List<Fragment>
  override fun createFragment(position: Int): Fragment {
    return list[position]
  }

  override fun getItemCount(): Int {
    return list.size
  }

  fun setList(list: List<Fragment>) {
    this.list = list
  }
}
