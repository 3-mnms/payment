package com.teckit.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Table(name = "cash_account")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TekcitPayAccount {
    @Id
    @Column(name="user_id")
    private Long userId;

    @Column(name="available_balance",nullable = false)
    private Long availableBalance=0L;

    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void prePersist() {
        this.updatedAt = LocalDateTime.now();
    }

}
