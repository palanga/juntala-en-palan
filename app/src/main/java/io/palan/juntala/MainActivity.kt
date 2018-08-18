package io.palan.juntala

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var exchangeRatesStore: ExchangeRatesStore = ExchangeRatesStore.default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        exchangeRatesStore = ExchangeRatesStore.of(getPreferences(Context.MODE_PRIVATE))

        eur_ars_input.hint = exchangeRatesStore.exchangeRates.getValue("EUR" to "ARS").toString()
        eur_usd_input.hint = exchangeRatesStore.exchangeRates.getValue("EUR" to "USD").toString()
    }

    fun handleCalculate(view: View) {
        val price = Price.of("EUR", amount_from_input.text.toString())
        val exchangeRates = exchangeRatesStore.exchangeRates
        amount_converted_pesos.text = price.convert("ARS", exchangeRates).amount.toString()
        amount_converted_dollars.text = price.convert("USD", exchangeRates).amount.toString()
    }

    fun handleChangeRates(view: View) {
        exchangeRatesStore.set(("EUR" to "ARS"), eur_ars_input.text.toString())
        exchangeRatesStore.set(("EUR" to "USD"), eur_usd_input.text.toString())

        with(eur_ars_input) { hint = if (text.isEmpty()) hint else text }
        with(eur_usd_input) { hint = if (text.isEmpty()) hint else text }

        handleCalculate(view)
        exchangeRatesStore.persist(getPreferences(Context.MODE_PRIVATE))
    }

    fun handleFetch(view: View) {
        Toast.makeText(this, "not implemented", Toast.LENGTH_SHORT).show()
    }

}
