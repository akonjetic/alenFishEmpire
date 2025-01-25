import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class DatabaseBackupWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        val context = applicationContext
        val localDatabasePath = context.getDatabasePath("FishDatabase").absolutePath
        val googleDriveHelper = GoogleDriveHelper(context)

        val driveService = googleDriveHelper.authenticate()

        try {
            googleDriveHelper.uploadFile(localDatabasePath, null)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }
}
