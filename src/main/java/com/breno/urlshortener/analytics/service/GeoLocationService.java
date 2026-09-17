package com.breno.urlshortener.analytics.service;

import com.breno.urlshortener.analytics.DTO.GeoLocation;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Service
public class GeoLocationService {
    private final Logger logger = LoggerFactory.getLogger(GeoLocationService.class);
    private final DatabaseReader databaseReader;

    public GeoLocationService(DatabaseReader databaseReader) {
        this.databaseReader = databaseReader;
    }

    public GeoLocation getGeoLocation(String ipAddress) {
        try {
            InetAddress ip = InetAddress.getByName(ipAddress);

            CityResponse response = databaseReader.city(ip);
//            return response;
            Country country = response.country();
            Subdivision subdivision = response.mostSpecificSubdivision();

            return new GeoLocation(country.isoCode(), subdivision.isoCode());
        } catch (Exception e) {
            logger.warn("Get geolocation failed: {}", e.getMessage());
            return unknownLocation();
//            return null;
        }
    }

    private GeoLocation unknownLocation() {
        return new GeoLocation(null, null);
    }

    public boolean isValidGeoLocation(GeoLocation geoLocation) {
        return geoLocation.country() != null && geoLocation.state() != null;
    }
}
