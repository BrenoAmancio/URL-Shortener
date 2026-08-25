package com.breno.urlshortener.url.service;

import com.breno.urlshortener.url.exception.CodeGenerationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CodeService {
    private static final Logger logger = LoggerFactory.getLogger(CodeService.class);

    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = BASE62_ALPHABET.length();

    private static final long MASK_MULTIPLIER = 37L;
    private static final long MASK_INCREMENT = 17L;
    private static final long MASK_MOD = 1L << 32;

    private final JdbcTemplate jdbcTemplate;

    public CodeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String generate() {
        long codeSeq = getCodeSeq();
        long masked = mask(codeSeq);
        String code = encodeBase62(masked);

        logger.debug("Generated short code {} from sequence value {}", code, codeSeq);
        return code;

    }

    private String encodeBase62(long number){
        if (number == 0) {
            return String.valueOf(BASE62_ALPHABET.charAt(0));
        }

        StringBuilder code = new StringBuilder();
        while(number > 0) {
            int remainder = (int) (number % BASE);
            code.append(BASE62_ALPHABET.charAt(remainder));
            number /= BASE;
        }

        return code.reverse().toString();
    }

    private long mask(long seqId) {return (long) ((MASK_MULTIPLIER * seqId + MASK_INCREMENT) % MASK_MOD);}

    private long getCodeSeq() {
        try {
            Long codeSeq = this.jdbcTemplate.queryForObject(
                    "SELECT nextval('short_url_code_seq')",
                    Long.class
            );

            if (codeSeq == null) {
                logger.error("Sequence query returned null value unexpectedly");
                throw new CodeGenerationException(
                        "Sequence returned null value",
                        null
                );
            }
            return codeSeq;
        } catch (DataAccessException e) {
            logger.error("Failed to fetch next value from short_url_code_seq", e);
            throw new CodeGenerationException("Unable to generate short code due to a database error", e);
        }
    }
}
