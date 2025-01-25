package com.example.alenfishempire.activity

import DatabaseBackupWorker
import GoogleDriveHelper
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.alenfishempire.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        setupPeriodicBackup()

        binding.newCalculationCard.setOnClickListener {
            val intent = Intent(this, NewCalculationActivity::class.java)
            startActivity(intent)
        }

        binding.orderHistoryCard.setOnClickListener {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
        }

        binding.administrationCard.setOnClickListener {
            val intent = Intent(this, AdministrationActivity::class.java)
            startActivity(intent)
        }

        binding.btnRestoreDatabase.setOnClickListener {
            val googleDriveHelper = GoogleDriveHelper(this)
            val driveService = googleDriveHelper.authenticate()

            // ID datoteke na Google Driveu (ručno ili dinamički)
            val driveFileId = "your_drive_file_id" // Postavi stvarni ID
            val localPath = getDatabasePath("FishDatabase.db").absolutePath

            googleDriveHelper.downloadFile(driveFileId, localPath)
            Toast.makeText(this, "Database restored from Google Drive", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupPeriodicBackup() {
        val backupWorkRequest = PeriodicWorkRequestBuilder<DatabaseBackupWorker>(
            1, TimeUnit.DAYS // Backup svaki dan
        ).build()

        WorkManager.getInstance(this).enqueue(backupWorkRequest)
    }
}