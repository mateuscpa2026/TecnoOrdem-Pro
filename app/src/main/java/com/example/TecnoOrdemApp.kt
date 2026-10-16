package com.example

import android.app.Application
import com.example.data.db.TecnoOrdemDatabase
import com.example.data.repository.TecnoOrdemRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class TecnoOrdemApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val database by lazy {
        TecnoOrdemDatabase.getDatabase(this, applicationScope)
    }

    val repository by lazy {
        TecnoOrdemRepository(
            companyProfileDao = database.companyProfileDao(),
            customerDao = database.customerDao(),
            equipmentDao = database.equipmentDao(),
            workOrderDao = database.workOrderDao(),
            statusHistoryDao = database.statusHistoryDao(),
            inventoryDao = database.inventoryDao(),
            financialDao = database.financialDao(),
            warrantyDao = database.warrantyDao()
        )
    }

    val preferencesDataStore by lazy {
        com.example.data.preferences.AppPreferencesDataStore(this)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: TecnoOrdemApp
            private set
    }
}
