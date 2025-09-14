package net.exoad.filewatch.ui

import com.formdev.flatlaf.extras.FlatSVGIcon
import net.exoad.filewatch.FileWatch
import net.exoad.filewatch.utils.Logger
import java.awt.*
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.io.File
import javax.swing.*
import javax.swing.Box.*
import javax.swing.border.Border
import javax.swing.border.EmptyBorder
import javax.swing.filechooser.FileFilter

fun color(rgb: Int): Color {
    return Color(rgb)
}

fun JComponent.repaintLater() {
    repaint(75L)
}

//fun JFrame.invokeLater(invokable: () -> Unit)
//{
//    if(this.isVisible || this.isShowing)
//    {
//        invokable()
//    }
//}
//
//fun JDialog.invokeLater(invokable: () -> Unit)
//{
//    if(this.isVisible || this.isShowing)
//    {
//        invokable()
//    }
//}

class UIBuildException(message: String) : RuntimeException(message)

enum class Alignment(val componentValue: Float, val swingConstant: Int) {
    TOP(TOP_ALIGNMENT, SwingConstants.TOP),
    BOTTOM(BOTTOM_ALIGNMENT, SwingConstants.BOTTOM),
    CENTER(CENTER_ALIGNMENT, SwingConstants.CENTER),
    LEFT(LEFT_ALIGNMENT, SwingConstants.LEFT),
    RIGHT(RIGHT_ALIGNMENT, SwingConstants.RIGHT)
}

enum class Axis(val componentValue: Int) {
    VERTICAL(SwingConstants.VERTICAL),
    HORIZONTAL(SwingConstants.HORIZONTAL)
}

class Modifier {
    var padding: Border? = null
    var size: Dim? = null
    var tooltip: String? = null
    var maxSize: Dim? = null
    var minSize: Dim? = null
    var background: Color? = null
    var foreground: Color? = null
    var border: Border? = null
    var visible: Boolean? = null
    var enabled: Boolean? = null
    var opaque: Boolean? = null
    var alignmentX: Alignment? = null
    var alignmentY: Alignment? = null
}

fun Component.applyModifier(modifier: Modifier?) {
    if (this is JComponent && modifier != null) {
        modifier.padding?.let { border = it }
        modifier.size?.let {
            size = it
            preferredSize = it
        }
        modifier.tooltip?.let { toolTipText = it }
        modifier.maxSize?.let { maximumSize = it }
        modifier.minSize?.let { minimumSize = it }
        modifier.background?.let { background = it }
        modifier.foreground?.let { foreground = it }
        modifier.border?.let { border = it }
        modifier.visible?.let { isVisible = it }
        modifier.enabled?.let { isEnabled = it }
        modifier.opaque?.let { isOpaque = it }
        modifier.alignmentX?.let { alignmentX = it.componentValue }
        modifier.alignmentY?.let { alignmentX = it.componentValue }
    }
}

fun image(loc: String): ImageIcon {
    return ImageIcon(FileWatch.getResource(loc))
}

fun frame(title: String): JFrame {
    return JFrame(title).apply {
        iconImage = image("/logo.png").image
    }
}

fun nullPanel(modifier: Modifier? = null, color: Int): JComponent {
    return customPainter(modifier) { g, dim ->
        g.color = color(color)
        g.fillRect(0, 0, dim.width, dim.height)
        g.dispose()
    }
}

fun <E> listBuilder(items: ListModel<E>, modifier: Modifier? = null, cellRenderer: ListCellRenderer<E>? = null):
        JList<E> {
    return JList<E>(items).apply {
        cellRenderer?.let { this.cellRenderer = it }
        applyModifier(modifier)
    }
}

fun splitPane(
    first: SingleChildScope.() -> Unit,
    second: SingleChildScope.() -> Unit,
    axis: Axis = Axis.HORIZONTAL,
    dividerPosition: Int? = null,
    modifier: Modifier? = null,
): JSplitPane {
    val scopeFirst = SingleChildScope()
    val scopeSecond = SingleChildScope()
    scopeFirst.first()
    scopeSecond.second()
    return JSplitPane(
        when (axis) {
            Axis.VERTICAL -> Axis.HORIZONTAL.componentValue
            Axis.HORIZONTAL -> Axis.VERTICAL.componentValue
        },
        scopeFirst.child,
        scopeSecond.child
    ).apply {
        dividerPosition?.let { dividerLocation = it }
        applyModifier(modifier)
    }
}

