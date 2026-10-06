package app.loyaltycards.domain

/** Barcode symbologies a loyalty card can use. Stored by [name] in the database. */
enum class BarcodeFormat {
    CODE_128,
    CODE_39,
    EAN_13,
    EAN_8,
    UPC_A,
    QR_CODE,
    PDF_417,
    AZTEC,
    DATA_MATRIX;

    companion object {
        fun fromName(name: String): BarcodeFormat =
            entries.firstOrNull { it.name == name } ?: CODE_128

        /**
         * Best guess for a code typed in by hand: EAN/UPC when the digits and check digit fit,
         * otherwise Code 128, which can encode anything printable.
         */
        fun infer(code: String): BarcodeFormat {
            if (code.isEmpty() || !code.all { it in '0'..'9' } || !hasValidCheckDigit(code)) {
                return CODE_128
            }
            return when (code.length) {
                13 -> EAN_13
                12 -> UPC_A
                8 -> EAN_8
                else -> CODE_128
            }
        }

        /** GS1 mod-10 check digit, shared by EAN-13, EAN-8 and UPC-A. */
        internal fun hasValidCheckDigit(digits: String): Boolean {
            if (digits.length < 2) return false
            val body = digits.dropLast(1)
            val sum = body.reversed().withIndex().sumOf { (index, char) ->
                val digit = char - '0'
                if (index % 2 == 0) digit * 3 else digit
            }
            return (10 - sum % 10) % 10 == digits.last() - '0'
        }
    }
}

/** Formats a card code the way it's usually printed, e.g. "2 400118 352905" for EAN-13. */
fun formatCardCode(code: String, format: BarcodeFormat): String = when {
    format == BarcodeFormat.EAN_13 && code.length == 13 ->
        "${code.take(1)} ${code.substring(1, 7)} ${code.substring(7)}"
    format == BarcodeFormat.EAN_8 && code.length == 8 ->
        "${code.take(4)} ${code.substring(4)}"
    format == BarcodeFormat.UPC_A && code.length == 12 ->
        "${code.take(1)} ${code.substring(1, 6)} ${code.substring(6, 11)} ${code.substring(11)}"
    else -> code.chunked(4).joinToString(" ")
}
