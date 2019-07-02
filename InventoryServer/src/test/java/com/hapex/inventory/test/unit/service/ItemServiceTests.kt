package com.hapex.inventory.test.unit.service

import com.hapex.inventory.data.entity.Item
import com.hapex.inventory.data.repository.CategoryRepository
import com.hapex.inventory.data.repository.ItemRepository
import com.hapex.inventory.service.ItemService
import com.hapex.inventory.test.utils.TestUtils.randId
import com.hapex.inventory.utils.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ItemServiceTests {
    @Mock private lateinit var repository: ItemRepository
    @Mock private lateinit var categoryRepository: CategoryRepository

    private lateinit var service: ItemService

    @Before
    fun setUp() {
        service = ItemService(repository, categoryRepository)
    }

    @Test
    fun `find details test`() {
        val id = randId()
        val item = Item("atxmega128a1")
        given(repository.findById(eq(id))).willReturn(Optional.of(item))

        val result = service.findById(id)

        assertThat(result.name).isEqualTo("atxmega128a1");
    }

    @Test(expected = ResourceNotFoundException::class)
    fun `item not found test`() {
        given(repository.findById(any())).willReturn(Optional.empty())

        service.findById(randId())
    }

}