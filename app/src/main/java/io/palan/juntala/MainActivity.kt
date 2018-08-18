package io.palan.juntala

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val exchangeRatesStore: ExchangeRatesStore = ExchangeRatesStore.ofDefault()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        eur_ars_input.hint = exchangeRatesStore.exchangeRates.getValue("EUR" to "ARS").toString()
        eur_usd_input.hint = exchangeRatesStore.exchangeRates.getValue("EUR" to "USD").toString()
    }

    fun handleCalculate(view: View) {
        val inputValue = amount_from_input.text.toString().toDoubleOrNull() ?: 0.0
        val price = Price("EUR", inputValue)
        val exchangeRates = exchangeRatesStore.exchangeRates
        amount_converted_pesos.text = price.convert("ARS", exchangeRates).amount.toString()
        amount_converted_dollars.text = price.convert("USD", exchangeRates).amount.toString()
    }

    fun handleChangeRates(view: View) {
        val previousEurArsChangeRate = exchangeRatesStore.exchangeRates.getValue("EUR" to "ARS")
        val eurArsChangeRate = eur_ars_input.text.toString().toDoubleOrNull()
                ?: previousEurArsChangeRate

        val previousEurUsdChangeRate = exchangeRatesStore.exchangeRates.getValue("EUR" to "USD")
        val eurUsdChangeRate = eur_usd_input.text.toString().toDoubleOrNull()
                ?: previousEurUsdChangeRate

        exchangeRatesStore.set(("EUR" to "ARS") to eurArsChangeRate)
        exchangeRatesStore.set(("EUR" to "USD") to eurUsdChangeRate)

        handleCalculate(view)
    }

}
