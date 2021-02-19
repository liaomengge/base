package com.github.liaomengge.service.base_framework.common.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/11/19.
 */
public class GlobalErrorController extends BasicErrorController {

    private ObjectMapper objectMapper;

    public GlobalErrorController() {
        super(new DefaultErrorAttributes(), new ErrorProperties());
    }

    public GlobalErrorController(ObjectMapper objectMapper) {
        this();
        this.objectMapper = objectMapper;
    }

    public GlobalErrorController(ErrorAttributes errorAttributes, ErrorProperties errorProperties,
                                 ObjectMapper objectMapper) {
        super(errorAttributes, errorProperties);
        this.objectMapper = objectMapper;
    }

    @Override
    public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {
        HttpStatus status = getStatus(request);
        if (status == HttpStatus.NO_CONTENT) {
            return new ResponseEntity<>(status);
        }
        Map<String, Object> errorAttributes = getErrorAttributes(request,
                getErrorAttributeOptions(request, MediaType.ALL));
        return new ResponseEntity<>(errorAttributes, status);
    }

    @Override
    public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> errorAttributes = getErrorAttributes(request,
                getErrorAttributeOptions(request, MediaType.ALL));
        HttpStatus status = getStatus(request);
        response.setStatus(status.value());
        MappingJackson2JsonView view = new MappingJackson2JsonView();
        if (Objects.nonNull(objectMapper)) {
            view.setObjectMapper(objectMapper);
        }
        view.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return new ModelAndView(view, errorAttributes);
    }
}
