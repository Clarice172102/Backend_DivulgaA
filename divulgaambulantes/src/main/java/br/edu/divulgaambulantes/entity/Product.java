package br.edu.divulgaambulantes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private String title;
    private String description;
    private String category;
    private String subcategory;

    private BigDecimal price;

    @Column(name = "price_type")
    private String priceType;

    private String currency;

    @Column(columnDefinition = "JSON")
    private String images;

    @Column(name = "thumbnail_url")
    private String thumbnailUrl;

    @Column(name = "video_url")
    private String videoUrl;

    private String city;
    private String state;
    private String country;

    private String status;

    @Column(name = "is_featured")
    private Boolean isFeatured;

    @Column(name = "views_count")
    private Integer viewsCount;

    @Column(name = "likes_count")
    private Integer likesCount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "product")
    private List<Review> reviews;

    @OneToMany(mappedBy = "product")
    private List<Like> likes;

    @OneToMany(mappedBy = "product")
    private List<ProductClick> clicks;
}