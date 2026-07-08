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
@Table(name = "consent_logs")
@Getter
@Setter
public class ConsentLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "consent_type")
    private String consentType;

    private Boolean accepted;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "policy_version")
    private String policyVersion;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}