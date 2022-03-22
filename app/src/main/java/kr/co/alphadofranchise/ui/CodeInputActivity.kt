package kr.co.alphadofranchise.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.rxbinding4.widget.textChanges
import gun0912.tedkeyboardobserver.TedKeyboardObserver
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import io.reactivex.rxjava3.schedulers.Schedulers
import kr.co.alphadofranchise.R
import kr.co.alphadofranchise.databinding.ActivityCodeInputBinding
import kr.co.alphadofranchise.viewmodel.FranchiseViewModel
import kr.co.alphadofranchise.base.BaseActivity
import kr.co.domain.model.StoreCode
import java.security.MessageDigest

class CodeInputActivity : BaseActivity() {
    private var binding : ActivityCodeInputBinding? = null
    private var isKeyboardShow = false
    private lateinit var franchiseViewModel : FranchiseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=  DataBindingUtil.setContentView(this, R.layout.activity_code_input)

        init()

        observeInputCodeLiveData()

        TedKeyboardObserver(this)
            .listen { isShow ->
                if(isShow) {
                    isKeyboardShow = true

                    if(binding!!.editCode.text.toString().trim().length != 4) {
                        binding!!.editCode.setBackgroundResource(R.drawable.bg_rounding_5dp_stroke_point_sub)
                        binding!!.btnConfirm.visibility = View.VISIBLE
                    }
                } else {
                    isKeyboardShow = false

                    if(binding!!.editCode.text.toString().trim().length != 4) {
                        binding!!.editCode.setBackgroundResource(R.drawable.bg_rounding_5dp_stroke_stroke_gray)
                        binding!!.btnConfirm.visibility = View.GONE
                        binding!!.editCode.clearFocus()
                    }
                }
            }

        binding!!.btnConfirm.setOnClickListener { moveMainActivity() }

        binding!!.editCode.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_DONE) {
                moveMainActivity()
            }
            false
        }

        binding!!.editCode.setOnKeyListener { _, keyCode, _ ->
            when(keyCode) {
                KeyEvent.ACTION_DOWN -> moveMainActivity()
                KeyEvent.KEYCODE_ENTER -> moveMainActivity()
            }

            false
        }

        binding!!.editCode.textChanges()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                if(it.trim().count() == 4) {
                    binding!!.editCode.setBackgroundResource(R.drawable.bg_rounding_5dp_stroke_point)
                } else {
                    if(isKeyboardShow) {
                        binding!!.editCode.setBackgroundResource(R.drawable.bg_rounding_5dp_stroke_point_sub)
                    } else {
                        binding!!.editCode.setBackgroundResource(R.drawable.bg_rounding_5dp_stroke_stroke_gray)
                    }
                }
            }
    }

    private fun init(){
        franchiseViewModel = ViewModelProvider(this, viewModelFactory).get(FranchiseViewModel::class.java)
    }

    private fun observeInputCodeLiveData(){
        franchiseViewModel.inputCodeLiveData.observe(this, {
            if(it != null) {
                 if(it is String) {
                     Toast.makeText(applicationContext,it, Toast.LENGTH_SHORT).show()
                 } else {
                     val storeCode = it as StoreCode
                     val intent = Intent(this, MainActivity::class.java)
                     intent.putExtra("code", storeCode.code)
                     intent.putExtra("region", storeCode.region)
                     startActivity(intent)
                     finish()
                 }
            } else {
                Toast.makeText(applicationContext,"인터넷 연결 끊김", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveMainActivity(){
        if(validCheck()) {
            val imm = applicationContext.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding!!.editCode.windowToken, 0)

            franchiseViewModel.requestInputStoreCode(code = binding!!.editCode.text.toString().trim())
        }
    }

    private fun validCheck() : Boolean {
        if(binding!!.editCode.text.toString().trim().isEmpty()){
            Toast.makeText(applicationContext, "지점코드를 입력해주세요.",Toast.LENGTH_SHORT).show()
            return false
        } else if(binding!!.editCode.text.toString().trim().length != 4)  {
            Toast.makeText(applicationContext, "지점코드를 정확히 입력해주세요.",Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        binding = null
        compositeDisposable.clear()
    }
}