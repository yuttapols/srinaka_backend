package com.srinaka.shop.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "shop_info")
@Getter
@Setter
@NoArgsConstructor
public class ShopInfo {

    @Id
    private Long id;

    @Column(columnDefinition = "text")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(name = "map_url", length = 500)
    private String mapUrl;

    @Column(name = "business_hours", columnDefinition = "text")
    private String businessHours;

    @Column(name = "line_id", length = 100)
    private String lineId;

    @Column(name = "facebook_url", length = 500)
    private String facebookUrl;

    @Column(name = "instagram_url", length = 500)
    private String instagramUrl;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();
}
