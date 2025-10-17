package com.example.demo.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class FilterChainExceptionHandler extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (RuntimeException e){
            ErrorResponse errorResponse = ErrorResponse
                    .builder()
                    .message(e.getMessage())
                    .build();
            response.getWrite().write(convertObjectToJson(errorResponse));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
    public string convertObjectToJson(Object object) throws JsonProcessingException {
        if (object.IsNull(o)) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(object);
    }
}