/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lhzkml.jasmine.components.internal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.DefaultFillType
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

internal object Icons {

    internal object AutoMirrored {
        internal object Filled {
            internal val KeyboardArrowLeft: ImageVector
                get() {
                    if (_keyboardArrowLeft != null) {
                        return _keyboardArrowLeft!!
                    }
                    _keyboardArrowLeft =
                        materialIcon(
                            name = "AutoMirrored.Filled.KeyboardArrowLeft",
                            autoMirror = true,
                        ) {
                            materialPath {
                                moveTo(15.41f, 16.59f)
                                lineTo(10.83f, 12.0f)
                                lineToRelative(4.58f, -4.59f)
                                lineTo(14.0f, 6.0f)
                                lineToRelative(-6.0f, 6.0f)
                                lineToRelative(6.0f, 6.0f)
                                lineToRelative(1.41f, -1.41f)
                                close()
                            }
                        }
                    return _keyboardArrowLeft!!
                }

            private var _keyboardArrowLeft: ImageVector? = null

            internal val KeyboardArrowRight: ImageVector
                get() {
                    if (_keyboardArrowRight != null) {
                        return _keyboardArrowRight!!
                    }
                    _keyboardArrowRight =
                        materialIcon(
                            name = "AutoMirrored.Filled.KeyboardArrowRight",
                            autoMirror = true,
                        ) {
                            materialPath {
                                moveTo(8.59f, 16.59f)
                                lineTo(13.17f, 12.0f)
                                lineTo(8.59f, 7.41f)
                                lineTo(10.0f, 6.0f)
                                lineToRelative(6.0f, 6.0f)
                                lineToRelative(-6.0f, 6.0f)
                                lineToRelative(-1.41f, -1.41f)
                                close()
                            }
                        }
                    return _keyboardArrowRight!!
                }

            private var _keyboardArrowRight: ImageVector? = null
        }
    }

    internal object Filled {
        internal val Close: ImageVector
            get() {
                if (_close != null) {
                    return _close!!
                }
                _close =
                    materialIcon(name = "Filled.Close") {
                        materialPath {
                            moveTo(19.0f, 6.41f)
                            lineTo(17.59f, 5.0f)
                            lineTo(12.0f, 10.59f)
                            lineTo(6.41f, 5.0f)
                            lineTo(5.0f, 6.41f)
                            lineTo(10.59f, 12.0f)
                            lineTo(5.0f, 17.59f)
                            lineTo(6.41f, 19.0f)
                            lineTo(12.0f, 13.41f)
                            lineTo(17.59f, 19.0f)
                            lineTo(19.0f, 17.59f)
                            lineTo(13.41f, 12.0f)
                            close()
                        }
                    }
                return _close!!
            }

        private var _close: ImageVector? = null

        internal val Check: ImageVector
            get() {
                if (_check != null) {
                    return _check!!
                }
                _check =
                    materialIcon(name = "Filled.Check") {
                        materialPath {
                            moveTo(9.0f, 16.17f)
                            lineTo(4.83f, 12.0f)
                            lineToRelative(-1.42f, 1.41f)
                            lineTo(9.0f, 19.0f)
                            lineTo(21.0f, 7.0f)
                            lineToRelative(-1.41f, -1.41f)
                            close()
                        }
                    }
                return _check!!
            }

        private var _check: ImageVector? = null

