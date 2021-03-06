package org.jdc.template.ux.startup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vikingsen.inject.viewmodel.savedstate.SavedStateViewModelFactory
import org.jdc.template.R
import org.jdc.template.inject.Injector
import org.jdc.template.ui.ThemeManager
import org.jdc.template.ui.activity.BaseActivity
import org.jdc.template.ux.main.MainActivity
import timber.log.Timber
import javax.inject.Inject

class StartupActivity : BaseActivity() {

    @Inject
    lateinit var themeManager: ThemeManager
    @Inject
    lateinit var viewModelFactoryFactory: SavedStateViewModelFactory.Factory

    private val viewModel: StartupViewModel by viewModels { viewModelFactoryFactory.create(this, null) }

    init {
        Injector.get().inject(this)
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        setupViewModelObservers()

        themeManager.applyTheme()
    }

    private fun setupViewModelObservers() {
        // Events
        lifecycleScope.launchWhenStarted {
            for (event in viewModel.eventChannel) {
                when (event) {
                    is StartupViewModel.Event.StartupProgress -> Timber.i("Startup Progress ${event.progress}")
                    is StartupViewModel.Event.StartupFinished -> showStartActivity()
                }
            }
        }
    }

    private fun showStartActivity() {
        val intent = Intent(application, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.fade_in, R.anim.nothing) // no animation
    }

    private fun devPauseStartup() {
        MaterialAlertDialogBuilder(this)
                .setMessage("Paused for debugger attachment")
                .setPositiveButton("OK") { _, _ -> viewModel.debugResumeStartup() }

    }
}
