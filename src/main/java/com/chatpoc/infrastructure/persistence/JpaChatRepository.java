package com.chatpoc.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaChatRepository extends JpaRepository<ChatEntity, UUID> {
    
    @Query("SELECT c FROM ChatEntity c LEFT JOIN FETCH c.participants LEFT JOIN FETCH c.messages WHERE c.id = :id")
    Optional<ChatEntity> findByIdWithDetails(@Param("id") UUID id);
}