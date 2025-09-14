package net.exoad.filewatch.ui

class Tag(val name: String) {
    private val children = mutableListOf<String>()
    private val attributes = mutableMapOf<String, String>()

    fun style(vararg styles: Pair<String, Any>) {
        val styleString = styles.joinToString(";") { "${it.first}:${it.second}" }
        attributes["style"] = styleString
    }

    fun add(content: String) {
        children += content
    }

    fun build(): String {
        val attrString = if (attributes.isNotEmpty()) {
            attributes.entries.joinToString(" ") { "${it.key}='${it.value}'" }
        } else {
            ""
        }
        return "<$name${if (attrString.isNotEmpty()) " $attrString" else ""}>${children.joinToString("")}</$name>"
    }

    override fun toString(): String {
        return build()
    }
}

fun html(init: Tag.() -> Unit): String {
    val tag = Tag("html")
    tag.init()
    return tag.toString()
}

fun Tag.u(vararg styles: Pair<String, Any>, init: Tag.() -> Unit) {
    val child = Tag("u")
    if (styles.isNotEmpty()) {
        child.style(*styles)
    }
    child.init()
    add(child.toString())
}

fun Tag.b(vararg styles: Pair<String, Any>, init: Tag.() -> Unit) {
    val child = Tag("b")
    if (styles.isNotEmpty()) {
        child.style(*styles)
    }
    child.init()
    add(child.toString())
}

fun Tag.span(vararg styles: Pair<String, Any>, init: Tag.() -> Unit) {
    val child = Tag("span")
    if (styles.isNotEmpty()) {
        child.style(*styles)
    }
    child.init()
    add(child.toString())
}

fun Tag.em(vararg styles: Pair<String, Any>, init: Tag.() -> Unit) {
    val child = Tag("em")
    if (styles.isNotEmpty()) {
        child.style(*styles)
    }
    child.init()
    add(child.toString())
}

fun Tag.br() {
    add("<br/>")
}

fun Tag.text(content: String) {
    add(content)
}

fun Tag.stripText(content: String) {
    add(
        content
            .replace("\n", "<br/>")
            .replace("\t", "&emsp;")
    )
}
