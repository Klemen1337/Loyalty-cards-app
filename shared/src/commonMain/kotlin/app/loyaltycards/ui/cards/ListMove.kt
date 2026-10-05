package app.loyaltycards.ui.cards

/** Returns a copy of this list with the item at [from] moved to index [to]. */
internal fun <T> List<T>.move(from: Int, to: Int): List<T> {
    if (from == to) return this
    return toMutableList().apply { add(to, removeAt(from)) }
}
