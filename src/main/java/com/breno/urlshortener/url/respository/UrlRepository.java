package com.breno.urlshortener.url.respository;

import com.breno.urlshortener.url.entity.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UrlRepository extends JpaRepository<ShortUrl, UUID> {
    Optional<ShortUrl> findByCode(String code);

    void deleteByCode(String shortCode);
}
