import java.io.File
import kotlin.reflect.full.declaredMemberProperties

/**
 * A entity of *name*.
 * @author Hisham Elfadhel Ahmed Khartoum
 * This class is to hold the members names and attributes with ability to write nested entities;
 *
 * @param MutableList the type of an attributes in this entity.
 * @param MutableList the type of an entities in this entity.
 * @property name the name of this entity.
 */
class Entity(var name: String) {
    var attributes: MutableList<Attribute> = mutableListOf()
    var entities: MutableList<Entity> = mutableListOf()

    /**
     * nome is required
     */
    init {
        require(name.isNotEmpty())
    }
    /**
     * Remove an [entity] to this entity.
     */
    fun removeEntity(entityToRemove: Entity) {
        entities.remove(entityToRemove)
    }
}
class ComponenteAvaliacao(val nome: String, val peso: Int)
class Fuc(
    var code: String,
    var name: String,
    var remark: String){
    var ects: Double = 0.0
    var evaluation: List<ComponenteAvaliacao> = mutableListOf()
    init{
        require(code.isNotEmpty())
        require(name.isNotEmpty())
    }
}
/**
 * @property name the name of this attribute.
 * @property value the value of this attribute.
 */
class Attribute(var name: String, var value: String) {
    init {
        require(name.isNotEmpty())
        require(value.isNotEmpty())
    }
}


fun toString(entity: Entity): String {
    fun addAttributes(attributes: MutableList<Attribute>): String {
        return attributes.joinToString(" ") { "${it.name}=\"${it.value}\"" }
    }
    fun addEntities(entities: MutableList<Entity>): String {
        return entities.joinToString("\n") { toString(it) }
    }

    val attributesString = if (entity.attributes.isNotEmpty()) {
        " ${addAttributes(entity.attributes)}"
    } else {
        ""
    }
    val entitiesString = if (entity.entities.isNotEmpty()) {
        "\n${addEntities(entity.entities)}\n"
    } else {
        ""
    }
    return "<${entity.name}${attributesString}>\n${entitiesString}</${entity.name}>"
}


fun translate(obj: Any) : Entity {
    val e = Entity(obj::class.simpleName!!)
    e.apply { obj::class.declaredMemberProperties.forEach{
        val prop = it.call(obj)
        if(prop is List<*>) {
            prop.forEach { p ->
                this.entities.add(translate(p!!))
            }
//            this.entities.add(Entity(it.name)).apply { obj::class.declaredMemberProperties.forEach { it.name } }
        }
        else
            this.attributes.add(Attribute(it.name,prop.toString()))
    } }
    return e;
}
fun main() {
    val plano = Entity("plano")
    val fuc = Fuc("fuc","fuc","fuc").apply {  code="M000";name = "fuc";remark = "11.0";ects=11.0;evaluation = listOf(
        ComponenteAvaliacao("Quizzes", 20),
        ComponenteAvaliacao("Projeto", 80)
    )
    }
    plano.entities = mutableListOf(
        Entity("curso").apply { name = "curso"; attributes = mutableListOf(Attribute("nome", "Mestrado em Engenharia Informática")) },
        Entity("fuc").apply { fuc },
        Entity("fuc").apply {
            name = "fuc"
            attributes = mutableListOf(Attribute("codigo", "M4310"))
            entities = mutableListOf(
                Entity("nome").apply { name = "nome"; attributes = mutableListOf(Attribute("nome", "Programação Avançada")) },
                Entity("ects").apply { name = "ects"; attributes = mutableListOf(Attribute("ects", "6.0")) },
                Entity("avaliacao").apply {
                    name = "avaliacao"
                    entities = mutableListOf(
                        Entity("componente").apply {
                            name = "componente"
                            attributes = mutableListOf(Attribute("nome", "Quizzes"), Attribute("peso", "20%"))
                        },
                        Entity("componente").apply {
                            name = "componente"
                            attributes = mutableListOf(Attribute("nome", "Projeto"), Attribute("peso", "80%"))
                        }
                    )
                }
            )
        },
        Entity("tutorial").apply {
            name = "test"
            attributes = mutableListOf(Attribute("codigo", "03782"))
        },
        Entity("fuc").apply {
            name = "fuc"
            attributes = mutableListOf(Attribute("codigo", "03782"))
            entities = mutableListOf(
                Entity("nome").apply { name = "nome"; attributes = mutableListOf(Attribute("nome", "Dissertação")) },
                Entity("ects").apply { name = "ects"; attributes = mutableListOf(Attribute("ects", "42.0")) },
                Entity("avaliacao").apply {
                    name = "avaliacao"
                    entities = mutableListOf(
                        Entity("componente").apply {
                            name = "componente"
                            attributes = mutableListOf(Attribute("nome", "Dissertação"), Attribute("peso", "60%"))
                        },
                        Entity("componente").apply {
                            name = "componente"
                            attributes = mutableListOf(Attribute("nome", "Apresentação"), Attribute("peso", "20%"))
                        },
                        Entity("componente").apply {
                            name = "componente"
                            attributes = mutableListOf(Attribute("nome", "Discussão"), Attribute("peso", "20%"))
                        }
                    )
                }
            )
        }
    )

    // Remove the first FUC entity
//    plano.removeEntity(plano.entities[1])

    val xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n${toString(plano)}"
    File("output.xml").printWriter().use { out -> out.println(xmlString) }

    val e = translate(fuc)
    println(toString(e))
}