fun hSpacer(): Component {
    return createHorizontalGlue()
}

fun vSpacer(): Component {
    return createVerticalGlue()
}

fun padNone(): EmptyBorder {
    return BorderFactory.createEmptyBorder() as EmptyBorder
}

fun padSym(h: Int = 0, v: Int = 0): EmptyBorder {
    return BorderFactory.createEmptyBorder(v, h, v, h) as EmptyBorder
}

fun padAll(value: Int): EmptyBorder {
    return BorderFactory.createEmptyBorder(value, value, value, value) as EmptyBorder
}

fun padOnly(top: Int = 0, bottom: Int = 0, left: Int = 0, right: Int = 0): EmptyBorder {
    return BorderFactory.createEmptyBorder(top, left, bottom, right) as EmptyBorder
}

fun rowLayout(target: JComponent): BoxLayout {
    return BoxLayout(target, BoxLayout.X_AXIS)
}

fun colLayout(target: JComponent): BoxLayout {
    return BoxLayout(target, BoxLayout.Y_AXIS)
}

fun svg(path: String, width: Int = 16, height: Int = 16): FlatSVGIcon {
    return FlatSVGIcon(path, width, height)
}

// ui components
fun button(
    text: String,
    modifier: Modifier? = null,
    outlined: Boolean = false,
    icon: Icon? = null,
    boldedLabel: Boolean = false,
    backgroundColor: Color? = null,
    foregroundColor: Color? = null,
    tooltip: String? = null,
    onClick: ActionListener? = null,
): JButton {
    return JButton(text).apply {
        if (outlined) {
            background = Color(0, 0, 0, 0)
        }
        tooltip?.let { toolTipText = it }
        icon?.let { this.icon = it }
        backgroundColor?.let { this.background = backgroundColor }
        foregroundColor?.let { this.foreground = foregroundColor }
        if (boldedLabel) {
            this.font = this.font.deriveFont(Font.BOLD)
        }
        onClick?.let { addActionListener(it) }
        applyModifier(modifier)
    }
}

fun hStrut(space: Int): Component {
    return createHorizontalStrut(space)
}

fun vStrut(space: Int): Component {
    return createVerticalStrut(space)
}

fun label(
    text: String,
    hAlignment: Alignment? = null,
    modifier: Modifier? = null,
    fontSize: Float = 16F,
    bolded: Boolean = false,
): JLabel {
    return JLabel(text).apply {
        font = font.deriveFont(fontSize)
        if (bolded) {
            font = font.deriveFont(Font.BOLD)
        }
        hAlignment?.let {
            horizontalAlignment = when (it) {
                Alignment.LEFT -> SwingConstants.LEFT
                Alignment.RIGHT -> SwingConstants.RIGHT
                Alignment.CENTER -> SwingConstants.CENTER
                else -> throw UIBuildException(
                    "Horizontal Alignment Value '$it' is not allowed to be supplied as 'horizontalAlignment' for a label."
                )
            }
        }
        applyModifier(modifier)
    }
}

fun marquee(baseText: String, displayLength: Int, rate: Int = 14, modifier: Modifier? = null): Marquee {
    return Marquee(baseText, displayLength, rate).apply {
        applyModifier(modifier)
    }
}

class Marquee(
    baseText: String,
    private val displayLength: Int,
    rate: Int,
) : JPanel(), ActionListener {
    private val timer: Timer = Timer(1000 / rate, this)
    private val label: JLabel = JLabel()
    private val paddedText: String
    private var index: Int = 0

    init {
        val padding = " ".repeat(displayLength)
        paddedText = padding + baseText + padding
        label.text = paddedText.substring(0, displayLength)
        add(label)
    }

    fun start() {
        if (!timer.isRunning) {
            timer.start()
        }
    }

    fun stop() {
        if (timer.isRunning) {
            timer.stop()
        }
    }

    override fun actionPerformed(e: ActionEvent?) {
        index = (index + 1) % (paddedText.length - displayLength + 1)
        label.text = paddedText.substring(index, index + displayLength)
    }
}