        internal val Add: ImageVector
            get() {
                if (_add != null) {
                    return _add!!
                }
                _add =
                    materialIcon(name = "Filled.Add") {
                        materialPath {
                            moveTo(19.0f, 13.0f)
                            horizontalLineToRelative(-6.0f)
                            verticalLineToRelative(6.0f)
                            horizontalLineToRelative(-2.0f)
                            verticalLineToRelative(-6.0f)
                            horizontalLineTo(5.0f)
                            verticalLineToRelative(-2.0f)
                            horizontalLineToRelative(6.0f)
                            verticalLineTo(5.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(6.0f)
                            horizontalLineToRelative(6.0f)
                            verticalLineToRelative(2.0f)
                            close()
                        }
                    }
                return _add!!
            }

        private var _add: ImageVector? = null

        internal val Edit: ImageVector
            get() {
                if (_edit != null) {
                    return _edit!!
                }
                _edit =
                    materialIcon(name = "Filled.Edit") {
                        materialPath {
                            moveTo(3.0f, 17.25f)
                            verticalLineTo(21.0f)
                            horizontalLineToRelative(3.75f)
                            lineTo(17.81f, 9.94f)
                            lineToRelative(-3.75f, -3.75f)
                            lineTo(3.0f, 17.25f)
                            close()
                            moveTo(20.71f, 7.04f)
                            curveToRelative(0.39f, -0.39f, 0.39f, -1.02f, 0.0f, -1.41f)
                            lineToRelative(-2.34f, -2.34f)
                            curveToRelative(-0.39f, -0.39f, -1.02f, -0.39f, -1.41f, 0.0f)
                            lineToRelative(-1.83f, 1.83f)
                            lineToRelative(3.75f, 3.75f)
                            lineToRelative(1.83f, -1.83f)
                            close()
                        }
                    }
                return _edit!!
            }

        private var _edit: ImageVector? = null

        internal val DateRange: ImageVector
            get() {
                if (_dateRange != null) {
                    return _dateRange!!
                }
                _dateRange =
                    materialIcon(name = "Filled.DateRange") {
                        materialPath {
                            moveTo(9.0f, 11.0f)
                            lineTo(7.0f, 11.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(-2.0f)
                            close()
                            moveTo(13.0f, 11.0f)
                            horizontalLineToRelative(-2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(-2.0f)
                            close()
                            moveTo(17.0f, 11.0f)
                            horizontalLineToRelative(-2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(-2.0f)
                            close()
                            moveTo(19.0f, 4.0f)
                            horizontalLineToRelative(-1.0f)
                            lineTo(18.0f, 2.0f)
                            horizontalLineToRelative(-2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(8.0f, 4.0f)
                            lineTo(8.0f, 2.0f)
                            lineTo(6.0f, 2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(5.0f, 4.0f)
                            curveToRelative(-1.11f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                            lineTo(3.0f, 20.0f)
                            curveToRelative(0.0f, 1.1f, 0.89f, 2.0f, 2.0f, 2.0f)
                            horizontalLineToRelative(14.0f)
                            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                            lineTo(21.0f, 6.0f)
                            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                            close()
                            moveTo(19.0f, 20.0f)
                            lineTo(5.0f, 20.0f)
                            lineTo(5.0f, 9.0f)
                            horizontalLineToRelative(14.0f)
                            verticalLineToRelative(11.0f)
                            close()
                        }
                    }
                return _dateRange!!
            }

        private var _dateRange: ImageVector? = null

        internal val ArrowDropDown: ImageVector
            get() {
                if (_arrowDropDown != null) {
                    return _arrowDropDown!!
                }
                _arrowDropDown =
                    materialIcon(name = "Filled.ArrowDropDown") {
                        materialPath {
                            moveTo(7.0f, 10.0f)
                            lineToRelative(5.0f, 5.0f)
                            lineToRelative(5.0f, -5.0f)
                            close()
                        }
                    }
                return _arrowDropDown!!
            }

        private var _arrowDropDown: ImageVector? = null

        internal val MoreVert: ImageVector
            get() {
                if (_moreVert != null) {
                    return _moreVert!!
                }
                _moreVert =
                    materialIcon(name = "Filled.MoreVert") {
                        materialPath {
                            moveTo(12.0f, 8.0f)
                            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
                            reflectiveCurveToRelative(-2.0f, 0.9f, -2.0f, 2.0f)
                            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
                            close()
                            moveTo(12.0f, 10.0f)
                            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
                            reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
                            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
                            close()
                            moveTo(12.0f, 16.0f)
                            curveToRelative(-1.1f, 0.0f, -2.0f, 0.9f, -2.0f, 2.0f)
                            reflectiveCurveToRelative(0.9f, 2.0f, 2.0f, 2.0f)
                            reflectiveCurveToRelative(2.0f, -0.9f, 2.0f, -2.0f)
                            reflectiveCurveToRelative(-0.9f, -2.0f, -2.0f, -2.0f)
                            close()
                        }
                    }
                return _moreVert!!
            }

        private var _moreVert: ImageVector? = null

        internal val Visibility: ImageVector
            get() {
                if (_visibility != null) {
                    return _visibility!!
                }
                _visibility =
                    materialIcon(name = "Filled.Visibility") {
                        materialPath {
                            moveTo(12.0f, 4.5f)
                            curveTo(7.0f, 4.5f, 2.73f, 7.61f, 1.0f, 12.0f)
                            curveToRelative(1.73f, 4.39f, 6.0f, 7.5f, 11.0f, 7.5f)
                            reflectiveCurveToRelative(9.27f, -3.11f, 11.0f, -7.5f)
                            curveToRelative(-1.73f, -4.39f, -6.0f, -7.5f, -11.0f, -7.5f)
                            close()
                            moveTo(12.0f, 17.0f)
                            curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
                            reflectiveCurveToRelative(2.24f, -5.0f, 5.0f, -5.0f)
                            reflectiveCurveToRelative(5.0f, 2.24f, 5.0f, 5.0f)
                            reflectiveCurveToRelative(-2.24f, 5.0f, -5.0f, 5.0f)
                            close()
                            moveTo(12.0f, 9.0f)
                            curveToRelative(-1.66f, 0.0f, -3.0f, 1.34f, -3.0f, 3.0f)
                            reflectiveCurveToRelative(1.34f, 3.0f, 3.0f, 3.0f)
                            reflectiveCurveToRelative(3.0f, -1.34f, 3.0f, -3.0f)
                            reflectiveCurveToRelative(-1.34f, -3.0f, -3.0f, -3.0f)
                            close()
                        }
                    }
                return _visibility!!
            }

        private var _visibility: ImageVector? = null

        internal val VisibilityOff: ImageVector
            get() {
                if (_visibilityOff != null) {
                    return _visibilityOff!!
                }
                _visibilityOff =
                    materialIcon(name = "Filled.VisibilityOff") {
                        materialPath {
                            moveTo(12.0f, 6.5f)
                            curveToRelative(3.79f, 0.0f, 7.17f, 2.13f, 8.82f, 5.5f)
                            curveToRelative(-0.7f, 1.43f, -1.79f, 2.61f, -3.07f, 3.45f)
                            lineToRelative(1.43f, 1.43f)
                            curveToRelative(1.71f, -1.41f, 3.07f, -3.08f, 3.82f, -4.88f)
                            curveToRelative(-1.73f, -4.39f, -6.0f, -7.5f, -11.0f, -7.5f)
                            curveToRelative(-1.4f, 0.0f, -2.74f, 0.25f, -3.98f, 0.7f)
                            lineToRelative(1.65f, 1.65f)
                            curveToRelative(0.74f, -0.22f, 1.52f, -0.35f, 2.33f, -0.35f)
                            close()
                            moveTo(2.01f, 3.87f)
                            lineToRelative(2.68f, 2.68f)
                            curveTo(3.06f, 7.83f, 1.77f, 9.7f, 1.0f, 12.0f)
                            curveToRelative(1.73f, 4.39f, 6.0f, 7.5f, 11.0f, 7.5f)
                            curveToRelative(1.52f, 0.0f, 2.98f, -0.29f, 4.32f, -0.82f)
                            lineToRelative(3.42f, 3.42f)
                            lineToRelative(1.41f, -1.41f)
                            lineTo(3.42f, 2.45f)
                            lineTo(2.01f, 3.87f)
                            close()
                            moveTo(7.53f, 9.39f)
                            lineToRelative(1.55f, 1.55f)
                            curveToRelative(-0.05f, 0.34f, -0.08f, 0.7f, -0.08f, 1.06f)
                            curveToRelative(0.0f, 1.66f, 1.34f, 3.0f, 3.0f, 3.0f)
                            curveToRelative(0.36f, 0.0f, 0.71f, -0.06f, 1.04f, -0.18f)
                            lineToRelative(1.55f, 1.55f)
                            curveToRelative(-0.77f, 0.39f, -1.64f, 0.63f, -2.59f, 0.63f)
                            curveToRelative(-2.76f, 0.0f, -5.0f, -2.24f, -5.0f, -5.0f)
                            curveToRelative(0.0f, -0.95f, 0.24f, -1.82f, 0.63f, -2.61f)
                            close()
                            moveTo(11.84f, 9.02f)
                            lineToRelative(3.15f, 3.15f)
                            lineToRelative(0.02f, -0.16f)
                            curveToRelative(0.0f, -1.66f, -1.34f, -3.0f, -3.0f, -3.0f)
                            lineToRelative(-0.17f, 0.01f)
                            close()
                        }
                    }
                return _visibilityOff!!
            }

        private var _visibilityOff: ImageVector? = null
    }

    internal object Outlined {

        val Schedule: ImageVector
            get() {
                if (_schedule != null) {
                    return _schedule!!
                }
                _schedule =
                    materialIcon(name = "Outlined.Schedule") {
                        materialPath {
                            moveTo(11.99f, 2.0f)
                            curveTo(6.47f, 2.0f, 2.0f, 6.48f, 2.0f, 12.0f)
                            reflectiveCurveToRelative(4.47f, 10.0f, 9.99f, 10.0f)
                            curveTo(17.52f, 22.0f, 22.0f, 17.52f, 22.0f, 12.0f)
                            reflectiveCurveTo(17.52f, 2.0f, 11.99f, 2.0f)
                            close()
                            moveTo(12.0f, 20.0f)
                            curveToRelative(-4.42f, 0.0f, -8.0f, -3.58f, -8.0f, -8.0f)
                            reflectiveCurveToRelative(3.58f, -8.0f, 8.0f, -8.0f)
                            reflectiveCurveToRelative(8.0f, 3.58f, 8.0f, 8.0f)
                            reflectiveCurveToRelative(-3.58f, 8.0f, -8.0f, 8.0f)
                            close()
                            moveTo(12.5f, 7.0f)
                            lineTo(11.0f, 7.0f)
                            verticalLineToRelative(6.0f)
                            lineToRelative(5.25f, 3.15f)
                            lineToRelative(0.75f, -1.23f)
                            lineToRelative(-4.5f, -2.67f)
                            close()
                        }
                    }
                return _schedule!!
            }

        private var _schedule: ImageVector? = null

        val Keyboard: ImageVector
            get() {
                if (_keyboard != null) {
                    return _keyboard!!
                }
                _keyboard =
                    materialIcon(name = "Outlined.Keyboard") {
                        materialPath {
                            moveTo(20.0f, 7.0f)
                            verticalLineToRelative(10.0f)
                            lineTo(4.0f, 17.0f)
                            lineTo(4.0f, 7.0f)
                            horizontalLineToRelative(16.0f)
                            moveToRelative(0.0f, -2.0f)
                            lineTo(4.0f, 5.0f)
                            curveToRelative(-1.1f, 0.0f, -1.99f, 0.9f, -1.99f, 2.0f)
                            lineTo(2.0f, 17.0f)
                            curveToRelative(0.0f, 1.1f, 0.9f, 2.0f, 2.0f, 2.0f)
                            horizontalLineToRelative(16.0f)
                            curveToRelative(1.1f, 0.0f, 2.0f, -0.9f, 2.0f, -2.0f)
                            lineTo(22.0f, 7.0f)
                            curveToRelative(0.0f, -1.1f, -0.9f, -2.0f, -2.0f, -2.0f)
                            close()
                            moveTo(11.0f, 8.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                            moveTo(11.0f, 11.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                            moveTo(8.0f, 8.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(8.0f, 10.0f)
                            close()
                            moveTo(8.0f, 11.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(8.0f, 13.0f)
                            close()
                            moveTo(5.0f, 11.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(5.0f, 13.0f)
                            close()
                            moveTo(5.0f, 8.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(5.0f, 10.0f)
                            close()
                            moveTo(8.0f, 14.0f)
                            horizontalLineToRelative(8.0f)
                            verticalLineToRelative(2.0f)
                            lineTo(8.0f, 16.0f)
                            close()
                            moveTo(14.0f, 11.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                            moveTo(14.0f, 8.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                            moveTo(17.0f, 11.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                            moveTo(17.0f, 8.0f)
                            horizontalLineToRelative(2.0f)
                            verticalLineToRelative(2.0f)
                            horizontalLineToRelative(-2.0f)
                            close()
                        }
                    }
                return _keyboard!!
            }

        private var _keyboard: ImageVector? = null
    }
}

private inline fun materialIcon(
    name: String,
    block: ImageVector.Builder.() -> ImageVector.Builder,
): ImageVector =
    ImageVector.Builder(
            name = name,
            defaultWidth = MaterialIconDimension.dp,
            defaultHeight = MaterialIconDimension.dp,
            viewportWidth = MaterialIconDimension,
            viewportHeight = MaterialIconDimension,
        )
        .block()
        .build()

private inline fun materialIcon(
    name: String,
    autoMirror: Boolean = false,
    block: ImageVector.Builder.() -> ImageVector.Builder,
): ImageVector =
    ImageVector.Builder(
            name = name,
            defaultWidth = MaterialIconDimension.dp,
            defaultHeight = MaterialIconDimension.dp,
            viewportWidth = MaterialIconDimension,
            viewportHeight = MaterialIconDimension,
            autoMirror = autoMirror,
        )
        .block()
        .build()

private inline fun ImageVector.Builder.materialPath(
    fillAlpha: Float = 1f,
    strokeAlpha: Float = 1f,
    pathFillType: PathFillType = DefaultFillType,
    pathBuilder: PathBuilder.() -> Unit,
) =
    path(
        fill = SolidColor(Color.Black),
        fillAlpha = fillAlpha,
        stroke = null,
        strokeAlpha = strokeAlpha,
        strokeLineWidth = 1f,
        strokeLineCap = StrokeCap.Butt,
        strokeLineJoin = StrokeJoin.Bevel,
        strokeLineMiter = 1f,
        pathFillType = pathFillType,
        pathBuilder = pathBuilder,
    )

// All Material icons (currently) are 24dp by 24dp, with a viewport size of 24 by 24.
private const val MaterialIconDimension = 24f
