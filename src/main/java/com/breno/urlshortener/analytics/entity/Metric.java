package com.breno.urlshortener.analytics.entity;

import com.breno.urlshortener.url.entity.ShortUrl;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "metrics",
        indexes = {
                @Index(name = "idx_metrics_link_date", columnList = "url_id, clicked_at")
        }
)
public class Metric {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne(fetch = FetchType.LAZY, optional = false)
        @JoinColumn(name = "url_id", nullable = false)
        private ShortUrl urlId;

        @Column(name = "clicked_at", nullable = false)
        private Instant clickedAt;

        @Column(name = "operating_system", length = 50)
        private String os;

        @Column(name = "device_type", length = 30)
        private String deviceType;

        @Column(name = "browser", length = 50)
        private String browser;

        @Column(name = "country", length = 100)
        private String country;

        @Column(name = "state", length = 100)
        private String state;

        @Column(name = "referrer", length = 255)
        private String referrer;

        public Metric(ShortUrl urlId, Instant clickedAt, String os, String deviceType, String browser, String state, String country, String referrer) {
                this.urlId = urlId;
                this.clickedAt = clickedAt;
                this.os = os;
                this.deviceType = deviceType;
                this.browser = browser;
                this.country = country;
                this.state = state;
                this.referrer = referrer;
        }
}
