package io.palan.juntala

import android.content.SharedPreferences

class ExchangeRatesStore(val exchangeRates: ExchangeRatesMap) {

    companion object {
        val default: ExchangeRatesStore = ExchangeRatesStore(DEFAULT_EXCHANGE_RATES)

        fun of(preferences: SharedPreferences): ExchangeRatesStore {
            val eurArsRate = preferences
                    .getFloat("EUR:ARS", DEFAULT_EXCHANGE_RATES.getValue("EUR" to "ARS"))
            val eurUsdRate = preferences
                    .getFloat("EUR:USD", DEFAULT_EXCHANGE_RATES.getValue("EUR" to "USD"))

            val exchangeRatesMap: ExchangeRatesMap = mutableMapOf(
                    ("EUR" to "ARS") to eurArsRate,
                    ("EUR" to "USD") to eurUsdRate
            ).withDefault { _ -> 1.0f }

            return ExchangeRatesStore(exchangeRatesMap)
        }
    }

    fun set(exchangeRate: ExchangeRate) {
        exchangeRates[exchangeRate.first] = exchangeRate.second
    }

    fun set(exchangeKey: ExchangeKey, rate: String) {
        exchangeRates[exchangeKey] = rate.toFloatOrNull() ?: exchangeRates.getValue(exchangeKey)
    }

    fun persist(preferences: SharedPreferences) {
        with(preferences.edit()) {
            putFloat("EUR:ARS", exchangeRates.getValue("EUR" to "ARS"))
            putFloat("EUR:USD", exchangeRates.getValue("EUR" to "USD"))
            apply()
        }
    }
}

typealias Currency = String
typealias Amount = Float
typealias Rate = Float
typealias ExchangeKey = Pair<Currency, Currency>
typealias ExchangeRate = Pair<ExchangeKey, Rate>
typealias ExchangeRatesMap = MutableMap<ExchangeKey, Rate>

class Price(val currency: Currency, val amount: Amount) {
    companion object {
        fun of(currency: Currency, amount: String): Price {
            return Price(currency, amount.toFloatOrNull() ?: 0.0f)
        }
    }

    fun convert(to: Currency, exchangeRates: ExchangeRatesMap): Price {
        return Price(to, amount * exchangeRates.getValue(currency to to))
    }
}

val DEFAULT_EXCHANGE_RATES = mutableMapOf(
        ("ARS" to "EUR") to 0.029f,
        ("ARS" to "USD") to 0.033f,
        ("EUR" to "ARS") to 33.97f,
        ("EUR" to "USD") to 1.14f,
        ("USD" to "ARS") to 29.86f,
        ("USD" to "EUR") to 0.8f
).withDefault { _ -> 1.0f }
