package com.game.bigwheel.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("SELECT u "
        + "FROM User u "
        + "LEFT JOIN FETCH u.chips "
        + "WHERE u.id = :userId")
    Optional<User> findByIdWithChips(@Param("userId") Long userId);

    boolean existsByNickname(String nickname);
}
