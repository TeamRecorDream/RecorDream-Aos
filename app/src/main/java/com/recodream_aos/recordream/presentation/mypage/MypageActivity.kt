package com.recodream_aos.recordream.presentation.mypage

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.recodream_aos.recordream.R
import com.recodream_aos.recordream.databinding.ActivityMypageBinding
import com.recodream_aos.recordream.presentation.login.LoginActivity
import com.recodream_aos.recordream.util.CustomDialog
import com.recodream_aos.recordream.util.shortToast

class MypageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMypageBinding
    private val myPageBottomSheetFragment = MypageBottomSheetFragment()
    private val mypageViewModel by viewModels<MypageViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMypageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClick()
    }

    private fun setOnClick() {
        binding.tvMypageDeleteAccount.setOnClickListener { showDialog() }
        binding.btnMypageLogout.setOnClickListener { outLogin() }
        binding.ivMypageEditName.setOnClickListener { editName() }
        switchOnClick()
        binding.ivMypageBack.setOnClickListener { finish() }

    }

    private fun editName() {
        binding.ivMypageEditName.setOnClickListener {
            binding.edtMypageName.isEnabled
        }
    }

    private fun showNicknameWarning() {
        if (binding.edtMypageName.text.isNullOrBlank()) {
            // TODO: 이거 왜 int값임?
            shortToast(R.string.mypage_name_warning)
        }
    }

    private fun showDialog() {
        val dialog: CustomDialog
        dialog = CustomDialog(this@MypageActivity)
        dialog.mypageShowDeleteDialog(R.layout.custom_mypage_dialog)
    }

    private fun outLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }

    private fun createBottomSheet() {
//        myPageBottomSheetFragment.show(supportFragmentManager)
        //.show(supportFragmentManager, myPageBottomSheetFragment.tag)
    }

    private fun switchOnClick() {
        binding.switchMypagePushAlam.setOnCheckedChangeListener { compoundButton, onSwitch ->
            if (onSwitch) {
                createBottomSheet()
            } else {
            }
        }
    }
}
