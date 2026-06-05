package com.MT_MX.demo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transaction_hashes")
public class TransactionHash{

    @Id
    @Column(name = "hash", length = 64)
    private String hash;

    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public TransactionHash(String hash, LocalDateTime now) {
        this.hash=hash;
        this.createdAt=now;
    }

    public TransactionHash() {

    }

    // Getter and Setter for hash
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    // Getter and Setter for transactionId
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    // Getter and Setter for createdAt
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getter and Setter for deleted
    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    // Getter and Setter for deletedAt
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}