package com.recodream_aos.recordream.presentation.splash // ktlint-disable package-name

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.recodream_aos.recordream.R
import com.recodream_aos.recordream.base.BindingActivity
import com.recodream_aos.recordream.databinding.ActivitySplashBinding
import com.recodream_aos.recordream.presentation.MainActivity
import com.recodream_aos.recordream.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : BindingActivity<ActivitySplashBinding>(R.layout.activity_splash) {
    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 뷰 등장 애니메이션 설정, TAG : 시하
        // overridePendingTransition(R.anim.none, R.anim.horizon_exit)

        initViewModel()
        isLoginAble()
    }

    private fun initViewModel() {
        binding.viewModel = splashViewModel
        binding.lifecycleOwner = this
    }

    private fun isLoginAble() {
        //  splashViewModel.tryLogin()
        lifecycleScope.launchWhenStarted {
            launch {
                splashViewModel.isLoginSuccess.collectLatest {
                    Log.d("안녕", "ㄴㅇㅂㄴㅂㅇ 액티비티요")
                    delay(3000)
                    Log.d("안녕", "$it 액티비티요")
                    if (it) startMainActivity() else startLoginActivity()
                }
            }
        }
    }

    private fun startMainActivity() {
        val intentToMain = Intent(this, MainActivity::class.java)
        startActivity(intentToMain)
        finish()
    }

    private fun startLoginActivity() {
        val intentToLogin = Intent(this, LoginActivity::class.java)
        startActivity(intentToLogin)
        finish()
    }

    companion object {
        const val NINE_HUNDRED = 900L
    }
}
