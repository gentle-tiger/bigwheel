package com.game.bigwheel.domain.bigwheel;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BigwheelRepository extends JpaRepository<Bigwheel, Long> {

  // 전체 조회 (페이징 없음)
  List<Bigwheel> findByUserId(Long userId);

  // 페이징 조회
  Page<Bigwheel> findByUserId(Long userId, Pageable pageable);

  // 최신 게임 기록 순으로 전체 조회
  List<Bigwheel> findAllByOrderByPlayedAtDesc();

  // 특정 사용자의 최신 게임 N개 조회
  List<Bigwheel> findByUserIdAndPlayedAt(Long userId, Timestamp playedAt);

  // 사용자별 + GameMode 필터링 (최신순)
  List<Bigwheel> findByUserIdAndGameModeOrderByPlayedAtDesc(Long userId, GameMode gameMode);

  // 사용자별 + GameMode 필터링 (최신순, 페이징)
  Page<Bigwheel> findByUserIdAndGameModeOrderByPlayedAtDesc(Long userId, GameMode gameMode, Pageable pageable);





}
