package com.MT_MX.demo.repository;

import com.MT_MX.demo.entity.TransactionHash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DuplicateCheckSHARepository extends JpaRepository<TransactionHash,String> {
    boolean existsByHash(String hash);
    boolean existsByHashAndDeletedFalseAndCreatedAtAfter(
            String hash,
            LocalDateTime createdAt
    );

}
