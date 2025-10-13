package com.damian.xBank.modules.user.customer.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Order(1)
@RestControllerAdvice
public class CustomerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(CustomerExceptionHandler.class);

}