package com.example.project1729;

import android.content.Context
import org.pytorch.*
import android.media.*
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.io.*

class MainActivity : AppCompatActivity() {

    private lateinit var module: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        module = LiteModuleLoader.load(assetFilePath(this, "model.pt"))

    }


    private fun assetFilePath(context: Context, assetName: String): String { // Helper
        val file = File(context.filesDir, assetName)
        if (file.exists() && file.length() > 0) {
            return file.absolutePath
        }
        context.assets.open(assetName).use { `is` ->
            FileOutputStream(file).use { os ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (`is`.read(buffer).also { read = it } != -1) {
                    os.write(buffer, 0, read)
                }
                os.flush()
            }
            return@use file.absolutePath
        }
    }


    private fun recognize(audioData: FloatArray): LongArray {
        val inputTensor = Tensor.fromBlob(audioData, longArrayOf(1, audioData.size.toLong()))
        val output = module.forward(IValue.from(inputTensor)).toTensor()
        return output.dataAsLongArray
    }
    
}