package io.palan.juntala

class ExchangeRatesStore(val exchangeRates: ExchangeRatesMap) {

    companion object {
        fun ofDefault(): ExchangeRatesStore {
            return ExchangeRatesStore(DEFAULT_EXCHANGE_RATES)
        }
    }

    fun set(exchangeRate: ExchangeRate) {
        exchangeRates[exchangeRate.first] = exchangeRate.second
    }
}

typealias Currency = String
typealias Amount = Double
typealias Rate = Double
typealias ExchangeKey = Pair<Currency, Currency>
typealias ExchangeRate = Pair<ExchangeKey, Rate>
typealias ExchangeRatesMap = MutableMap<ExchangeKey, Rate>

class Price(val currency: Currency, val amount: Amount) {
    fun convert(to: Currency, exchangeRates: ExchangeRatesMap): Price {
        return Price(to, amount * exchangeRates.getValue(currency to to))
    }
}

val DEFAULT_EXCHANGE_RATES = mutableMapOf(
        ("ARS" to "EUR") to 0.029,
        ("ARS" to "USD") to 0.033,
        ("EUR" to "ARS") to 33.97,
        ("EUR" to "USD") to 1.14,
        ("USD" to "ARS") to 29.86,
        ("USD" to "EUR") to 0.8
).withDefault { _ -> 1.0 }
