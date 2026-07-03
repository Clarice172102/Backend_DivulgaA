package br.edu.divulgaambulantes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

//import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    //private String fullName;

   // private String username;

    //private String email;

    //private String phone;

    //private String city;

    //private String state;

    //private String country;

    //private LocalDateTime createdAt;
}