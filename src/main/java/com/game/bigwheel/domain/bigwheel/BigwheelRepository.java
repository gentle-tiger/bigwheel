package com.game.bigwheel.domain.bigwheel;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BigwheelRepository extends JpaRepository<Bigwheel, Long> {

  // 특정 사용자의 게임 기록 조회
  List<Bigwheel> findByUserId(Long userId);

  // 최신 게임 기록 순으로 전체 조회
  List<Bigwheel> findAllByOrderByPlayedAtDesc();

  // 특정 사용자의 최신 게임 N개 조회
  List<Bigwheel> findByUserIdAndPlayedAt(Long userId, Timestamp playedAt);


}
