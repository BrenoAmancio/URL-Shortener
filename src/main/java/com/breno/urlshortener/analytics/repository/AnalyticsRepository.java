package com.breno.urlshortener.analytics.repository;

import com.breno.urlshortener.analytics.entity.Metric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AnalyticsRepository extends JpaRepository<Metric, UUID> {
}