fun scaffold(
    modifier: Modifier? = null,
    north: (SingleChildScope.() -> Unit)? = null,
    center: (SingleChildScope.() -> Unit)? = null,
    south: (SingleChildScope.() -> Unit)? = null,
    west: (SingleChildScope.() -> Unit)? = null,
    east: (SingleChildScope.() -> Unit)? = null,
): JPanel {
    return JPanel(BorderLayout()).apply {
        north?.let {
            val scope = SingleChildScope()
            scope.it()
            scope.child?.let { component -> add(component, BorderLayout.NORTH) }
        }
        center?.let {
            val scope = SingleChildScope()
            scope.it()
            scope.child?.let { component -> add(component, BorderLayout.CENTER) }
        }
        south?.let {
            val scope = SingleChildScope()
            scope.it()
            scope.child?.let { component -> add(component, BorderLayout.SOUTH) }
        }
        west?.let {
            val scope = SingleChildScope()
            scope.it()
            scope.child?.let { component -> add(component, BorderLayout.WEST) }
        }
        east?.let {
            val scope = SingleChildScope()
            scope.it()
            scope.child?.let { component -> add(component, BorderLayout.EAST) }
        }
        applyModifier(modifier)
    }
}

fun customPainter(
    modifier: Modifier? = null,
    painter: (Graphics2D, Dim) -> Unit,
): JComponent {
    return object : JComponent() {
        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)
            val g2d = g as Graphics2D
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            painter(g2d, dim(width, height))
        }
    }.apply {
        applyModifier(modifier)
    }
}

fun iconButton(
    icon: Icon,
    modifier: Modifier? = null,
    tooltip: String? = null,
    onClick: ActionListener? = null,
): JButton {
    return JButton(icon).apply {
        tooltip?.let { toolTipText = it }
        onClick?.let { addActionListener(it) }
        applyModifier(modifier)
    }
}

fun textPane(
    initialText: String = "",
    modifier: Modifier? = null,
    type: String = "text/html",
    editable: Boolean = true,
): JTextPane {
    return JTextPane().apply {
        text = initialText
        this.contentType = type
        isEditable = editable
        applyModifier(modifier)
    }
}

fun editorPane(
    initialText: String = "",
    modifier: Modifier? = null,
    contentType: String = "text/html",
    editable: Boolean = true,
): JEditorPane {
    return JEditorPane().apply {
        text = initialText
        this.contentType = contentType
        isEditable = editable
        applyModifier(modifier)
    }
}

fun textArea(
    initialText: String = "",
    rows: Int = 5,
    columns: Int = 20,
    modifier: Modifier? = null,
): JTextArea {
    return JTextArea(initialText, rows, columns).apply {
        applyModifier(modifier)
    }
}

data class ToggleButtonState(
    val label: String? = null,
    val icon: Icon? = null,
    val tooltip: String? = null,
    val foregroundColor: Color? = null,
    val backgroundColor: Color? = null,
    val boldedLabel: Boolean = false,
) : InternalState<JToggleButton> {
    override fun applyTo(component: JToggleButton) {
        icon?.let { component.icon = it }
        label?.let { component.text = label }
        tooltip?.let { component.toolTipText = it }
        foregroundColor?.let { component.foreground = foregroundColor }
        backgroundColor?.let { component.background = backgroundColor }
        if (boldedLabel) {
            component.font = component.font.deriveFont(Font.BOLD)
        } else if (component.font.style == Font.BOLD) {
            component.font = component.font.deriveFont(Font.PLAIN)
        }
    }
}

fun toggleButton(
    modifier: Modifier? = null,
    selected: Boolean = false,
    onChange: (Boolean) -> ToggleButtonState,
): JToggleButton {
    val initialState = onChange(selected)
    return JToggleButton().apply {
        initialState.applyTo(this)
        addItemListener {
            onChange(it.getStateChange() == ItemEvent.SELECTED).applyTo(this)
        }
        applyModifier(modifier)
    }
}

