package com.hapex.inventory.test.unit.data

import com.hapex.inventory.data.entity.Item
import com.hapex.inventory.utils.InvalidValueException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ItemEntityTests {

    @Test
    fun nameSetterTest() {
        val item = Item();
        item.name = "xD";

        assertThat(item.name).isEqualTo("xD");
    }

    @Test(expected = InvalidValueException::class)
    fun invalidNameSetterTest() {
        val item = Item();
        item.name = "  ";
    }

    @Test
    fun quantitySetterTest() {
        val item = Item()
        item.quantity = 5;
        assertThat(item.quantity).isEqualTo(5)

        item.quantity = -5;
        assertThat(item.quantity).isEqualTo(0)
    }
}