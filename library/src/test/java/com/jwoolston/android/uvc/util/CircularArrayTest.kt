package com.jwoolston.android.uvc.util

import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * @author Ian Thomas (toxicbakery@gmail.com)
 */
class CircularArrayTest {

    @Test
    fun getSize() {
        val circularArray = CircularArray<Int>(10)
        assertEquals(0, circularArray.size)

        circularArray.add(1)
        assertEquals(1, circularArray.size)

        (0..100).forEach(circularArray::add)
        assertEquals(10, circularArray.size)
    }

    @Test
    fun get() {
        CircularArray<Int>(1).also { circularArray ->
            circularArray.add(1)
            assertEquals(1, circularArray[0])

            circularArray.add(2)
            assertEquals(2, circularArray[0])
        }

        CircularArray<Int>(2).also { circularArray ->
            circularArray.add(1)
            assertEquals(1, circularArray[0])

            circularArray.add(2)
            assertEquals(1, circularArray[0])
            assertEquals(2, circularArray[1])

            circularArray.add(3)
            assertEquals(2, circularArray[0])
            assertEquals(3, circularArray[1])

            circularArray.add(4)
            assertEquals(3, circularArray[0])
            assertEquals(4, circularArray[1])
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getZeroSizeException() {
        CircularArray<Int>(1).get(0)
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getIndexOutOfBounds() {
        CircularArray<Int>(1).also { circularArray ->
            circularArray.add(1)
            circularArray[10]
        }
    }

    @Test(expected = IndexOutOfBoundsException::class)
    fun getIndexNegative() {
        CircularArray<Int>(1).also { circularArray ->
            circularArray.add(1)
            circularArray[-1]
        }
    }

    @Test
    fun toList() {
        CircularArray<Int>(1).also { circularArray ->
            circularArray.add(0)
            assertEquals(listOf(0), circularArray.toList())

            circularArray.add(1)
            assertEquals(listOf(1), circularArray.toList())
        }

        CircularArray<Int>(2).also { circularArray ->
            circularArray.add(0)
            assertEquals(listOf(0), circularArray.toList())

            circularArray.add(1)
            assertEquals(listOf(0, 1), circularArray.toList())

            circularArray.add(2)
            assertEquals(listOf(1, 2), circularArray.toList())
            println("${circularArray[0]}")

            circularArray.add(3)
            assertEquals(listOf(2, 3), circularArray.toList())

            circularArray.add(4)
            assertEquals(listOf(3, 4), circularArray.toList())
        }
    }

    @Test
    fun testClone() {
        CircularArray<Int>(1)
                .also { circularArray -> circularArray.add(1) }
                .clone()
                .also { clonedCircularArray -> assertEquals(1, clonedCircularArray[0]) }

    }

}