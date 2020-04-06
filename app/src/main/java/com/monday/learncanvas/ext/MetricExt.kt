package com.bytedance.coloring.ext

import android.content.Context
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.AttrRes
import androidx.annotation.Dimension
import androidx.annotation.VisibleForTesting

/**
 * ClassName: MetricExt.kt
 * Description:
 * author 丁健航
 * since 2019-11-26 09:31
 * version 1.0
 */
@VisibleForTesting
var displayMetrics: DisplayMetrics = Resources.getSystem().displayMetrics

/**
 * Get screen display width.
 *
 * @return the absolute width of the available display size in pixels
 */
@Dimension(unit = Dimension.PX)
fun Context.getScreenWidth(): Int = displayMetrics.widthPixels

/**
 * Get screen display width.
 *
 * @return the absolute width of the available display size in pixels
 */
@Dimension(unit = Dimension.PX)
fun View.getScreenWidth(): Int = displayMetrics.widthPixels

/**
 * Get screen display height.
 *
 * @return the absolute height of the available display size in pixels
 */
@Dimension(unit = Dimension.PX)
fun Context.getScreenHeight(): Int = displayMetrics.heightPixels

/**
 * Get screen display height.
 *
 * @return the absolute height of the available display size in pixels
 */
@Dimension(unit = Dimension.PX)
fun View.getScreenHeight(): Int = displayMetrics.heightPixels

/**
 * Converts the `dp` value to pixels dimension.
 *
 * @return the converted `dp` value to integer pixels
 */
val Int.dpToPx: Int
    @Dimension(unit = Dimension.PX) get() = (this * displayMetrics.density).toInt()

/**
 * Converts the `px` value to dp.
 *
 * @return the converted `px` value to dp
 */
val Int.pxToDp: Int
    @Dimension(unit = Dimension.DP) get() = (this / displayMetrics.density).toInt()

/**
 * Converts the `sp` value to pixels dimension.
 *
 * @return the converted `sp` value to pixels
 */
val Int.spToPx: Int
    @Dimension(unit = Dimension.SP) get() = (this * displayMetrics.scaledDensity).toInt()

/**
 * Converts the `dp` value to pixels dimension.
 *
 * @return the converted `dp` value to integer pixels
 */
val Float.dpToPx: Float
    @Dimension(unit = Dimension.PX) get() = (this * displayMetrics.density)

/**
 * Converts the `px` value to dp.
 *
 * @return the converted `px` value to dp
 */
val Float.pxToDp: Float
    @Dimension(unit = Dimension.DP) get() = (this / displayMetrics.density)

/**
 * Converts the `sp` value to pixels dimension.
 *
 * @return the converted `sp` value to pixels
 */
val Float.spToPx: Float
    @Dimension(unit = Dimension.SP) get() = (this * displayMetrics.scaledDensity)

/**
 * Retrieves the toolbar height of an app theme.
 *
 * @param actionBarSize the current [ActionBar] size
 *
 * @return the toolbar height of the current app theme
 */
@Dimension(unit = Dimension.DP)
fun Resources.Theme.toolbarHeight(@AttrRes actionBarSize: Int): Int {
    val styledAttributes = obtainStyledAttributes(intArrayOf(actionBarSize))
    val toolbarHeight = styledAttributes.getDimension(0, 0f).toInt()
    styledAttributes.recycle()
    return toolbarHeight
}