fun progressBar(
    value: Int = 0,
    min: Int = 0,
    max: Int = 100,
    stringPainted: Boolean = false,
    string: String? = null,
    modifier: Modifier? = null,
): JProgressBar {
    return JProgressBar(min, max).apply {
        isStringPainted = stringPainted
        if (string == null && isStringPainted) {
            Logger.I.warning("Null string; enabled string painting. Oversight?")
        }
        string?.let { this.string = it }
        this.value = value
        applyModifier(modifier)
    }
}

fun icon(icon: Icon, modifier: Modifier? = null): JLabel {
    return JLabel(icon, JLabel.CENTER).apply {
        applyModifier(modifier)
    }
}

fun textField(
    initialText: String = "",
    modifier: Modifier? = null,
    onChange: ((String) -> Unit)? = null,
): JTextField {
    return JTextField().apply {
        text = initialText
        onChange?.let { it -> addCaretListener { it(text) } }
        applyModifier(modifier)
    }
}

fun vDivider(
    color: Color = UIManager.getColor("Separator.foreground"),
    thickness: Float = 2F,
): JComponent {
    return customPainter(modifier = Modifier().apply {
        size = dim(Int.MAX_VALUE, 6 + thickness.toInt())
        maxSize = size
        minSize = size
    }) { g, dim ->
        g.color = color
        g.stroke = BasicStroke(thickness)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g.drawLine(dim.width / 2, 0, dim.width / 2, dim.height)
        g.dispose()
    }
}

fun hDivider(
    color: Color = UIManager.getColor("Separator.foreground"),
    thickness: Float = 2F,
): JComponent {
    return customPainter(modifier = Modifier().apply {
        size = dim(Int.MAX_VALUE, 6 + thickness.toInt())
        maxSize = size
        minSize = size
    }) { g, dim ->
        g.color = color
        g.stroke = BasicStroke(thickness)
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE)
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF)
        g.drawLine(0, dim.height / 2, dim.width, dim.height / 2)
        g.dispose()
    }
}

fun viewport(
    modifier: Modifier? = null,
    content: SingleChildScope.() -> Unit,
): JViewport {
    val scope = SingleChildScope()
    scope.content()
    return JViewport().apply {
        scope.child?.let { this.view = it }
        applyModifier(modifier)
    }
}

enum class FilePickerMode {
    FILES,
    DIRECTORIES,
    BOTH
}

fun filePicker(
    allowMultiple: Boolean = false,
    initialStart: File? = null,
    mode: FilePickerMode = FilePickerMode.FILES,
    modifier: Modifier? = null,
    fileFilter: FileFilter? = null,
): JFileChooser {
    return JFileChooser().apply {
        initialStart?.let { currentDirectory = initialStart }
        isMultiSelectionEnabled = allowMultiple
        this.fileFilter = fileFilter
        fileSelectionMode = when (mode) {
            FilePickerMode.FILES -> JFileChooser.FILES_ONLY
            FilePickerMode.DIRECTORIES -> JFileChooser.DIRECTORIES_ONLY
            FilePickerMode.BOTH -> JFileChooser.FILES_AND_DIRECTORIES
        }
        applyModifier(modifier)
    }
}

fun <T> comboBox(
    initialValue: T,
    values: Array<T>,
    modifier: Modifier? = null,
    backgroundColor: Color? = null,
    onChange: ((T, JComboBox<T>) -> Unit)? = null,
): JComboBox<T> {
    return JComboBox(values).apply {
        if (backgroundColor != null) {
            background = backgroundColor
        }
        onChange?.let { it ->
            addActionListener {
                @Suppress("UNCHECKED_CAST") onChange(selectedItem!! as T, this)
            }
        }
        selectedItem = initialValue
        applyModifier(modifier)
    }
}

fun spinner(
    modifier: Modifier? = null,
    initialValue: Long,
    allowNegative: Boolean,
    stepSize: Long = 1,
    onChange: ((Long) -> Unit)? = null,
): JSpinner {
    return JSpinner(
        SpinnerNumberModel(
            initialValue,
            if (allowNegative) Long.MIN_VALUE else 0,
            Long.MAX_VALUE,
            stepSize
        )
    ).apply {
        value = initialValue
        onChange?.let { it ->
            addChangeListener {
                it(value as Long)
            }
        }
        applyModifier(modifier)
    }
}

