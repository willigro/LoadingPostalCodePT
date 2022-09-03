package com.rittmann.common.liveevent

/**
 * Mutable [LiveEvent]
 */
class MutableLiveEvent<E : Any> : LiveEvent<E>() {

    fun postEvent(event: E) {
        super.postEvent(ConsumableEvent(event))
    }
}