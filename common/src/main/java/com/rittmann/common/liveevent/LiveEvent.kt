package com.rittmann.common.liveevent

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.LinkedBlockingQueue

/**
 * [LiveData] that uses [Event] instead of state.
 *
 * This should NOT be used unless to send events about navigation,
 * toasts, SnackBar.
 * For everything else that is shown in the UI until further user
 * input, consider using normal [LiveData].
 * Do NOT use this as a way to callback the UI.
 *
 * It makes sure that all submitted events are consumed by saving
 * a FIFO-like queue of posted events (with [postEvent]) when
 * there is already a posted event that has not yet been consumed.
 *
 * It can have multiple observers but one only one should consume the event.
 * All the others must use normal [Observer] and [Event.content] to see its content.
 *
 * It will throw [UnrecoverableException] if trying to observe
 * with [ConsumerObserver] while there is already one consumer.
 *
 * It will throw [UnrecoverableException] if an event is posted ([postEvent])
 * and no [ConsumerObserver] is observing it.
 */
open class LiveEvent<E : Any> : LiveData<Event<E>>() {

    private val queuedEvents = LinkedBlockingQueue<Event<E>>()
    private var hasPostedEvent = false

    private var consumerObserver: LifecycleBoundConsumerObserver? = null

    override fun observe(owner: LifecycleOwner, observer: Observer<in Event<E>>) {
        checkConsumerOnAddingObserver(observer, owner)

        super.observe(owner) {
            val beforeWasHandled = it.hasBeenConsumed
            observer.onChanged(it)
            // if we have multiple observers,
            // we are only done with the event once it has been handled
            if (!beforeWasHandled && it.hasBeenConsumed) {
                onEventHandled()
            }
        }
    }

    override fun removeObserver(observer: Observer<in Event<E>>) {
        super.removeObserver(observer)
        if (observer === consumerObserver?.observer) {
            removeConsumerObserver()
        }
    }

    override fun removeObservers(owner: LifecycleOwner) {
        super.removeObservers(owner)
        if (owner == consumerObserver?.owner) {
            removeConsumerObserver()
        }
    }

    @Synchronized
    protected open fun postEvent(event: Event<E>) {
        if (!hasPostedEvent) {
            hasPostedEvent = true
            super.postValue(event)
        } else {
            queuedEvents.offer(event)
        }
    }

    @Synchronized
    private fun onEventHandled() {
        hasPostedEvent = false
        val queuedEvent = queuedEvents.poll()
        if (queuedEvent != null) {
            postEvent(queuedEvent)
        }
    }

    private fun checkConsumerOnAddingObserver(
        observer: Observer<in Event<E>>,
        owner: LifecycleOwner
    ) {
        if (observer !is ConsumerObserver<*>) return

        // We are adding a new ConsumerObserver
        if (consumerObserver != null) {

            return
        }

        consumerObserver = LifecycleBoundConsumerObserver(owner, observer)
    }

    private fun removeConsumerObserver() {
        consumerObserver?.let {
            it.detachObserver()
            consumerObserver = null
        }
    }

    private inner class LifecycleBoundConsumerObserver(
        val owner: LifecycleOwner,
        val observer: ConsumerObserver<*>,
    ) {

        val lifecycleObserver: LifecycleEventObserver

        init {
            lifecycleObserver = object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    val currentState: Lifecycle.State = owner.lifecycle.currentState

                    if (currentState == Lifecycle.State.DESTROYED) {
                        removeConsumerObserver()
                        return
                    }
                }
            }

            owner.lifecycle.addObserver(lifecycleObserver)
        }

        fun detachObserver() {
            owner.lifecycle.removeObserver(lifecycleObserver)
        }
    }
}