package com.breno.urlshortener.url.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CodeService {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();
    private final JdbcTemplate jdbcTemplate;

    public CodeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private String encodeBase62(long number){
        if (number == 0) {
            return "0";
        }
        StringBuilder code = new StringBuilder();

        while(number > 0) {
            int remainder = (int) (number % BASE);
            code.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return code.reverse().toString();
    }

    public String generate() {
        long code_seq = this.jdbcTemplate.queryForObject(
                "SELECT nextval('short_url_code_seq')",
                Long.class
        );

        return encodeBase62(code_seq);

    }
}
