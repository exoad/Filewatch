package net.exoad.filewatch.ui.visualbuilder

import kotlin.reflect.KClass

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualString(val name: String, val defaultValue: String, val hint: String = "")

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualDiscreteString(
    val name: String,
    val defaultValue: String,
    val discreteValues: Array<String>,
    val hint: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualDiscreteLong(
    val name: String,
    val defaultValue: Long,
    val discreteValues: LongArray,
    val hint: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualLong(
    val name: String,
    val defaultValue: Long,
    val allowNegative: Boolean = true,
    val hint: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualDiscreteDouble(
    val name: String,
    val defaultValue: Double,
    val discreteValues: DoubleArray,
    val displayPrecision: Int = 2,
    val hint: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualMultiObject(
    val name: String,
    val hint: String = "",
    val iconPath: String = "",
    val discreteValues: Array<KClass<out Any>>,
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualObject(
    val name: String,
    val hint: String = "",
    val buttonLabel: String,
    val iconPath: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualDouble(
    val name: String,
    val defaultValue: Double,
    val displayPrecision: Int = 2,
    val allowNegative: Boolean = true,
    val hint: String = "",
)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualBool(val name: String, val defaultValue: Boolean, val hint: String = "")
//
//@Target(AnnotationTarget.VALUE_PARAMETER)
//@Retention(AnnotationRetention.RUNTIME)
//annotation class VisualKey(val value: String)

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class VisualPath(
    val name: String,
    val defaultValue: String,
    val hint: String = "",
    val type: Int = Type.BOTH
) {
    object Type {
        const val DIRECTORIES = 1
        const val FILES = 2
        const val BOTH = 3
    }
}