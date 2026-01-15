package com.raczu.skincareapp.data.local.preferences

import android.content.Context
import android.util.Base64
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager

class SecurityProvider(context: Context) {

    init {
        AeadConfig.register()
    }

    companion object {
        private const val MASTER_KEY_URI = "android-keystore://skin_care_app_master_key"
        private const val KEYSET_NAME = "skin_care_app_keyset"
        private const val PREF_FILE_NAME = "skin_care_app_tink_prefs"
    }

    private val aead: Aead = AndroidKeysetManager.Builder()
        .withSharedPref(context, KEYSET_NAME, PREF_FILE_NAME)
        .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
        .withMasterKeyUri(MASTER_KEY_URI)
        .build()
        .keysetHandle
        .getPrimitive(RegistryConfiguration.get(), Aead::class.java)

    fun encrypt(plaintext: String): String {
        val ciphertext = aead.encrypt(plaintext.toByteArray(), null)
        return Base64.encodeToString(ciphertext, Base64.DEFAULT)
    }

    fun decrypt(ciphertext: String?): String? {
        if (ciphertext == null) return null
        return try {
            val decoded = Base64.decode(ciphertext, Base64.DEFAULT)
            val plaintext = aead.decrypt(decoded, null)
            String(plaintext)
        } catch (e: Exception) {
            null
        }
    }
}