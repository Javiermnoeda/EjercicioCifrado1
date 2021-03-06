package com.example.ejerciciocifrado

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ejerciciocifrado.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.security.MessageDigest
import java.util.*
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import kotlin.jvm.Throws

class MainActivity : AppCompatActivity() {


    lateinit var binding: ActivityMainBinding



    //Para declarar constantes que no van a cambiar y tb para que se pueda acceder a ellas desde cualquier parte del programa
    companion object {
        const val LLAVE_EN_STRING: String = "__Moway19312"
        const val TIPO_DE_CIFRADO: String = "AES/ECB/PKCS5Padding"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.botonCifrado.setOnClickListener {
            val textoCifrado = cifrar(binding.editText.text.toString())
            binding.textviewCrifrado.text = textoCifrado

            CoroutineScope(Dispatchers.IO).launch{
                val client = OkHttpClient()
                val url = "https://e06acdbc6990.ngrok.io"
                val request = Request.Builder().url(url).build()
                val call = client.newCall(request)
                call.execute()
            }
        }

        binding.botonDescifrado.setOnClickListener {
            val textoDescifrado = descifrar(binding.textviewCrifrado.text.toString())
            binding.textviewDescifrado.text = textoDescifrado
        }
    }


    private fun cifrar(textoParaCifrar: String): String {
        val cipher = Cipher.getInstance(TIPO_DE_CIFRADO)
        cipher.init(Cipher.ENCRYPT_MODE, getKey(LLAVE_EN_STRING))
        val textoCifrado = Base64.getEncoder().encodeToString(cipher.doFinal(textoParaCifrar.toByteArray(Charsets.UTF_8)))
        return textoCifrado
    }

    @Throws(BadPaddingException::class)
    private fun descifrar(textoCifrado: String): String {
        val cipher = Cipher.getInstance(TIPO_DE_CIFRADO)
        cipher.init(Cipher.DECRYPT_MODE, getKey(LLAVE_EN_STRING))
        val textoDescifrado = String(cipher.doFinal(Base64.getDecoder().decode(textoCifrado)))
        return textoDescifrado
    }

    private fun getKey(llaveEnString: String): SecretKeySpec {
        var llaveUtf8 = llaveEnString.toByteArray(Charsets.UTF_8)
        val sha = MessageDigest.getInstance("SHA-1")
        llaveUtf8 = sha.digest(llaveUtf8)
        llaveUtf8 = llaveUtf8.copyOf(16)
        return SecretKeySpec(llaveUtf8, "AES")
    }

}