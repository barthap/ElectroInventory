package com.hapex.inventory.service.storage

import com.hapex.inventory.config.StorageProperties
import com.hapex.inventory.utils.ResourceNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.FileSystemResource
import org.springframework.core.io.Resource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.nio.file.Path
import java.nio.file.Paths

@Service
class FileSystemStorageService(storageProperties: StorageProperties) : StorageService {
    private val log = LoggerFactory.getLogger(FileSystemStorageService::class.java)

    private val dir = File(storageProperties.rootDirectory);

    override fun store(file: MultipartFile, filename: String): Boolean {
        if (file.isEmpty) {
            log.warn("Error: file is empty!");
            return false;
        }

        try {
            if (!dir.exists()) dir.mkdirs()

            val serverFile = getFile(filename)
            val stream = BufferedOutputStream(FileOutputStream(serverFile))

            stream.write(file.bytes)
            stream.close()

            log.debug("File upload successful at ${serverFile.absolutePath}")
            return true

        } catch (ex: Exception) {
            log.error("Failed to upload file: ${ex.message}")
            return false
        }
    }

    override fun loadAll(): List<String> {
        return dir.listFiles().map { it.name }.toList()
    }

    override fun getPath(filename: String): Path {
        val file = getFile(filename)
        return Paths.get(file.absolutePath)
    }

    override fun loadAsResource(filename: String): Resource {
        val file = getFile(filename)
        if(!file.exists())
            throw ResourceNotFoundException("File $filename not found!")

        return FileSystemResource(file)
    }

    override fun delete(filename: String): Boolean {
        val file = getFile(filename)
        return file.exists() && file.delete()
    }

    override fun deleteAll(): Boolean {
        return dir.exists() && dir.delete();
    }

    private fun getFile(filename: String)
            = File(dir.absolutePath + File.separator + prepareFilename(filename))

    private fun prepareFilename(filename: String) = filename.trim().replace(' ', '_')
}