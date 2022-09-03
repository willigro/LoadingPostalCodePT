package com.rittmann.common.liveevent

import androidx.lifecycle.Observer

/**
 * An [Observer] for [Event]s that will consume the event when it gets called.
 *
 * [onEventToConsume] is *only* called if the [Event]'s content has not been consumed.
 */
class ConsumerObserver<T>(private val onEventToConsume: (T) -> Unit) : Observer<Event<T>> {

    override fun onChanged(event: Event<T>?) {
        if (event == null) return

        event as ConsumableEvent

        if (!event.hasBeenConsumed) {
            event.consume()

            onEventToConsume(event.content)
        }
    }
}