package br.edu.divulgaambulantes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    private String fullName;
    private String username;
    private String email;
    private String phone;
    private String avatarUrl;
    private String bio;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "selling_status")
    private String sellingStatus;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_banned")
    private Boolean isBanned;

    private String city;
    private String state;
    private String country;

    @Column(name = "avg_rating")
    private BigDecimal avgRating;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @Column(name = "auth_user_id")
    private String authUserId;

    private String cpf;

    @Column(name = "privacy_accepted")
    private Boolean privacyAccepted;

    @Column(name = "privacy_accepted_at")
    private LocalDateTime privacyAcceptedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Relacionamentos
    @OneToMany(mappedBy = "seller")
    private List<Product> products;

    @OneToMany(mappedBy = "reviewer")
    private List<Review> reviewsGiven;

    @OneToMany(mappedBy = "seller")
    private List<Review> reviewsReceived;

    @OneToMany(mappedBy = "buyer")
    private List<Favorite> favorites;

    @OneToOne(mappedBy = "user")
    private Location location;
}