package com.example.auth.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.auth.R
import com.example.auth.ui.AuthFactory
import com.example.auth.ui.AuthViewModel
import com.example.auth.ui.MainActivity
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class HomeActivity : AppCompatActivity(), KodeinAware {

    override val kodein: Kodein by kodein()
    private val authFactory: AuthFactory by instance()

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        authViewModel = ViewModelProvider(this, authFactory).get(AuthViewModel::class.java)

        signOut.setOnClickListener {
            authViewModel.signOut().let {
                if (it){
                    startActivity(Intent(applicationContext, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    })
                    finish()
                }else Toast.makeText(baseContext, "Something went wrong, please try again later...", Toast.LENGTH_SHORT).show()
            }
        }

        authViewModel.getUserDetail()
        observer()
    }

    private fun observer(){
        authViewModel.userImage.observe(this, Observer {

            Picasso.get()
                .load(it)
                .error(R.drawable.button_border)
                .resize(100, 100)
                .into(userimage)
        })
        authViewModel.userName.observe(this, Observer {
            username.text = it
        })
    }
}