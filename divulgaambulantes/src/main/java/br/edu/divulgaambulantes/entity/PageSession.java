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
@Table(name = "page_sessions")
@Getter
@Setter
public class PageSession {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String page;

    @Column(name = "ref_id")
    private UUID refId;

    @Column(name = "entered_at")
    private LocalDateTime enteredAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;
}