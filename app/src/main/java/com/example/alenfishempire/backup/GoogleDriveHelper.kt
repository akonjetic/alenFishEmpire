import android.content.Context
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import com.google.api.client.http.FileContent
import com.google.api.client.http.HttpTransport
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory

import java.io.FileOutputStream
import java.io.OutputStream

@Suppress("DEPRECATION")
class GoogleDriveHelper(private val context: Context) {
    private var driveService: Drive? = null

    fun authenticate(): Drive {
        val credential = GoogleAccountCredential.usingOAuth2(
            context, listOf(DriveScopes.DRIVE_FILE)
        )
        credential.selectedAccountName = "anakonjetic@gmail.com"

        // Fix the unresolved references here
        val transport: HttpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

        driveService = Drive.Builder(
            transport, jsonFactory, credential
        ).setApplicationName("Alen Fish Empire").build()

        return driveService!!
    }

    fun uploadFile(localFilePath: String, driveFolderId: String?): String? {
        val fileMetadata = com.google.api.services.drive.model.File()
        fileMetadata.name = "backup_database.db"
        driveFolderId?.let { fileMetadata.parents = listOf(it) }

        val filePath = java.io.File(localFilePath)
        if (!filePath.exists()) {
            throw IllegalArgumentException("The local file path does not exist: $localFilePath")
        }

        fileMetadata.name = filePath.name.ifEmpty { "backup_database.db" }

        val fileContent = FileContent("application/octet-stream", filePath)

        val uploadedFile = driveService?.files()?.create(fileMetadata, fileContent)
            ?.setFields("id")
            ?.execute()
        return uploadedFile?.id
    }

    fun downloadFile(driveFileId: String, outputPath: String) {
        val outputStream: OutputStream = FileOutputStream(outputPath)
        driveService?.files()?.get(driveFileId)?.executeMediaAndDownloadTo(outputStream)
    }
}
