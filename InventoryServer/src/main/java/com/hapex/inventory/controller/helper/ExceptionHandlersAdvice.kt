package com.hapex.inventory.controller.helper

import com.hapex.inventory.utils.ConflictException
import com.hapex.inventory.utils.InvalidValueException
import com.hapex.inventory.utils.ResourceNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.lang.RuntimeException

@RestControllerAdvice
class ExceptionHandlersAdvice : ResponseEntityExceptionHandler() {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(ex: RuntimeException, request: WebRequest): ResponseEntity<Any>
            = handleExceptionInternal(ex, "Error: " + ex.message, HttpHeaders(), HttpStatus.NOT_FOUND, request)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: RuntimeException, request: WebRequest): ResponseEntity<Any>
            = handleExceptionInternal(ex, "Error: " + ex.message, HttpHeaders(), HttpStatus.CONFLICT, request)

    @ExceptionHandler(InvalidValueException::class)
    fun handleInvalidException(ex: RuntimeException, request: WebRequest): ResponseEntity<Any>
            = handleExceptionInternal(ex, "Error: " + ex.message, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
}