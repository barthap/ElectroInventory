package com.hapex.inventory.test.unit.data

import com.hapex.inventory.data.entity.Location
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class LocationEntityTests {

    @Test
    fun fullNameTest() {
        val parent = Location("Parent")
        val child1 = Location("Child 1")
        val child2 = Location("Child 2")

        child1.parent = parent
        child2.parent = child1

        assertThat(parent.fullName).isEqualTo(parent.name)
        assertThat(child1.fullName).isEqualTo("Parent - Child 1")
        assertThat(child2.fullName).isEqualTo("Parent - Child 1 - Child 2")
    }
}
