package com.hermes.android.data.keystore

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConfigStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "hermes_secure",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    private val plainPrefs: SharedPreferences = context.getSharedPreferences("hermes_config", Context.MODE_PRIVATE)

    private val _provider = MutableStateFlow(plainPrefs.getString("provider", "openai") ?: "openai")
    val provider: Flow<String> = _provider.asStateFlow()

    private val _model = MutableStateFlow(plainPrefs.getString("model", "gpt-4o") ?: "gpt-4o")
    val model: Flow<String> = _model.asStateFlow()

    private val _autoApprove = MutableStateFlow(plainPrefs.getBoolean("auto_approve", false))
    val autoApprove: Flow<Boolean> = _autoApprove.asStateFlow()

    var providerValue: String
        get() = _provider.value
        set(value) {
            _provider.value = value
            plainPrefs.edit().putString("provider", value).apply()
        }

    var modelValue: String
        get() = _model.value
        set(value) {
            _model.value = value
            plainPrefs.edit().putString("model", value).apply()
        }

    var autoApproveValue: Boolean
        get() = _autoApprove.value
        set(value) {
            _autoApprove.value = value
            plainPrefs.edit().putBoolean("auto_approve", value).apply()
        }

    fun getApiKey(): String {
        return securePrefs.getString("api_key", "") ?: ""
    }

    fun setApiKey(key: String) {
        securePrefs.edit().putString("api_key", key).apply()
    }

    fun getGithubPat(): String {
        return securePrefs.getString("github_pat", "") ?: ""
    }

    fun setGithubPat(pat: String) {
        securePrefs.edit().putString("github_pat", pat).apply()
    }
}
