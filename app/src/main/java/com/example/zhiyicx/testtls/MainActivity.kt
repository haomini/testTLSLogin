package com.example.zhiyicx.testtls

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        login.setOnClickListener {
            TsTimManager.doTlsLogin("ok", this) {
                Toast.makeText(this, "login success", Toast.LENGTH_SHORT).show()
            }
        }

        logout.setOnClickListener {
            TIMManager.getInstance().logout(object : TIMCallBack {
                override fun onError(p0: Int, p1: String?) {
                    Toast.makeText(this@MainActivity, "login out error {$p0 $p1}", Toast.LENGTH_SHORT).show()
                }

                override fun onSuccess() {
                    Toast.makeText(this@MainActivity, "login out success", Toast.LENGTH_SHORT).show()
                }
            })
            TlsBusiness.logout("ts_14")
        }
    }
}
