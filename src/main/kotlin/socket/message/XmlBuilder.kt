package dev.gangster.socket.message

class XmlBuilder(private val name: String) {
    private val attributes = mutableMapOf<String, String>()
    private val children = mutableListOf<XmlBuilder>()
    private var textContent: String? = null

    fun attr(key: String, value: String): XmlBuilder {
        attributes[key] = value
        return this
    }

    fun text(value: String): XmlBuilder {
        textContent = value
        return this
    }

    fun child(name: String, block: XmlBuilder.() -> Unit = {}): XmlBuilder {
        val child = XmlBuilder(name)
        child.block()
        children.add(child)
        return this
    }

    fun build(indent: String = ""): String {
        val attrs = if (attributes.isNotEmpty()) {
            attributes.entries.joinToString(" ") { "${it.key}='${it.value}'" }
        } else ""
        val openTag = if (attrs.isNotEmpty()) "<$name $attrs>" else "<$name>"

        val inner = buildString {
            textContent?.let { append(it) }
            children.forEach { append(it.build(indent + "  ")) }
        }

        return if (inner.isEmpty()) {
            if (attrs.isNotEmpty()) "<$name $attrs/>" else "<$name/>"
        } else {
            "$openTag$inner</$name>"
        }
    }
}