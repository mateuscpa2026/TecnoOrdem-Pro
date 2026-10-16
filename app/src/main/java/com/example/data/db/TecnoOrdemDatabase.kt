package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CompanyProfileDao
import com.example.data.dao.CustomerDao
import com.example.data.dao.EquipmentDao
import com.example.data.dao.FinancialDao
import com.example.data.dao.InventoryDao
import com.example.data.dao.StatusHistoryDao
import com.example.data.dao.WarrantyDao
import com.example.data.dao.WorkOrderDao
import com.example.data.model.CompanyProfile
import com.example.data.model.Customer
import com.example.data.model.Equipment
import com.example.data.model.EquipmentType
import com.example.data.model.FinancialTransaction
import com.example.data.model.InventoryPart
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.StatusHistoryEntry
import com.example.data.model.TransactionType
import com.example.data.model.Warranty
import com.example.data.model.WarrantyType
import com.example.data.model.WorkOrder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CompanyProfile::class,
        Customer::class,
        Equipment::class,
        WorkOrder::class,
        StatusHistoryEntry::class,
        InventoryPart::class,
        FinancialTransaction::class,
        Warranty::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class TecnoOrdemDatabase : RoomDatabase() {

    abstract fun companyProfileDao(): CompanyProfileDao
    abstract fun customerDao(): CustomerDao
    abstract fun equipmentDao(): EquipmentDao
    abstract fun workOrderDao(): WorkOrderDao
    abstract fun statusHistoryDao(): StatusHistoryDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun financialDao(): FinancialDao
    abstract fun warrantyDao(): WarrantyDao

    companion object {
        @Volatile
        private var INSTANCE: TecnoOrdemDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): TecnoOrdemDatabase {
            return INSTANCE ?: synchronized(this) {
                // Delete previous development sample databases to ensure app is completely clean for users
                try {
                    context.deleteDatabase("tecno_ordem_v2.db")
                    context.deleteDatabase("tecno_ordem_pro.db")
                    context.deleteDatabase("tecno_ordem_v1.db")
                } catch (_: Exception) {}

                lateinit var instance: TecnoOrdemDatabase
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TecnoOrdemDatabase::class.java,
                    "tecno_ordem_app.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope) { instance })
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope,
        private val dbProvider: () -> TecnoOrdemDatabase
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            scope.launch(Dispatchers.IO) {
                populateInitialData(dbProvider())
            }
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
            scope.launch(Dispatchers.IO) {
                populateInitialData(dbProvider())
            }
        }

        private suspend fun populateInitialData(db: TecnoOrdemDatabase) {
            // Clean database for production / Play Store publication:
            // Only initialize the clean Company Profile entity (no dummy customers, orders, inventory or transactions)
            db.companyProfileDao().insertOrUpdate(CompanyProfile())
        }
    }
}
