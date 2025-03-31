package com.task.mercadopagotask.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.task.mercadopagopotask.R
import com.task.mercadopagopotask.databinding.ActivityPagamentoBinding
import com.task.mercadopagotask.data.preference.MercadoPagoService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.*

class PagamentoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPagamentoBinding
    private val mercadoPagoService = MercadoPagoService()
    private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagamentoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAmountField()
        setupFormValidation()

        binding.btnPay.setOnClickListener {
            processPayment()
        }
    }

    private fun setupAmountField() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    binding.etAmount.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R\\$\\.\\s,]".toRegex(), "")

                    val parsed = if (cleanString.isNotEmpty()) {
                        cleanString.toDouble() / 100
                    } else {
                        0.0
                    }

                    val formatted = currencyFormat.format(parsed)
                    current = formatted
                    binding.etAmount.setText(formatted)
                    binding.etAmount.setSelection(formatted.length)

                    binding.etAmount.addTextChangedListener(this)
                }
                checkFields()
            }
        })
    }

    private fun setupFormValidation() {
        binding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkFields()
            }
        })
    }

    private fun checkFields() {
        val amountText = binding.etAmount.text.toString().replace("[R\\$\\.\\s,]".toRegex(), "")
        val description = binding.etDescription.text.toString()

        val isAmountValid = amountText.isNotEmpty() && amountText.toDouble() > 0
        val isDescriptionValid = description.isNotEmpty()

        binding.btnPay.isEnabled = isAmountValid && isDescriptionValid

        if (binding.btnPay.isEnabled) {
            binding.btnPay.backgroundTintList = ContextCompat.getColorStateList(this, R.color.colorPrimary)
        } else {
            binding.btnPay.backgroundTintList = ContextCompat.getColorStateList(this, R.color.gray)
        }
    }

    private fun processPayment() {
        val amountText = binding.etAmount.text.toString()
            .replace("[R\\$\\.\\s ]".toRegex(), "")
            .replace(",", ".")

        val description = binding.etDescription.text.toString()

        val amount = try {
            BigDecimal(amountText)
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
            return
        }

        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.btnPay.isEnabled = false

        CoroutineScope(Dispatchers.IO).launch {
            val result = mercadoPagoService.createPaymentPreference(amount, description)

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = android.view.View.GONE
                binding.btnPay.isEnabled = true

                result.onSuccess { paymentUrl ->
                    binding.tvPaymentUrl.text = paymentUrl
                    binding.tvPaymentUrl.visibility = android.view.View.VISIBLE
                    openCustomTab(paymentUrl)
                }.onFailure { error ->
                    Toast.makeText(
                        this@PagamentoActivity,
                        "Erro: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun openCustomTab(url: String) {
        val customTabsIntent = CustomTabsIntent.Builder()
            .setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))
            .build()

        try {
            customTabsIntent.launchUrl(this, android.net.Uri.parse(url))
        } catch (e: Exception) {
            Toast.makeText(this, "Não foi possível abrir o navegador", Toast.LENGTH_SHORT).show()
        }
    }
}