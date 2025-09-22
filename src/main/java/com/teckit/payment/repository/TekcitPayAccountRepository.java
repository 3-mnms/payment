package com.teckit.payment.repository;

import com.teckit.payment.entity.TekcitPayAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TekcitPayAccountRepository extends JpaRepository<TekcitPayAccount, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from TekcitPayAccount a where a.userId = :userId")
    Optional<TekcitPayAccount> findByIdForUpdate(Long userId);

}
