package com.game.bigwheel.test;

import java.lang.reflect.Field;

/**
 * Jackson 역직렬화 성능 비교 테스트
 * - Setter 방식 vs Reflection 방식
 *
 * 실행 방법: 이 파일의 main 메서드 직접 실행
 */
public class JacksonPerformanceTest {

    // 테스트용 DTO (Setter 방식)
    static class DtoWithSetter {
        private String name;
        private int age;
        private String email;

        public void setName(String name) { this.name = name; }
        public void setAge(int age) { this.age = age; }
        public void setEmail(String email) { this.email = email; }
    }

    // 테스트용 DTO (Reflection 방식)
    static class DtoWithReflection {
        private String name;
        private int age;
        private String email;

        public DtoWithReflection() {} // NoArgsConstructor
    }

    public static void main(String[] args) throws Exception {
        int iterations = 1_000_000; // 100만 번 반복

        System.out.println("=== Jackson 역직렬화 성능 비교 ===");
        System.out.println("반복 횟수: " + String.format("%,d", iterations) + "회\n");

        // 1. Setter 방식 테스트
        long setterStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            DtoWithSetter dto = new DtoWithSetter();
            dto.setName("홍길동");
            dto.setAge(25);
            dto.setEmail("test@example.com");
        }
        long setterEnd = System.nanoTime();
        long setterTime = setterEnd - setterStart;

        // 2. Reflection 방식 테스트
        long reflectionStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            DtoWithReflection dto = new DtoWithReflection();

            // Reflection으로 필드 접근
            Field nameField = DtoWithReflection.class.getDeclaredField("name");
            Field ageField = DtoWithReflection.class.getDeclaredField("age");
            Field emailField = DtoWithReflection.class.getDeclaredField("email");

            nameField.setAccessible(true);
            ageField.setAccessible(true);
            emailField.setAccessible(true);

            nameField.set(dto, "홍길동");
            ageField.set(dto, 25);
            emailField.set(dto, "test@example.com");
        }
        long reflectionEnd = System.nanoTime();
        long reflectionTime = reflectionEnd - reflectionStart;

        // 결과 출력
        System.out.println("📊 성능 측정 결과:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("1. Setter 방식");
        System.out.println("   - 총 소요 시간: " + String.format("%,d", setterTime / 1_000_000) + " ms");
        System.out.println("   - 평균 시간: " + String.format("%.2f", (double) setterTime / iterations) + " ns/회");
        System.out.println();

        System.out.println("2. Reflection 방식");
        System.out.println("   - 총 소요 시간: " + String.format("%,d", reflectionTime / 1_000_000) + " ms");
        System.out.println("   - 평균 시간: " + String.format("%.2f", (double) reflectionTime / iterations) + " ns/회");
        System.out.println();

        // 성능 차이 계산
        double performanceDiff = (double) reflectionTime / setterTime;
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📌 결론:");
        System.out.println("   Reflection 방식이 Setter 방식보다");
        System.out.println("   약 " + String.format("%.1f", performanceDiff) + "배 느립니다.");
        System.out.println();
//
//        System.out.println("💡 해석:");
//        if (performanceDiff > 100) {
//            System.out.println("   Reflection은 매우 느립니다!");
//            System.out.println("   하지만 Request DTO는 요청당 1회만 실행되므로");
//            System.out.println("   실무에서는 큰 문제가 되지 않습니다.");
//        } else if (performanceDiff > 10) {
//            System.out.println("   Reflection이 확실히 느리지만,");
//            System.out.println("   요청당 밀리초 단위 차이라 실무에선 무시 가능합니다.");
//        } else {
//            System.out.println("   성능 차이가 크지 않습니다.");
//        }
//
//        System.out.println();
//        System.out.println("🎯 실무 권장:");
//        System.out.println("   - Request DTO: @Setter 사용 (실용성)");
//        System.out.println("   - Response DTO: @Getter만 (불변성)");
//        System.out.println("   - 성능보다 가독성과 유지보수성이 중요!");
    }
}
