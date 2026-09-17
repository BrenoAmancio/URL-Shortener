package com.breno.urlshortener.config;

import com.maxmind.geoip2.DatabaseReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

@Configuration
public class GeoIpConfig {
    @Bean
    public DatabaseReader databaseReader(@Value("${geoip.database.path}") String databasePath) throws IOException {
        return new DatabaseReader.Builder(new File(databasePath)).build();
    }
}
