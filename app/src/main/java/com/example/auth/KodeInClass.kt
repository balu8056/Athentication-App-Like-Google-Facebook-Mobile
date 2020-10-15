package com.example.auth

import android.app.Application
import com.example.auth.repository.AuthRep
import com.example.auth.ui.AuthFactory
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton

class KodeInClass: Application(), KodeinAware {

    override val kodein: Kodein by Kodein.lazy {
        import(androidXModule(this@KodeInClass))

        bind() from singleton { AuthRep() }
        bind() from singleton { AuthFactory(instance()) }

    }

}