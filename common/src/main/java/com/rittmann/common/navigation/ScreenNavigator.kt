package com.rittmann.common.navigation

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentTransaction.TRANSIT_FRAGMENT_FADE
import androidx.fragment.app.clearFragmentResult
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import com.rittmann.baselifecycle.base.BaseFragment
import com.rittmann.common.constants.INVALID_ID
import com.rittmann.common.lifecycle.BaseBindingActivity
import com.rittmann.common.lifecycle.BaseFragmentBinding
import com.rittmann.common.lifecycle.FRAGMENT_EXIT_CODE
import com.rittmann.common.lifecycle.FRAGMENT_RESULT_CODE
import com.rittmann.common.lifecycle.FRAGMENT_RESULT_KEY
import com.rittmann.common.navigation.CloseScreen.CloseBehavior
import com.rittmann.common.navigation.CloseScreen.CloseReason
import com.rittmann.common.navigation.OpenScreen.FinishType
import com.rittmann.common.tracker.track
import java.lang.Integer.max

/**
 * Screen navigator for fragments.
 */
class ScreenNavigator(
    private val activity: BaseBindingActivity<*>,
) {

    init {
        activity.supportFragmentManager.setFragmentResultListener(
            FRAGMENT_RESULT_KEY,
            activity,
            activity
        )
    }

    /**
     * Starts a new [event].
     */
    fun startNavigationEvent(event: NavigationEvent): Boolean {
        when (event) {
            is CloseScreen -> startCloseScreenEvent(event)
            is OpenScreen -> startOpenScreenEvent(event)
        }

        return true
    }

    private fun startOpenScreenEvent(event: OpenScreen) = with(event) {
        track(event.toString())

        val fragment = getFragmentFromEvent(this)

        val containerViewId =
            if (event.containerViewId == INVALID_ID) activity.screenHolder
            else event.containerViewId

        track("containerViewId=$containerViewId")

        val fragmentManager = getFragmentManager(containerViewId)

        when (finishType) {
            FinishType.REMOVE_ALL -> replaceFragment(
                fragment = fragment,
                fragmentManager = fragmentManager,
                arguments = event.parameters,
                addBackStack = addBackStack,
                containerViewId = containerViewId,
            )

            FinishType.REMOVE_CURRENT -> switchFragment(
                fragment = fragment,
                fragmentManager = fragmentManager,
                parameters = event.parameters,
                containerViewId = containerViewId,
            )

            FinishType.HIDE_CURRENT -> addFragment(
                fragment = fragment,
                fragmentManager = fragmentManager,
                arguments = event.parameters,
                containerViewId = containerViewId,
                hideCurrent = true,
            )

            FinishType.NONE -> addFragment(
                fragment = fragment,
                fragmentManager = fragmentManager,
                arguments = event.parameters,
                containerViewId = containerViewId,
                hideCurrent = false,
            )
        }
    }

    private fun startCloseScreenEvent(event: CloseScreen) = with(event) {
        val containerViewId =
            if (event.containerViewId == INVALID_ID) activity.screenHolder else event.containerViewId
        val fragmentManager = getFragmentManager(containerViewId)

        when (behavior) {
            CloseBehavior.NONE -> {
                removeFragment(
                    fragment = getFragmentFromEvent(this),
                    fragmentManager = fragmentManager,
                )
            }

            CloseBehavior.HIDE_ALL -> {
                hideAll(
                    containerViewId = containerViewId,
                    fragmentManager = fragmentManager,
                )
            }

            CloseBehavior.HIDE_CURRENT -> {
                hide(
                    fragment = getFragmentFromEvent(this),
                    fragmentManager = fragmentManager,
                )
            }

            CloseBehavior.SHOW_ALL -> {
                showAll(containerViewId, fragmentManager)
            }

            CloseBehavior.SHOW_PREVIOUS -> {
                getPreviousFragment(fragmentManager)?.run {
                    show(
                        fragment = this,
                        fragmentManager = fragmentManager,
                    )
                }
            }

            CloseBehavior.SWITCH_PREVIOUS -> {
                getPreviousFragment(fragmentManager, containerViewId)?.run {
                    switchFragment(
                        fragment = this,
                        containerViewId = containerViewId,
                        fragmentManager = fragmentManager,
                        parameters = null,
                    )
                }
            }

            CloseBehavior.REMOVE_ALL -> {
                getFragmentsFromContainer(containerViewId).run {
                    removeFragmentsList(
                        fragments = this,
                        fragmentManager = fragmentManager,
                    )
                }

                if (visibleViewId != INVALID_ID) {
                    // This is necessary to trigger the onVisible to the visibleViewId.
                    getFragmentsFromContainer(visibleViewId).forEach {
                        if (it is BaseFragmentBinding<*>) {
                            it.onVisible()
                        }
                    }
                }
            }
            CloseBehavior.REMOVE_ALL_BEFORE -> {
                val fragments = getFragmentManager(containerViewId).fragments
                val fragment = getFragmentFromEvent(this)
                val fragmentIndex = fragments.indexOfFirst { it == fragment }
                fragments.subList(0, fragmentIndex)
                    .filter { isContainerEquals(it, containerViewId) }.run {
                        removeFragmentsList(
                            fragments = this,
                            fragmentManager = fragmentManager,
                        )
                    }
            }
            CloseBehavior.REMOVE_ALL_AFTER -> {
                val fragments = getFragmentManager(containerViewId).fragments.asReversed()
                val fragment = getFragmentFromEvent(this)
                val fragmentIndex = fragments.indexOfFirst { it == fragment }
                fragments.subList(0, fragmentIndex)
                    .filter { isContainerEquals(it, containerViewId) }.run {
                        removeFragmentsList(
                            fragments = this,
                            fragmentManager = fragmentManager,
                        )
                    }
            }
        }

        when (reason) {
            CloseReason.NONE -> return@with

            CloseReason.EXIT_PRESSED -> {
                val fragment = getFragmentFromEvent(this)
                fragment.setFragmentResult(
                    FRAGMENT_RESULT_KEY,
                    bundleOf(FRAGMENT_RESULT_CODE to FRAGMENT_EXIT_CODE)
                )
                fragment.clearFragmentResult(FRAGMENT_RESULT_KEY)
            }
        }
    }

    private fun switchFragment(
        fragment: Fragment,
        parameters: Bundle?,
        @IdRes containerViewId: Int,
        fragmentManager: FragmentManager,
    ) {
        if (parameters != null) fragment.arguments = parameters

        val currentFragment = getActiveScreen(fragmentManager, containerViewId)
        // TODO - Since we are doing fade animation, we need to separate by two commits and remove, otherwise fragment on background will be visible between transaction. Review this behaviour with this task.
        fragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(TRANSIT_FRAGMENT_FADE)

            if (fragmentManager.fragments.contains(fragment)) show(fragment)
            else add(containerViewId, fragment)

            runOnCommit {
                (fragment as BaseFragmentBinding<*>).onVisible()
            }
        }

        fragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            if (currentFragment != null) remove(currentFragment)
        }
    }

    /**
     * Replaces the fragment at the given [containerViewId] by the provided one. The fragment will only be replaced until the app returns to its event loop.
     *
     * @param fragment The fragment that will replace the current one
     * @param arguments The arguments to pass to the fragment
     * @param containerViewId The ID of the container in which the new fragment will be placed
     * @param fragmentManager The [FragmentManager] to be used in the fragment transaction
     * @param addBackStack Whether the fragment transaction will be added to a backstack so that it can be reverted
     */
    private fun replaceFragment(
        fragment: Fragment,
        arguments: Bundle?,
        @IdRes containerViewId: Int,
        fragmentManager: FragmentManager,
        addBackStack: Boolean,
    ) {
        if (arguments != null) {
            fragment.arguments = arguments
        }

        fragmentManager.commit {
            if (addBackStack) addToBackStack(null)

            setReorderingAllowed(true)
            replace(containerViewId, fragment)
            if (fragment.isHidden) show(fragment)

            onVisible(fragmentManager, containerViewId, fragment, addBackStack)
        }
    }

    /**
     * Removes a list of fragments.
     */
    private fun removeFragmentsList(
        fragments: List<Fragment>,
        fragmentManager: FragmentManager,
    ) {
        fragmentManager.commit {
            fragments.forEach {
                setReorderingAllowed(true)
                setTransition(TRANSIT_FRAGMENT_FADE)
                remove(it)
            }
        }
    }

    /**
     * Add an fragment to fragmentManager, applying the transitions animation.
     */
    private fun addFragment(
        fragment: Fragment,
        arguments: Bundle?,
        @IdRes containerViewId: Int,
        fragmentManager: FragmentManager,
        hideCurrent: Boolean,
    ) {
        if (arguments != null) {
            fragment.arguments = arguments
        }

        val currentFragment = getActiveScreen(fragmentManager, containerViewId)

        fragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(TRANSIT_FRAGMENT_FADE)
            if (fragment.isAdded.not()) add(containerViewId, fragment)
            if (fragment.isHidden) show(fragment)
            if (hideCurrent && currentFragment != null) hide(currentFragment)

            currentFragment?.onInvisible()
            onVisible(fragmentManager, containerViewId, fragment, false)
        }
    }

    /**
     * Remove fragment in fragmentManager, applying the transitions animation.
     */
    private fun removeFragment(
        fragment: Fragment,
        fragmentManager: FragmentManager,
    ) {
        val fragmentToRemove = fragment as? BaseFragmentBinding<*>

        fragmentManager.commit {
            setReorderingAllowed(true)
            setTransition(TRANSIT_FRAGMENT_FADE)
            remove(fragment)
            runOnCommit {
                fragmentToRemove?.onInvisible()
                val previousFragment = getActiveScreen(fragmentManager)
                if (previousFragment?.isVisible == true) {
                    previousFragment.onVisible()
                }
            }
        }
    }

    /**
     * Show fragment in fragmentManager.
     */
    private fun show(
        fragment: Fragment,
        fragmentManager: FragmentManager,
    ) {
        track("Navigating to ${fragment::class.java.simpleName}")
        fragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            show(fragment)
            runOnCommit {
                getActiveScreen(fragmentManager)?.onVisible()
            }
        }
    }

    /**
     * Show all fragments from [containerViewId]
     */
    private fun showAll(
        containerViewId: Int,
        fragmentManager: FragmentManager,
    ) {

        // TODO To avoid flicks we need to separate transaction by two commits(Previous fragment and rest)

        // Show previous fragment
        val fragmentList = getFragmentsFromContainer(containerViewId).toMutableList()
        val previousFragment = fragmentList.removeLastOrNull() ?: return

        fragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            show(previousFragment)
            runOnCommit {
                if (previousFragment is BaseFragmentBinding<*>) {
                    previousFragment.onVisible()
                }
            }
        }

        // Show rest
        if (fragmentList.isNotEmpty()) {
            fragmentManager.commit {
                setTransition(TRANSIT_FRAGMENT_FADE)
                fragmentList.forEach { fragment ->
                    show(fragment)
                }
            }
        }
    }

    /**
     * Hide fragment in fragmentManager.
     */
    private fun hide(
        fragment: Fragment,
        fragmentManager: FragmentManager,
    ) {
        val fragmentToHide = fragment as? BaseFragmentBinding<*>

        fragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            hide(fragment)
            runOnCommit {
                fragmentToHide?.onInvisible()
            }
        }
    }

    /**
     * Hide all fragments from [containerViewId]
     *
     */
    private fun hideAll(
        containerViewId: Int,
        fragmentManager: FragmentManager,
    ) {
        val fragmentList = getFragmentsFromContainer(containerViewId)
        if (fragmentList.isEmpty()) return

        fragmentManager.commit {
            setTransition(TRANSIT_FRAGMENT_FADE)
            fragmentList.forEach { fragment ->
                hide(fragment)
                runOnCommit {
                    if (fragment is BaseFragmentBinding<*>) {
                        fragment.onInvisible()
                    }
                }
            }
        }
    }

    private fun FragmentTransaction.onVisible(
        fragmentManager: FragmentManager,
        containerViewId: Int,
        fragment: Fragment,
        addBackStack: Boolean
    ) {
        if (addBackStack) {
            fragment.lifecycleScope.launchWhenResumed {
                getActiveScreen(fragment.parentFragmentManager, containerViewId)?.onVisible()
            }
        } else {
            runOnCommit {
                getActiveScreen(fragmentManager, containerViewId)?.onVisible()
            }
        }
    }

    private fun getActiveScreen(
        fragmentManager: FragmentManager = activity.supportFragmentManager,
        screenHolder: Int = activity.screenHolder
    ): BaseFragmentBinding<*>? {
        return fragmentManager.findFragmentById(screenHolder) as? BaseFragmentBinding<*>
            ?: return null
    }

    /**
     * Get Previous Fragment from all fragments in [fragmentManager] or from [containerViewId]
     */
    private fun getPreviousFragment(
        fragmentManager: FragmentManager,
        containerViewId: Int? = null
    ): Fragment? {
        val fragmentsList =
            if (containerViewId != null) getFragmentsFromContainer(containerViewId) else fragmentManager.fragments.filterIsInstance<BaseFragmentBinding<*>>()
        val index = max(fragmentsList.size - 2, 0)

        return fragmentsList.getOrNull(index)
    }

    private fun getFragmentFromEvent(event: OpenScreen): Fragment {
        if (event.screenClass == null && event.screen == null) {
            throw IllegalArgumentException("Unable to get fragment from event. Invalid screenClass and screen parameters.")
        }

        return event.screen ?: if (event.allowSameInstance) {
            activity.supportFragmentManager.fragments.find {
                it::class.java == event.screenClass
            } ?: event.screenClass!!.newInstance() as Fragment

        } else event.screenClass!!.newInstance() as Fragment
    }

    private fun getFragmentFromEvent(event: CloseScreen): Fragment {
        if (event.screenClass == null && event.screen == null) {
            throw IllegalArgumentException("Unable to get fragment from event. Invalid screenClass and screen parameters.")
        }

        return event.screen ?: activity.supportFragmentManager.fragments.find {
            it::class.java == event.screenClass
        } ?: throw IllegalStateException("Unable to get fragment from event. | event=$event")
    }

    private fun getFragmentManager(containerViewId: Int): FragmentManager {
        return try {
            val activeScreenRootView = getActiveScreen()?.view
                ?: return activity.supportFragmentManager

            val fragment = activeScreenRootView.findViewById<View>(containerViewId)
                ?.findFragment<BaseFragment>()
                ?: return activity.supportFragmentManager

            fragment.childFragmentManager
        } catch (e: IllegalStateException) {
            activity.supportFragmentManager
        }
    }

    /**
     * Used to get fragments from [containerViewId]
     */
    private fun getFragmentsFromContainer(containerViewId: Int): List<Fragment> {
        return getFragmentManager(containerViewId).fragments.filter { fragment ->
            isContainerEquals(
                fragment,
                containerViewId
            )
        }
    }

    /**
     * Check if fragment has the same container as [containerViewId]
     */
    private fun isContainerEquals(fragment: Fragment, containerViewId: Int): Boolean {
        return (fragment.view?.parent as? ViewGroup)?.id == containerViewId
    }
}