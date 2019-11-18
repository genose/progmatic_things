import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class MyClassTest : MyClass() {

    @BeforeEach
    fun setUp() {
    }

    @AfterEach
    fun tearDown() {
    }


    @Test
    fun helloWorldReturnsPersonalizedMessage() {
        assertEquals(" Hello, Molly!", helloWorld("Molly"))
    }
    @Test
    fun helloWorldReturnsGenericEmptyMessage() {
        assertEquals(" Hello, World!", helloWorld(" "))
    }
    @Test
    fun helloWorldReturnsGenericMessage() {
        assertEquals(" Hello, World!", helloWorld())
    }

    @Test
    fun helloWorldReturnsConvertVoid() {
        assertEquals(" Hello, World!", convert("10.25"))
    }

}