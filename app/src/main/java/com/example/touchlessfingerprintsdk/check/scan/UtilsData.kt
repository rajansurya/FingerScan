package com.example.touchlessfingerprintsdk.check.scan

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

object FingerSegmenter {
    fun segmentImage(bitmap: Bitmap): List<Bitmap> {
        val width = bitmap.width / 4
        return (0 until 4).map {
            Bitmap.createBitmap(bitmap, it * width, 0, width, bitmap.height)
        }
    }
}

object AESUtil {
    private val key: SecretKey by lazy {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        keyGen.generateKey()
    }

    fun encryptBitmap(bitmap: Bitmap): String {
        val byteStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
        val inputBytes = byteStream.toByteArray()

        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        val iv = ByteArray(16)
        SecureRandom().nextBytes(iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))

        val encrypted = cipher.doFinal(inputBytes)
        val combined = iv + encrypted
        return Base64.encodeToString(combined, Base64.DEFAULT)
    }
}
