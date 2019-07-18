package com.hapex.inventory.service.storage

import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Path

interface StorageService {
    /**
     * Saves the file to storage with given filename
     */
    fun store(file: MultipartFile, filename: String): Boolean

    /**
     * Retrieves list of all stored files
     */
    fun loadAll(): List<String>

    /**
     * Returns system path to stored file
     */
    fun getPath(filename: String): Path

    /**
     * Loads specified file as Spring Resource
     */
    fun loadAsResource(filename: String): Resource

    /**
     * Deletes file with specified filename
     */
    fun delete(filename: String): Boolean

    /**
     * Deletes all files in the store
     */
    fun deleteAll(): Boolean
}