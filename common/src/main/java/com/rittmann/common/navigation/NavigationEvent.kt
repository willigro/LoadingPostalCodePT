package com.rittmann.common.navigation

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import com.rittmann.common.constants.INVALID_ID

/**
 * Base class for all navigation events of our app.
 *
 * This should increase as needed to accommodate all use
 * cases.
 */
sealed interface NavigationEvent

/**
 * Used to signal the screen to be closed.
 *
 * @param screenClass class of the screen we wish to close
 * @param screen instance of the screen we wish to close
 * @param reason represents the reason why this screen is finishing
 * @param behavior defines what will be happening with the current screen
 * @param containerViewId view identifier that holds the screen
 * @param visibleViewId view identifier that holds the screen that will be visible.
 */
class CloseScreen private constructor(
    val screenClass: Class<out Fragment>?,
    val screen: Fragment?,
    val reason: CloseReason,
    val behavior: CloseBehavior,
    @IdRes val containerViewId: Int,
    @IdRes val visibleViewId: Int,
) : NavigationEvent {

    constructor(
        reason: CloseReason = CloseReason.NONE,
        behavior: CloseBehavior = CloseBehavior.NONE,
        @IdRes containerViewId: Int = INVALID_ID,
        @IdRes visibleViewId: Int = INVALID_ID,
    ) : this(null, null, reason, behavior, containerViewId, visibleViewId)

    constructor(
        screen: Fragment,
        reason: CloseReason = CloseReason.NONE,
        behavior: CloseBehavior = CloseBehavior.NONE,
        @IdRes containerViewId: Int = INVALID_ID,
        @IdRes visibleViewId: Int = INVALID_ID,
    ) : this(null, screen, reason, behavior, containerViewId, visibleViewId)

    constructor(
        screenClass: Class<out Fragment>,
        reason: CloseReason = CloseReason.NONE,
        behavior: CloseBehavior = CloseBehavior.NONE,
        @IdRes containerViewId: Int = INVALID_ID,
        @IdRes visibleViewId: Int = INVALID_ID,
    ) : this(screenClass, null, reason, behavior, containerViewId, visibleViewId)

    enum class CloseReason {
        NONE,           // No specific reason
        EXIT_PRESSED,   // Finished because exit was pressed
    }

    enum class CloseBehavior {
        NONE,               // No specific reason
        HIDE_CURRENT,       // Hide current screen
        HIDE_ALL,           // Hide all screens
        SHOW_PREVIOUS,      // Show previous fragment
        SHOW_ALL,           // Show all previous fragments saved on stack
        SWITCH_PREVIOUS,    // Show previous fragment from same container and remove current
        REMOVE_ALL,         // Removes all fragments in the stack
        REMOVE_ALL_BEFORE,  // Removes all fragments in stack before fragment passed as parameter
        REMOVE_ALL_AFTER,   // Removes all fragments in stack before fragment passed as parameter
    }

    override fun toString(): String {
        return "CloseScreen(screenClass=$screenClass, screen=$screen, reason=$reason, behavior=$behavior, containerViewId=$containerViewId)"
    }
}

/**
 * Navigates to the screen correspondent to [screenClass].
 *
 * @param screenClass The class of the screen to be opened
 * @param screen Instance of the screen to be opened
 * @param parameters An optional set of parameters that will be passed to the screen
 * @param finishType Changes how the current screen is finished, by default doesn't finish anything
 * @param allowSameInstance Allow two or more same Class type in stack
 * @param addBackStack Tag to add on stack
 * @param containerViewId view identifier that holds the screen
 */
class OpenScreen private constructor(
    val screenClass: Class<out Fragment>?,
    val screen: Fragment?,
    val parameters: Bundle?,
    val finishType: FinishType,
    val allowSameInstance: Boolean,
    val addBackStack: Boolean,
    @IdRes val containerViewId: Int,
) : NavigationEvent {

    constructor(
        screen: Fragment,
        parameters: Bundle? = null,
        finishType: FinishType = FinishType.NONE,
        allowSameInstance: Boolean = false,
        addBackStack: Boolean = false,
        @IdRes containerViewId: Int = INVALID_ID,
    ) : this(null, screen, parameters, finishType, allowSameInstance, addBackStack, containerViewId)

    constructor(
        screenClass: Class<out Fragment>,
        parameters: Bundle? = null,
        finishType: FinishType = FinishType.NONE,
        allowSameInstance: Boolean = false,
        addBackStack: Boolean = false,
        @IdRes containerViewId: Int = INVALID_ID,
    ) : this(
        screenClass,
        null,
        parameters,
        finishType,
        allowSameInstance,
        addBackStack,
        containerViewId
    )

    enum class FinishType {
        NONE,               // Open new screen on top of the current screen
        REMOVE_ALL,         // Finish all the open screens
        REMOVE_CURRENT,     // Only finish the current screen
        HIDE_CURRENT,       // Hide the current fragment and keep it in the stack
    }

    override fun toString(): String {
        return "GoToScreen(screenClass=$screenClass, screen=$screen, parameters=$parameters, finishType=$finishType, allowSameInstance=$allowSameInstance, " +
                "addBackStack=$addBackStack, containerViewId=$containerViewId)"
    }
}