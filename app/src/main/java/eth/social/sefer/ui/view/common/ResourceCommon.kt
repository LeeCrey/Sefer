package eth.social.sefer.ui.view.common

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.yahiaangelo.markdownedittext.MarkdownEditText
import eth.social.sefer.R
import eth.social.sefer.databinding.APostNewBinding
import eth.social.sefer.helpers.ApplicationHelper.isInternetAvailable
import eth.social.sefer.helpers.InputHelper
import eth.social.sefer.helpers.KeyboardUtils
import eth.social.sefer.helpers.PrefHelper
import eth.social.sefer.helpers.ToastUtility
import eth.social.sefer.helpers.ViewUtils
import eth.social.sefer.ui.view.AppActivity
import eth.social.sefer.ui.view.dialogs.ConfirmationDialog
import eth.social.sefer.ui.view.dialogs.ConnectionErrorDialog
import eth.social.sefer.ui.view.dialogs.LoadingDialog
import eth.social.sefer.ui.vm.NewResourceVM
import io.noties.markwon.Markwon
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Common for new(post, comment and reply screen)
open class ResourceCommon : AppActivity() {
  private var toast: Toast? = null

  protected val user = PrefHelper.currentUser
  private val loading: LoadingDialog = LoadingDialog()

  protected lateinit var backPressCB: OnBackPressedCallback
  protected val vm: NewResourceVM by viewModels()
  protected val markdown: Markwon by lazy {
    InputHelper.buildMarkWon(this)
  }

  protected lateinit var submit: TextView
  protected lateinit var editor: MarkdownEditText
  protected lateinit var binding: APostNewBinding

  // for update
  protected var isUpdate: Boolean = false
  private lateinit var confirmationDialog: ConfirmationDialog

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    binding = APostNewBinding.inflate(layoutInflater)
    setContentView(binding.root)

    setSupportActionBar(binding.toolBar)

    confirmationDialog = ConfirmationDialog()

    supportActionBar?.apply {
      title = ""
      setHomeAsUpIndicator(R.drawable.ic_close)
      setDisplayHomeAsUpEnabled(true)
    }

    val imageLoader = Glide.with(this)

    // user data attach
    binding.userFullName.text = user.fullName
    imageLoader.load(user.profileUrl).placeholder(R.drawable.profile_placeholder)
      .into(binding.profile)

    editor = binding.editor
    submit = binding.submit

    // event
    editor.setStylesBar(binding.btnStyle)
    registerBackPress()
    editor.doAfterTextChanged {
      vm.enableStatus(it.toString().isNotEmpty())
    }
    submit.setOnClickListener {
      KeyboardUtils.hide(editor)

      if (!isInternetAvailable()) {
        ConnectionErrorDialog().show(supportFragmentManager, null)
        return@setOnClickListener
      }
      submitResource()
    }

    val myContext = this
    // observer
    lifecycleScope.launch {
      repeatOnLifecycle(Lifecycle.State.RESUMED) {
        vm.response.collectLatest {
          it.message?.let { msg ->
            toast = ToastUtility.toastInstance(msg, myContext)
            toast?.show()
          }

          // dismiss dialog first
          loading.dismiss()

          if (it.okay || it.unauthorized) {
            finish()
          }
        }
      }
    }
    vm.enabled.observe(this) {
      submit.isEnabled = it
      backPressCB.isEnabled = vm.shouldShowDialog
    }

    ViewUtils.changeStatusBarIcons(this, isDarkMode())
    ViewUtils.changeStatusBarColor(this, R.color.surface)
  }

  override fun finish() {
    super.finish()

    overridePendingTransition(R.anim.wait, R.anim.slide_out_bottom)
  }

  override fun onDestroy() {
    super.onDestroy()

    backPressCB.remove()
    toast?.cancel()
    toast = null
  }

  override fun onSaveInstanceState(outState: Bundle) {
    editor.setSelection(0)
    editor.clearFocus()

    super.onSaveInstanceState(outState)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == android.R.id.home) {
      finish()
    }

    return super.onOptionsItemSelected(item)
  }

  private fun registerBackPress() {
    backPressCB = object : OnBackPressedCallback(false) {
      override fun handleOnBackPressed() {
        if (editor.isFocused) {
          editor.clearFocus()
        } else if (vm.shouldShowDialog) {
          confirmationDialog.show(supportFragmentManager, null)
        }
      }
    }
    onBackPressedDispatcher.addCallback(backPressCB)
  }

  // send data to server
  protected open fun submitResource() {
    loading.show(supportFragmentManager, null)
  }
}