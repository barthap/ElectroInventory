package com.hapex.inventory.controller.helper

import org.springframework.http.converter.HttpMessageConverter
import org.springframework.core.MethodParameter
import org.springframework.data.domain.Page
import org.springframework.http.MediaType
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice
import org.springframework.web.bind.annotation.RestControllerAdvice

//@see https://stackoverflow.com/questions/44375435/spring-auto-add-x-total-count-header

@RestControllerAdvice
class PagedResourceSizeAdvice : ResponseBodyAdvice<Any> {


    override fun supports(returnType: MethodParameter, converterType: Class<out HttpMessageConverter<*>>): Boolean {
        //Checks if this advice is applicable.
        //In this case it applies to any endpoint which returns a page.
        return Page::class.java.isAssignableFrom(returnType.parameterType)
    }

    override fun beforeBodyWrite(body: Any?,
                                 returnType: MethodParameter,
                                 selectedContentType: MediaType,
                                 selectedConverterType: Class<out HttpMessageConverter<*>>,
                                 request: ServerHttpRequest,
                                 response: ServerHttpResponse): Any? {
        response.headers.add("X-Total-Count", (body as? Page<*>)?.totalElements.toString())
        return (body as? Page<*>)?.content
    }
}