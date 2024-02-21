package eth.social.sefer.callbacks

interface MainActivityCallBack {
    fun hideBottomNavView()

    fun showBottomNavView()

    fun toggleBottomNav()

    fun logout()

    fun isBottomNavVisible(): Boolean
}