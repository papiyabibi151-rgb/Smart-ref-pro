package com.example

import android.app.Application
import com.example.data.local.SmartRefDatabase
import com.example.data.repository.SmartRefRepository

class SmartRefApplication : Application() {

    lateinit var repository: SmartRefRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = SmartRefDatabase.getDatabase(this)
        repository = SmartRefRepository(database)
    }
}
