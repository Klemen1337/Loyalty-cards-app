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
    }
}
