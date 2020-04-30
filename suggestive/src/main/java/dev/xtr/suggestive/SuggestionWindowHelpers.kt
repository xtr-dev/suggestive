package dev.xtr.suggestive

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import kotlin.math.max
import kotlin.math.min

/**
 * Measures the window's visible region into [windowVisibleFrame].
 */
internal fun SuggestionWindow.measureWindowVisibleFrame() {
    anchor.rootView.getWindowVisibleDisplayFrame(windowVisibleFrame)
}

/**
 * Measures the view dimensions into [viewDimensions]
 *
 * @param availableWidth the available width
 * @param availableHeight the available height
 */
internal fun SuggestionWindow.measureViewDimensions(availableWidth: Int, availableHeight: Int) {
    if (!viewDimensions.isEmpty && !layoutRequested) {
        // Dimensions shouldn't have changed since the last calculation
        return
    } else if (layoutRequested) {
        layoutRequested = false
    }

    view.measure(
        View.MeasureSpec.makeMeasureSpec(availableWidth, View.MeasureSpec.AT_MOST),
        View.MeasureSpec.makeMeasureSpec(availableHeight, View.MeasureSpec.AT_MOST)
    )

    val width = if (view.layoutParams.width == ViewGroup.LayoutParams.MATCH_PARENT) {
        availableWidth
    } else {
        view.measuredWidth
    }
    val height = if (view.layoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT) {
        availableHeight
    } else {
        view.measuredHeight
    }

    viewDimensions.set(0, 0, width, height)
}

/**
 * Update the screen space rect of [anchor] stored in [anchorRect].
 */
internal fun SuggestionWindow.updateAnchorRect() {
    val anchorScreenLocation = intArrayOf(0, 0)
    anchor.getLocationOnScreen(anchorScreenLocation)
    anchorRect.set(
        anchorScreenLocation[0], anchorScreenLocation[1],
        anchorScreenLocation[0] + anchor.measuredWidth,
        anchorScreenLocation[1] + anchor.measuredHeight
    )
}

/**
 * Update the popup window rect stored in [windowRect].
 */
internal fun SuggestionWindow.updateWindowRect() {
    measureWindowVisibleFrame()
    updateAnchorRect()

    val availableWidth = if (constrainToAnchorBounds) {
        anchorRect.width()
    } else {
        windowVisibleFrame.width()
    } - marginLeft - marginRight
    val availableHeight = if (determinedPosition == SuggestionWindow.PreferredPosition.ABOVE) {
        anchorRect.top - stableInsets.top
    }
    else {
        windowVisibleFrame.height() - anchorRect.top - stableInsets.bottom
    } - marginTop - marginBottom

    // Adjust available height by (safe) margins
    val safeSpaceHeight = availableHeight - verticalSafeMargin * 2

    measureViewDimensions(availableWidth, availableHeight)
    val viewWidth = viewDimensions.width()
    val viewHeight = viewDimensions.height()
    val width = max(0, min(viewWidth, availableWidth))
    val height = max(0, min(viewHeight, safeSpaceHeight))
    val offsetY = if (determinedPosition == SuggestionWindow.PreferredPosition.ABOVE) {
        -height
    } else {
        anchorRect.height()
    }

    if (popupGravity xor Gravity.START == 0) {
        windowRect.left = windowVisibleFrame.left
        windowRect.right = windowRect.left + width
    }
    else if (popupGravity xor Gravity.END == 0) {
        windowRect.right = anchorRect.right
        windowRect.left = windowRect.right - width
    }
    else {
        windowRect.left = anchorRect.centerX() - width / 2
        windowRect.right = anchorRect.centerX() + width / 2
    }

    if (constrainToAnchorBounds) {
        if (width > anchorRect.width()) {
            windowRect.left = anchorRect.left
            windowRect.right = anchorRect.right
        }
        else if (popupGravity xor Gravity.START == 0) {
            windowRect.left = anchorRect.left
            windowRect.right = anchorRect.left + width
        }
        else if (popupGravity xor Gravity.END == 0) {
            windowRect.right = anchorRect.right
            windowRect.left = anchorRect.right - width
        }
        else {
            windowRect.left = anchorRect.centerX() - width / 2
            windowRect.right = anchorRect.centerX() + width / 2
        }
    }
    windowRect.left += marginLeft
    windowRect.right -= marginRight
    windowRect.top = anchorRect.top + offsetY - marginBottom
    windowRect.bottom = windowRect.top + height
}

/**
 * Determine the popup's position based on visible space if not set explicitly.
 */
internal fun SuggestionWindow.determinePosition() {
    measureWindowVisibleFrame()
    updateAnchorRect()
    if (preferredPosition == SuggestionWindow.PreferredPosition.BEST_FIT) {
        determinedPosition = if (anchorRect.top + marginTop > windowVisibleFrame.height() / 2) {
            SuggestionWindow.PreferredPosition.ABOVE
        }
        else {
            SuggestionWindow.PreferredPosition.BELOW
        }
    }
}