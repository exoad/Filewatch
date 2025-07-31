package net.exoad.filewatch.tests

import net.exoad.filewatch.engine.automation.rule.Action
import net.exoad.filewatch.engine.automation.rule.Condition
import net.exoad.filewatch.ui.visualbuilder.VisualBool
import net.exoad.filewatch.ui.visualbuilder.VisualBuilder
import net.exoad.filewatch.ui.visualbuilder.VisualClass
import net.exoad.filewatch.ui.visualbuilder.VisualPath
import net.exoad.filewatch.ui.visualbuilder.VisualString
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestVisualBuilder
{
    @Test
    fun test0()
    {
        @VisualClass("Test0-Class", "")
        class Test0_1

        @VisualClass("Test0_2-Class", "")
        class Test0_2(@param:VisualString("Test0_2:test0Prop", "") val test0Prop: String)
        assertTrue { VisualBuilder.isBuildable(Test0_1::class) }
        assertTrue { VisualBuilder.isBuildable(Test0_2::class) }
    }

    @Test
    fun test1()
    {
        class Test1_1
        class Test1_2(val a: String, val b: Int)
        assertFalse { VisualBuilder.isBuildable(Test1_1::class) }
        assertFalse { VisualBuilder.isBuildable(Test1_2::class) }
    }

    @Test
    fun test2()
    {
        @VisualClass("Test2-Class", "")
        class Test2_1(val invalidParameter: String, @param:VisualBool("OkBool", true) val okBool: Boolean)
        assertFalse { VisualBuilder.isBuildable(Test2_1::class) }
    }

    @Test
    fun actionsAreSane()
    {
        assertTrue { VisualBuilder.isBuildable(Action.Delete::class) }
        assertTrue { VisualBuilder.isBuildable(Action.Move::class) }
        assertTrue { VisualBuilder.isBuildable(Action.Convert::class) }
        assertTrue { VisualBuilder.isBuildable(Action.Rename::class) }
        assertTrue { VisualBuilder.isBuildable(Action.CompressImage::class) }
        assertTrue { VisualBuilder.isBuildable(Action.ResizeImage::class) }
    }

    @Test
    fun deleteActionHaveCorrectInternals()
    {
        assertEquals(0, VisualBuilder.buildPrototype(Action.Delete::class).fields.size)
        assertEquals("Delete File", VisualBuilder.buildPrototype(Action.Delete::class).visualClass.name)
    }

    @Test
    fun moveActionHaveCorrectInternals()
    {
        val obj = VisualBuilder.buildPrototype(Action.Move::class)
        assertEquals(2, obj.fields.size)
        assertEquals("Move File", obj.visualClass.name)
        // jvm guarantees parameter ordering during invocation
        assertIs<VisualPath>(obj.fields.first())
        assertEquals("Output Folder", (obj.fields.first() as VisualPath).name)
        assertIs<VisualBool>(obj.fields.last())
        assertEquals("Overwrite?", (obj.fields.last() as VisualBool).name)
    }

    @Test
    fun conditionsAreSane()
    {
        assertTrue { VisualBuilder.isBuildable(Condition.FileNameMatches::class) }
        assertTrue { VisualBuilder.isBuildable(Condition.FileNameContains::class) }
        assertTrue { VisualBuilder.isBuildable(Condition.FileSizeLessThan::class) }
        assertTrue { VisualBuilder.isBuildable(Condition.FileSizeGreaterThan::class) }
    }

    @Test
    fun launchConditionVisualClasses()
    {
        // only time when the semicolon is good
        assertDoesNotThrow { VisualBuilder.build(Condition.FileNameMatches::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow {
            VisualBuilder.build(Condition.FileSizeLessThan::class).also { it.showNow(); it.dispose() }
        }
        assertDoesNotThrow {
            VisualBuilder.build(Condition.FileNameContains::class).also { it.showNow(); it.dispose() }
        }
        assertDoesNotThrow {
            VisualBuilder.build(Condition.FileSizeGreaterThan::class).also { it.showNow(); it.dispose() }
        }
    }

    @Test
    fun launchActionVisualClasses()
    {
        assertDoesNotThrow { VisualBuilder.build(Action.Delete::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow { VisualBuilder.build(Action.Move::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow { VisualBuilder.build(Action.Rename::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow { VisualBuilder.build(Action.ResizeImage::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow { VisualBuilder.build(Action.CompressImage::class).also { it.showNow(); it.dispose() } }
        assertDoesNotThrow { VisualBuilder.build(Action.Convert::class).also { it.showNow(); it.dispose() } }
    }
}