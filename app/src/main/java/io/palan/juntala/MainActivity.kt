package io.palan.juntala

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private val EXCHANGE_RATES = mapOf(
            ("ARS" to "EUR") to 0.029,
            ("ARS" to "USD") to 0.033,
            ("EUR" to "ARS") to 33.97,
            ("EUR" to "USD") to 1.14,
            ("USD" to "ARS") to 29.86,
            ("USD" to "EUR") to 0.8
    ).withDefault { _ -> 1.0 }

    fun calculate(view: View) {
        val inputValue = amount_input.text.toString().toDoubleOrNull()
        val price = Price("EUR", inputValue ?: 0.0)
        amount_converted_pesos.text = price.convert("ARS", EXCHANGE_RATES).amount.toString()
        amount_converted_dollars.text = price.convert("USD", EXCHANGE_RATES).amount.toString()
    }
}

typealias Currency = String
typealias Amount = Double
class Price(val currency: Currency, val amount: Amount) {
    fun convert(to: Currency, exchangeRates: ExchangeRates): Price {
        return Price(to, amount * exchangeRates.getValue(currency to to))
    }
}
typealias ExchangeRates = Map<Pair<String, String>, Double>
