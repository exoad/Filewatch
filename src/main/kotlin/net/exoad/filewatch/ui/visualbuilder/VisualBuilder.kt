package net.exoad.filewatch.ui.visualbuilder

import com.formdev.flatlaf.extras.FlatSVGIcon
import net.exoad.filewatch.ui.*
import net.exoad.filewatch.utils.EMPTY
import net.exoad.filewatch.utils.Logger
import net.exoad.filewatch.utils.Theme
import net.exoad.filewatch.utils.truncate
import java.awt.Component
import javax.swing.JFrame
import javax.swing.UIManager
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.primaryConstructor

class VisualBuilder<T : Any>(
    private val visualPrototype: VisualPrototype<T>,
    parent: Component? = null,
) : JFrame() {
    lateinit var onCancel: () -> Unit
    lateinit var onBuild: (T) -> Unit
    val storedParameters = mutableListOf<Any>()

    init {
        iconImage = image("/logo.png").image
        title = visualPrototype.visualClass.name
        defaultCloseOperation = DISPOSE_ON_CLOSE
        contentPane = scaffold(
            Modifier().apply { padding = padAll(8) },
            center = {
                +col {
                    +row {
                        +label(
                            "<html><body style=\"font-size: 10px;\"><b style=\"font-size: 16px;\">${
                                visualPrototype.visualClass.name
                            }</b><br/>${
                                visualPrototype.visualClass.hint
                            }</body></html>",
                            hAlignment = Alignment.LEFT,
                        )
                    }
                    +hDivider()
                    +scrollPane {
                        +col {
                            var i = 0
                            for (field in visualPrototype.fields) {
                                +row(
                                    Modifier().apply { padding = padSym(v = 8) },
                                    content = components(field, i++)
                                )
                            }
                        }
                    }
                }
            },
            south = {
                +row {
                    +hSpacer()
                    +button("Cancel", outlined = true, icon = svg("icons/cancel_circle.svg")) {
                        dispose()
                        if (::onCancel.isInitialized) {
                            onCancel.invoke()
                        }
                    }
                    +button("Apply", icon = svg("icons/check_circle.svg")) {
                        if (::onBuild.isInitialized) {
                            if (visualPrototype.clazz.primaryConstructor == null) {
                                Logger.I.severe(
                                    "It is dangerous to build a non Kotlin Primary Constructor. Defaulting to first class constructor."
                                )
                            }
                            onBuild(
                                visualPrototype.clazz.cast(
                                    (visualPrototype.clazz.primaryConstructor ?: visualPrototype
                                        .clazz
                                        .constructors
                                        .first()).call(*storedParameters.toTypedArray())
                                )
                            )
                        }
                        dispose()
                    }
                }
            }
        )
        pack()
        size = dim(660, preferredSize.height)
        preferredSize = size
        if (parent == null) {
            location = centerScreenRect(size.toRect())
        } else {
            setLocationRelativeTo(parent)
        }
        isAlwaysOnTop = true
    }

    fun <T : Any> addParameterData(type: String, index: Int, value: T) {
        while (storedParameters.size <= index) {
            storedParameters.add(EMPTY)
        }
        storedParameters[index] = value
        Logger.I.info("${hashCode()} VisualBuilder[${visualPrototype.clazz.simpleName!!}] ($type): $index -> $value")
    }

    private fun components(field: Annotation, index: Int): MultiChildrenScope.() -> Unit {
        fun hint(value: String): Component {
            return label(value, fontSize = 12F)
        }
        return when (field) {
            is VisualMultiObject -> {
                addParameterData("MultiObj", index, field.discreteValues.first().primaryConstructor!!.call())
                return {
                    val title = col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    if (field.iconPath.isNotBlank()) {
                        +row {
                            +icon(svg(field.iconPath, 18, 18))
                            +hStrut(8)
                            +title
                        }
                    } else {
                        +title
                    }
                    val visualObjectBuild = remember<VisualBuilder<*>?>(null)
                    fun fieldSubhint(v: String, classAnnot: List<Annotation>): String {
                        return "<html><b>$v</b><br/>${
                            (classAnnot.findLast { it is VisualClass }!! as VisualClass).hint
                        }</html>"
                    }

                    val mapper = mutableMapOf<String, KClass<out Any>>()
                    field.discreteValues.forEach {
                        mapper[fieldSubhint(it.simpleName!!, it.annotations)] = it
                    }
                    +comboBox(
                        fieldSubhint(
                            field.discreteValues.first().simpleName!!,
                            field.discreteValues.first().annotations
                        ),
                        values = mapper.keys.toTypedArray(),
                        onChange = { selectedObj, comboBox ->
                            if (visualObjectBuild() != null) {
                                visualObjectBuild()!!.dispose()
                            }
                            if (mapper[selectedObj]!! != field.discreteValues.first()) {
                                val discreteSelectedObject = build(mapper[selectedObj]!!).apply {
                                    onBuild = { builtObj ->
                                        visualObjectBuild(this)
                                        this@VisualBuilder.addParameterData("MultiObj", index, builtObj)
                                    }
                                    onCancel = {
                                        visualObjectBuild(null)
                                        this@VisualBuilder.addParameterData(
                                            "MultiObj",
                                            index,
                                            field.discreteValues.first().primaryConstructor!!.call()
                                        )
                                        comboBox.selectedItem = fieldSubhint(
                                            field.discreteValues.first().simpleName!!,
                                            field.discreteValues.first().annotations
                                        )
                                    }
                                    setLocationRelativeTo(this@VisualBuilder)
                                }
                                discreteSelectedObject.showNow()
                            }
                        }
                    ).apply {
                        visualObjectBuild.observe {
                            background = if (it == null) null else color(Theme.SUCCESS_COLOR)
                        }
                    }
                }
            }

            is VisualObject -> {
                addParameterData("Object", index, Any())
                return {
                    val title = col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    if (field.iconPath.isNotBlank()) {
                        +row {
                            +icon(svg(field.iconPath, 18, 18))
                            +hStrut(8)
                            +title
                        }
                    } else {
                        +title
                    }
                    val builtVisualObject = remember(false)
                    +(button(
                        if (builtVisualObject()) "${field.name}#${storedParameters[index].hashCode()}"
                        else field.buttonLabel,
                        backgroundColor = if (builtVisualObject()) color(Theme.SUCCESS_COLOR) else null,
                        icon = if (builtVisualObject()) null else svg("icons/create.svg")
                    ) {
                        val targetClass = visualPrototype.recursiveObjectsTable[field]!! // bang operator to catch
                        // stray errors before something further down happens (definitely a programming logic bug)
                        @Suppress("UNCHECKED_CAST")
                        build(targetClass as KClass<Any>).apply {
                            onBuild = { it ->
                                addParameterData("Object", index, it)
                                builtVisualObject(!builtVisualObject())
                            }
                        }.also {
                            it.showNow()
                        }
                    }).apply {
                        builtVisualObject.observe {
                            this.background = if (it) color(Theme.SUCCESS_COLOR)
                            else UIManager.getColor(
                                "Button.background"
                            )
                        }
                    }
                }
            }

            is VisualPath -> {
                addParameterData("Path", index, field.defaultValue)
                return {
                    +col {
                        +label(
                            html {
                                b {
                                    text(field.name)
                                }
                            }
                        )
                        +hint(field.hint)
                    }
                    +col {
                        val selectedPath = remember(field.defaultValue)
                        +label(
                            selectedPath(),
                            fontSize = 12F,
                            modifier = Modifier().apply {
                                alignmentX = Alignment.RIGHT
                            }
                        ).also { label ->
                            selectedPath.observe {
                                label.text = it.truncate(42)
                                label.toolTipText = it
                            }
                        }
                        +hStrut(6)
                        +button(
                            "Open File Picker",
                            modifier = Modifier().apply {
                                tooltip = "Use file picker"
                                alignmentX = Alignment.RIGHT
                            },
                            icon = FlatSVGIcon("icons/folder.svg", 16, 16)
                        ) {
                            val picker = filePicker(
                                allowMultiple = false,
                                modifier = Modifier().apply { size = dim(740, 540) },
                                mode = when (field.type) {
                                    VisualPath.Type.FILES -> FilePickerMode.DIRECTORIES
                                    VisualPath.Type.DIRECTORIES -> FilePickerMode.DIRECTORIES
                                    else -> FilePickerMode.BOTH
                                }
                            ).also {
                                it.showDialog(
                                    this@VisualBuilder,
                                    "Select"
                                )
                            }
                            if (picker.selectedFile == null) {
                                addParameterData("Path", index, ".")
                                selectedPath(".")
                            } else {
                                addParameterData<String>("Path", index, picker.selectedFile.absolutePath)
                                selectedPath(picker.selectedFile.absolutePath)
                            }
                        }
                    }
                }
            }

            is VisualLong -> {
                addParameterData("Long", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +spinner(initialValue = field.defaultValue, allowNegative = field.allowNegative) {
                        addParameterData("Long", index, it)
                    }
                }
            }

            is VisualDiscreteLong -> {
                addParameterData("DiscreteLong", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +spinner(
                        initialValue = field.defaultValue,
                        discreteValues = field.discreteValues.toTypedArray()
                    ) {
                        addParameterData("DiscreteLong", index, it)
                    }
                }
            }

            is VisualDiscreteDouble -> {
                addParameterData("DiscreteDouble", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +spinner(
                        initialValue = field.defaultValue,
                        discreteValues = field.discreteValues.toTypedArray(),
                        mantissaLength = field.displayPrecision
                    ) {
                        addParameterData("DiscreteDouble", index, it)
                    }
                }
            }

            is VisualDouble -> {
                addParameterData("Double", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +spinner(
                        initialValue = field.defaultValue,
                        allowNegative = field.allowNegative,
                        mantissaLength = field.displayPrecision
                    ) {
                        addParameterData("Double", index, it)
                    }
                }
            }

            is VisualDiscreteString -> {
                addParameterData("DiscreteString", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +comboBox(initialValue = field.defaultValue, values = field.discreteValues) { it, _ ->
                        addParameterData("DiscreteString", index, it)
                    }
                }
            }

            is VisualString -> {
                addParameterData("String", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +textField(field.defaultValue) { text ->
                        addParameterData("String", index, text)
                    }.apply {
                        maximumSize = dim(Int.MAX_VALUE, preferredSize.height)
                    }
                }
            }

            is VisualBool -> {
                addParameterData("Bool", index, field.defaultValue)
                return {
                    +col {
                        +label("<html><b>${field.name}</b></html>")
                        +hint(field.hint)
                    }
                    +checkbox(field.defaultValue) { selected ->
                        addParameterData("Bool", index, selected)
                    }
                }
            }

            else -> {
                {
                    +panel()
                    Logger.I.warning("Failed to visually build '$field'. Because it is unsupported.")
                }
            }
        }
    }

    fun showNow() {
        isVisible = true
    }

    companion object {
        private val visualDecorators = arrayOf(
            VisualObject::class,
            VisualLong::class,
            VisualDouble::class,
            VisualPath::class,
            VisualString::class,
            VisualDiscreteString::class,
            VisualDiscreteDouble::class,
            VisualDiscreteLong::class,
            VisualMultiObject::class
        )

        @JvmStatic
        fun isBuildable(clazz: KClass<*>): Boolean {
            if (clazz.annotations.none { it is VisualClass }) {
                return false
            }
            if (clazz.simpleName == null) // anonymous class likely
            {
                Logger.I.warning("An anonymous class cannot be built visually. (simpleName returned null)")
                return false
            }
            val primaryConstructor = clazz.primaryConstructor
            if (primaryConstructor == null || primaryConstructor.parameters.isEmpty()) {
                return true
            }
            return primaryConstructor.parameters.all { param ->
                param.annotations.any {
                    if (it is VisualObject) {
                        return isBuildable(param.type.classifier as KClass<*>)
                    }
                    if (it is VisualMultiObject) {
                        return it.discreteValues.all { discreteClass -> isBuildable(discreteClass) }
                    }
                    return visualDecorators.contains(it.annotationClass)
                }
            }
        }

        @JvmStatic
        fun <T : Any> buildPrototype(clazz: KClass<T>): VisualPrototype<T> {
            assert(isBuildable(clazz)) { "Cannot build non-visual class $clazz." }
            val visualClass = if (clazz.annotations.any { it is VisualClass }) {
                clazz.annotations.find { it is VisualClass } as VisualClass
            } else {
                VisualClass(clazz.simpleName ?: clazz.qualifiedName ?: clazz.toString(), hint = "Unknown?")
            }
            val fields = mutableListOf<Annotation>()
            val recursiveObjectsTable = mutableMapOf<Annotation, KClass<*>>()
            if (clazz.primaryConstructor != null) {
                clazz.primaryConstructor!!.parameters.forEach { param ->
                    param.annotations.forEach { annotation ->
                        if (annotation.annotationClass.simpleName!!.startsWith("Visual")) {
                            if (annotation is VisualObject) {
                                val paramType = param.type.classifier as? KClass<*>
                                if (paramType != null) {
                                    recursiveObjectsTable[annotation] = paramType
                                }
                            }
                            fields.add(param.annotations.find { it.annotationClass.simpleName!!.startsWith("Visual") }!!)
                        }
                    }
                }
            }
            return VisualPrototype(clazz, visualClass, fields.toTypedArray(), recursiveObjectsTable)
        }

        @JvmStatic
        fun <T : Any> build(clazz: KClass<T>): VisualBuilder<T> {
            return VisualBuilder(buildPrototype(clazz))
        }
    }
}