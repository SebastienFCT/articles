package com.tectes.fileprovider

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Surface(modifier = Modifier.fillMaxSize()) {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Gray
            ),
            onClick = {
                val file = createDummyFile(context)
                shareFile(file, context)
            }
        ) {
            Text(
                text = "Send file",
                color = Color.Black
            )
        }
    }
}

enum class PathType {
    FILES,
    CACHE,
    EXTERNAL,
    EXTERNAL_FILES,
    EXTERNAL_CACHE,
}

fun createDummyFile(context: Context, pathType: PathType = PathType.FILES): File {
    val timestamp = System.currentTimeMillis()
    val path = when (pathType) {
        PathType.FILES -> File(context.filesDir, "files")
        PathType.CACHE -> File(context.cacheDir, "cache")
        PathType.EXTERNAL -> File(Environment.getExternalStorageDirectory(), "external")
        PathType.EXTERNAL_FILES -> File(context.getExternalFilesDir(null), "external_files")
        PathType.EXTERNAL_CACHE -> File(context.externalCacheDir, "external_cache")
    }

    try {
        File(context.filesDir, "").mkdirs()
        path.mkdirs()
    } catch (_: SecurityException) {}

    val file = File(path, "$timestamp.txt")
    file.printWriter().use { out ->
        out.println("hello world")
    }

    return file
}

fun shareFile(file: File, context: Context) {
    val uri = FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.customfileprovider", file)

    val intent = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_STREAM, uri)
        setDataAndType(uri, "text/html")
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val shareIntent = Intent.createChooser(intent, "Share file")
    try {
        context.startActivity(shareIntent)
    } catch (e: ActivityNotFoundException) {
        Log.w("MainActivity", "failed to initiate `createChooser`", e)
    }
}