fun spinner(
    modifier: Modifier? = null,
    initialValue: Double,
    discreteValues: Array<Double>,
    mantissaLength: Int = 2,
    onChange: ((Double) -> Unit)? = null,
): JSpinner {
    return JSpinner(SpinnerListModel(discreteValues)).apply {
        value = initialValue
        onChange?.let { it ->
            addChangeListener {
                it(value as Double)
            }
        }
        setEditor(JSpinner.NumberEditor(this, "#.${"#".repeat(mantissaLength)}"))
        applyModifier(modifier)
    }
}

fun spinner(
    modifier: Modifier? = null,
    initialValue: Long,
    discreteValues: Array<Long>,
    onChange: ((Long) -> Unit)? = null,
): JSpinner {
    return JSpinner(SpinnerListModel(discreteValues)).apply {
        value = initialValue
        onChange?.let { it ->
            addChangeListener {
                it(value as Long)
            }
        }
        applyModifier(modifier)
    }
}

fun spinner(
    modifier: Modifier? = null,
    initialValue: Double,
    allowNegative: Boolean,
    stepSize: Double = 1.0,
    mantissaLength: Int = 2,
    onChange: ((Double) -> Unit)? = null,
): JSpinner {
    return JSpinner(
        SpinnerNumberModel(
            initialValue,
            if (allowNegative) Double.MIN_VALUE else .0,
            Double.MAX_VALUE,
            stepSize
        )
    ).apply {
        value = initialValue
        onChange?.let { it ->
            addChangeListener {
                it(value as Double)
            }
        }
        setEditor(JSpinner.NumberEditor(this, "#.${"#".repeat(mantissaLength)}"))
        applyModifier(modifier)
    }
}

fun checkbox(
    selected: Boolean = false,
    modifier: Modifier? = null,
    onChange: ((Boolean) -> Unit)? = null,
): JCheckBox {
    return JCheckBox().apply {
        isSelected = selected
        onChange?.let { it ->
            addItemListener {
                it(isSelected)
            }
        }
        applyModifier(modifier)
    }
}

fun scrollPane(
    modifier: Modifier? = null,
    content: SingleChildScope.() -> Unit,
): JScrollPane {
    val scope = SingleChildScope()
    scope.content()
    return JScrollPane(scope.child).apply {
        border = BorderFactory.createEmptyBorder()
        applyModifier(modifier)
    }
}

fun col(
    modifier: Modifier? = null,
    spacing: Int = 0,
    content: MultiChildrenScope.() -> Unit,
): JPanel {
    val scope = MultiChildrenScope()
    scope.content()
    return JPanel().apply {
        this.layout = colLayout(this)
        if (spacing > 0) {
            scope.children.forEachIndexed { index, component ->
                add(component)
                if (index < scope.children.size - 1) {
                    add(createVerticalStrut(spacing))
                }
            }
        } else {
            scope.children.forEach { add(it) }
        }
        applyModifier(modifier)
    }
}

fun row(
    modifier: Modifier? = null,
    spacing: Int = 0,
    content: MultiChildrenScope.() -> Unit,
): JPanel {
    val scope = MultiChildrenScope()
    scope.content()
    return JPanel().apply {
        this.layout = rowLayout(this)
        if (spacing > 0) {
            scope.children.forEachIndexed { index, component ->
                add(component)
                if (index < scope.children.size - 1) {
                    add(createHorizontalStrut(spacing))
                }
            }
        } else {
            scope.children.forEach { add(it) }
        }
        applyModifier(modifier)
    }
}

class SingleChildScope {
    private var _child: Component? = null
    val child: Component? get() = _child

    operator fun Component.unaryPlus() {
        _child = this
    }

    fun setChild(component: Component) {
        _child = component
    }
}

class MultiChildrenScope {
    val children = mutableListOf<Component>()

    operator fun Component.unaryPlus() {
        children.add(this)
    }

    operator fun List<Component>.unaryPlus() {
        children.addAll(this)
    }
}

fun panel(
    modifier: Modifier? = null,
    layout: LayoutManager? = null,
    content: SingleChildScope.() -> Unit = {},
): JPanel {
    val scope = SingleChildScope()
    scope.content()
    return JPanel().apply {
        layout?.let { this.layout = layout }
        scope.child?.let { this.add(it) }
        applyModifier(modifier)
    }
}