package com.hapex.inventory.test.unit.service

import com.hapex.inventory.data.entity.Item
import com.hapex.inventory.data.entity.Photo
import com.hapex.inventory.data.repository.ItemRepository
import com.hapex.inventory.service.CategoryService
import com.hapex.inventory.service.ItemService
import com.hapex.inventory.service.LocationService
import com.hapex.inventory.service.storage.StorageService
import com.hapex.inventory.test.utils.TestUtils.randId
import com.hapex.inventory.utils.ResourceNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class ItemServiceTests {
    @Mock private lateinit var repository: ItemRepository
    @Mock private lateinit var categoryService: CategoryService
    @Mock private lateinit var locationService: LocationService
    @Mock private lateinit var storageService: StorageService

    private lateinit var service: ItemService

    @Before
    fun setUp() {
        service = ItemService(repository, categoryService, locationService, storageService)
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


    @Test
    fun `get photo test`() {
        val item = Item("name")
        item.photo = Photo("abc.jpg");
        given(storageService.loadAsResource(anyString())).willReturn(InputStreamResource(mockFile().inputStream))
        given(repository.findById(anyLong())).willReturn(Optional.of(item))

        assertThat(service.getPhoto(randId())).isPresent
    }

    @Test
    fun `get invalid filename test`() {
        val item = Item("name")
        item.photo = Photo("");
        given(repository.findById(anyLong())).willReturn(Optional.of(item))

        assertThat(service.getPhoto(randId())).isNotPresent
    }

    @Test
    fun `get photo of item without photo test`() {
        given(repository.findById(anyLong())).willReturn(Optional.of(Item("testItem")))
        assertThat(service.getPhoto(randId())).isNotPresent
    }

    @Test
    fun `create photo test`() {
        given(repository.findById(anyLong())).willReturn(Optional.of(Item("abc")))
        given(storageService.store(any(), anyString())).willReturn(true)

        assertThat(service.updatePhoto(randId(), mockFile())).isEqualTo(true)
    }

    @Test
    fun `replace photo test`() {
        val item = Item("name")
        item.photo = Photo("test.png");
        given(storageService.store(any(), anyString())).willReturn(true)
        given(storageService.delete(anyString())).willReturn(true)
        given(repository.findById(anyLong())).willReturn(Optional.of(item))

        assertThat(service.updatePhoto(randId(), mockFile())).isEqualTo(false)
    }

    private fun mockFile() = MockMultipartFile("mock", "mock.jpg", null, null)

    //Needed because Kotlin sucks (but I am too lazy to rewrite to Java)
    //https://medium.com/@elye.project/befriending-kotlin-and-mockito-1c2e7b0ef791
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}