package io.palan.juntala

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.Fuel.Companion.put
import com.github.kittinunf.fuel.android.extension.responseJson
import com.github.kittinunf.fuel.gson.responseObject
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.map
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject

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
        fetchFirex()
    }

    fun fetchFirex() {
//        val queue = Volley.newRequestQueue(this)

        val baseUrl = "http://data.fixer.io/api"
        val endpoint = "/latest"
        val accessKey = "?access_key=194dd99570e0b7ccfcf4827bf443d387"

//        source currency access restricted for free plan (default source currency is EUR)
//        val sourceCurrency = "&base=USD"
        val destinationCurrencies = "&symbols=USD,ARS,GBP"

        val url = baseUrl + endpoint + accessKey + destinationCurrencies

        val (_, _, result) = url.httpGet().responseString()

        fun lala(result: String): Int {
            return 7
        }
        result.map { result -> lala(result) }



//        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(
//                Request.Method.GET, url,
//                Response.Listener<String> { response ->
//                    // Display the first 500 characters of the response string.
//                    fetch_response.text = "Response is: ${response}"
//                },
//                Response.ErrorListener { fetch_response.text = "That didn't work! ${url}" }
//        )

        // Add the request to the RequestQueue.
//        queue.add(stringRequest)
    }
}
