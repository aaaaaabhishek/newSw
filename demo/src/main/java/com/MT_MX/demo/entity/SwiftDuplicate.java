//package com.MT_MX.demo.entity;
//
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "swift_duplicate")
//public class SwiftDuplicate {
//
//    @Id
//    @Column(name = "hash", length = 64, nullable = false)
//    private String hash;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "i")
//    public SwiftDuplicate() {}
//
//    public SwiftDuplicate(String hash, LocalDateTime createdAt) {
//        this.hash = hash;
//        this.createdAt = createdAt;
//    }
//
//    public String getHash() {
//        return hash;
//    }
//
//    public void setHash(String hash) {
//        this.hash = hash;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//}
