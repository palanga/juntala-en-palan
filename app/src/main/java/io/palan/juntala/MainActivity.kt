package io.palan.juntala

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.Fuel.Companion.put
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
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
//        fetchFirex()
        fetchVisa()
    }

    fun fetchFirex() {
        val queue = Volley.newRequestQueue(this)

        val baseUrl = "http://data.fixer.io/api"
        val endpoint = "/latest"
        val accessKey = "?access_key=194dd99570e0b7ccfcf4827bf443d387"

//        source currency access restricted for free plan (default source currency is EUR)
//        val sourceCurrency = "&base=USD"
        val destinationCurrencies = "&symbols=USD,ARS"

        val url = baseUrl + endpoint + accessKey + destinationCurrencies

        // Request a string response from the provided URL.
        val stringRequest = StringRequest(
                Request.Method.GET, url,
                Response.Listener<String> { response ->
                    // Display the first 500 characters of the response string.
                    fetch_response.text = "Response is: ${response}"
                },
                Response.ErrorListener { fetch_response.text = "That didn't work! ${url}" }
        )

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }

    fun fetchVisa() {
        val url = "https://sandbox.api.visa.com/forexrates/v1/foreignexchangerates"
        val userId = "JUJVAR3QPP1F2CRCBOEN21M9n5kSYoyhTzJbKkfbrfZgH6SPU"
        val password = "QeQejH1fqs36zdi8Hmu6UwEhT6tvi"

        val headersMap = mapOf(
                "Accept" to "application/json",
                "Authorization" to "{base64 encoded $userId:$password}"
        )

        val requestBodyMap = mapOf(
                "destinationCurrencyCode" to "ARS",
                "markUpRate" to "1",
                "retrievalReferenceNumber" to "201010101031",
                "sourceAmount" to "100",
                "sourceCurrencyCode" to "EUR",
                "systemsTraceAuditNumber" to "350421"
        )

        val json = JSONObject()
        json
                .put("destinationCurrencyCode", "ARS")
                .put("markUpRate", "1")
                .put("retrievalReferenceNumber", "201010101031")
                .put("sourceAmount", "100")
                .put("sourceCurrencyCode", "EUR")
                .put("systemsTraceAuditNumber", "350421")

        url
                .httpPost()
                .header(mapOf(
                        "Accept" to "application/json",
                        "Authorization" to "Basic" + Base64.encodeToString(
                                "$userId:$password".toByteArray()
                                , Base64.NO_WRAP
                        ),
//                        "Authorization" to "SlVKVkFSM1FQUDFGMkNSQ0JPRU4yMU05bjVrU1lveWhUekpiS2tmYnJmWmdINlNQVTpRZVFlakgxZnFzMzZ6ZGk4SG11NlV3RWhUNnR2aQ==",
                        "Content-Type" to "application/json"
                ))
//                        "Accept" to "application/json",
//                        "Authorization" to "base64 encoded $userId:$password",
//                .body(json.toString())
                .responseString { request, response, result ->
                    println(request.toString())
                    println(response.toString())
//                    println(result.toString())
                    when (result) {
                        is Result.Failure -> {
                            val ex = result.getException()
                            fetch_response.text = "FAILED: $ex"
                        }
                        is Result.Success -> {
                            val data = result.get()
                            fetch_response.text = "SUCCEDED: $data"
                        }
                    }
                }


//        url
//                .httpPost()
//                .header(headersMap)
//                .body(requestBodyMap.toString())
//                .responseString { request, response, result ->
//                    //do something with response
//                    when (result) {
//                        is Result.Failure -> {
//                            val ex = result.getException()
//                        }
//                        is Result.Success -> {
//                            val data = result.get()
//                        }
//                    }
//                }

//        val json = JSONObject()
//        json
//                .put("destinationCurrencyCode", "ARS")
//                .put("markUpRate", "1")
//                .put("retrievalReferenceNumber", "201010101031")
//                .put("sourceAmount", "100")
//                .put("sourceCurrencyCode", "EUR")
//                .put("systemsTraceAuditNumber", "350421")
//
//        val (request, response, result) =
//                Fuel.post(url)
//                        .header(
//                                "Content-Type" to "application/json",
//                                "Accept" to "application/json",
//                                "Authorization" to "base64 encoded $userId:$password"
//                        )
//                        .body(json.toString())
//                        .responseString()
//
//        println(request.toString())
//        result.fold({ /*success*/ }, { /*failure*/ })

//        val requestBody = JSONObject(requestBodyMap)
//
//        val jsonRequest = JsonObjectRequest(
//                Request.Method.POST,
//                url,
//                requestBody,
//                Response.Listener { response ->
//                    fetch_response.text = "Response is: $response"
//                },
//                Response.ErrorListener { fetch_response.text = "That didn't work! ${url}" }
//                )
//
//        // Add the request to the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//
//        queue.add(jsonRequest)


        //an extension over string (support GET, PUT, POST, DELETE with httpGet(), httpPut(), httpPost(), httpDelete())
//        "http://httpbin.org/get".httpGet().responseString { request, response, result ->
//            //do something with response
//            when (result) {
//                is Result.Failure -> {
//                    val ex = result.getException()
//                }
//                is Result.Success -> {
//                    val data = result.get()
//                }
//            }
//        }
//                .header(headersMap).body(requestBodyMap.toString())


        // Request a string response from the provided URL.
//        val stringRequest = StringRequest(
//                Request.Method.POST,
//                url,
//                Response.Listener<String> { response ->
//                    // Display the first 500 characters of the response string.
//                    fetch_response.text = "Response is: ${response}"
//                },
//                Response.ErrorListener { fetch_response.text = "That didn't work! ${url}" }
//        )

//        HEADERS:
//        Accept: application/json
//        Authorization: {base64 encoded userid:password}

//        BODY
//
//        {
//            "cardAcceptor": {
//            "address": {
//            "city": "Foster City",
//            "country": "RU",
//            "county": "San Mateo",
//            "state": "CA",
//            "zipCode": "94404"
//        },
//            "idCode": "ABCD1234ABCD123",
//            "name": "ABCD",
//            "terminalId": "ABCD1234"
//        },
//            "destinationCurrencyCode": "ARS",
//            "markUpRate": "1",
//            "retrievalReferenceNumber": "201010101031",
//            "sourceAmount": "100",
//            "sourceCurrencyCode": "EUR",
//            "systemsTraceAuditNumber": "350421"
//        }
    }

}
