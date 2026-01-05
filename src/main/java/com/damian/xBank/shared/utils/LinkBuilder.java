package com.damian.xBank.shared.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LinkBuilder {

    private final Environment env;

    public LinkBuilder(Environment env) {
        this.env = env;
    }

    public String build(String path) {
        String host = env.getProperty("app.frontend.host", "localhost");
        String port = env.getProperty("app.frontend.port", "3000");
        return String.format("http://%s:%s/%s", host, port, path);
    }
}