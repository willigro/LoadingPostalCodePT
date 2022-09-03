package com.rittmann.common.liveevent

typealias MutableLiveSimpleEvent = MutableLiveEvent<Unit>
typealias LiveSimpleEvent = LiveEvent<Unit>

fun MutableLiveSimpleEvent.postEvent() = postEvent(Unit)