package com.task.mercadopagotask.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.task.mercadopagopotask.databinding.ActivityPagamentoResultBinding

class PagamentoResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagamentoResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagamentoResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { handleIntent(it) }
    }

    private fun handleIntent(intent: Intent) {
        val data: Uri? = intent.data
        data?.let { uri ->
            when (uri.path) {
                "/success" -> showResult("Pagamento aprovado!")
                "/failure" -> showResult("Pagamento recusado")
                "/pending" -> showResult("Pagamento pendente")
                else -> showResult("Status de pagamento desconhecido")
            }
        }
    }

    private fun showResult(message: String) {
        binding.tvResult.text = message
    }
}