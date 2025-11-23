package com.game.bigwheel.domain.user;

import com.game.bigwheel.domain.coin.ChipType;
import com.game.bigwheel.presentation.dto.UserChipsResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class UserService {

  @Autowired
  UserRepository userRepository;

  @Transactional
  public ResponseEntity<String> register(User user)  {

    try{
      if(userRepository.findByUsername(user.getUsername()) != null) {
        userRepository.save(user);
      }

      if(userRepository.existsByNickname(user.getNickname())){
        throw new RuntimeException("이미 사용중인 닉네임입니다.");
      }
    }catch(Exception e){
      log.error(e.getMessage());
      throw new RuntimeException("이미 가입된 유저입니다.");
    }


    return ResponseEntity.ok("회원가입 성공");
  }

  @Transactional
  public User login(String username, String password) {
    log.info("유저 로그인 확인");
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new RuntimeException("가입한 유저가 아닙니다."));

    if (!password.equals(user.getPassword())) {
      throw new RuntimeException("패스워드가 일치하지 않습니다.");
    }
    return user;
  }

  @Transactional(readOnly = true)
  public UserChipsResponse getUserChips(Long userId) {
    log.info("유저 칩 조회");
    User user = userRepository.findByIdWithChips(userId)
        .orElseThrow(() -> new RuntimeException("가지고 있는 칩이 없습니다."));
    UserChipsResponse userChips = UserChipsResponse.from(user);
    return userChips;
  }

  @Transactional
  public UserChipsResponse saveChips(Long userId, Map<ChipType, Integer> chips){

    log.info("유저 칩 저장");
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다: " + userId));

    // 입력값 검증
    chips.forEach((chipType, count) -> {
      if(count <= 0){
        throw new IllegalStateException("칩 개수는 0보다 커야 합니다" + chipType); //이거 도메인 로직 아닌가? 여기 있어도 되나?
      }
    });

    chips.forEach((chipType, count) -> {
      user.addChip(chipType, count);
    });

    // 영속화
    userRepository.save(user);

    return UserChipsResponse.from(user);
  }

}
