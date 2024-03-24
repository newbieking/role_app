package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    private Long id;

    @Size(min = 1, max = 16)
    @NotNull
    private String name;

    @Size(min = 8, max = 16)
    @NotNull
    private String password;

    @OneToOne
    @JoinColumn(name = "role_id")
    @NotNull
    private Role role;

}
