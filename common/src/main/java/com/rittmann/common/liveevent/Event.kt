package com.rittmann.common.liveevent

/**
 * Used as a wrapper for data that is exposed via a LiveData that represents an event.
 *
 * Seen at: https://medium.com/google-developers/livedata-with-snackbar-navigation-and-other-events-the-singleliveevent-case-ac2622673150
 * as the current best solution for "one time" event case.
 */
interface Event<out T> {

    val hasBeenConsumed: Boolean

    /** Returns the content, even if it's already been consumed. */
    val content: T
}

/**
 * [hasBeenConsumed] setter should only be used inside [ConsumerObserver]
 */
class ConsumableEvent<out T>(override val content: T) : Event<T> {

    override var hasBeenConsumed: Boolean = false
        private set

    fun consume() {
        hasBeenConsumed = true
    }

}