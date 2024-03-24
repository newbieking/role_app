package com.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Size(min = 1, max = 10)
    @NotNull
    @NotBlank
    @Column(unique = true)
    private String name;

    public static Role guest() {
        return Role.builder()
                .name("guest")
                .build();
    }

    public static Role admin() {
        return Role.builder()
                .name("admin")
                .build();
    }

    public static Role user() {
        return Role.builder()
                .name("user")
                .build();
    }

    public static Role vip() {
        return Role.builder()
                .name("vip")
                .build();
    }
}
