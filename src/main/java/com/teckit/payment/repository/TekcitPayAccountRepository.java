package com.teckit.payment.repository;

import com.teckit.payment.entity.TekcitPayAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TekcitPayAccountRepository extends JpaRepository<TekcitPayAccount, Long> {

}
