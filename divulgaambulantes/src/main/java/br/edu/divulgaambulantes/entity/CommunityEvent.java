package br.edu.divulgaambulantes.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "community_events")
@Getter
@Setter
public class CommunityEvent {

    @Id
    @GeneratedValue
    private UUID id;

    private String title;
    private String description;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @Column(name = "location_name")
    private String locationName;

    private String city;
    private String state;

    private Double latitude;
    private Double longitude;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}