package net.exoad.filewatch.ui.visualbuilder

import kotlin.reflect.KClass

data class VisualPrototype<T : Any>(
    val clazz: KClass<T>, val visualClass: VisualClass,
    val fields: Array<Annotation>,
    val recursiveObjectsTable: Map<Annotation, KClass<*>>,
) {
    // == generate ==
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as VisualPrototype<T>
        if (visualClass != other.visualClass) return false
        if (!fields.contentEquals(other.fields)) return false
        return true
    }

    override fun hashCode(): Int {
        var result = visualClass.hashCode()
        result = 31 * result + fields.contentHashCode()
        return result
    }

    override fun toString(): String {
        return "VisualObject[[ ${visualClass.name} ]]{ ${fields.joinToString(", ")} }"
    }
}
