package com.game.bigwheel.presentation.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class PageResponse<T> {

  private List<T> content;

  private int pageNumber; // 현재 페이지(0)
  private int pageSize; // 페이지 크기
  private long totalElements; // 전체 데이터 개수
  private int totalPages; // 전체 페이지 수

  private boolean first; // 첫 페이지 여부
  private boolean last; // 마지막 페이지 여부
  private boolean hasNext; // 다음 페이지 존재 유므

  public static <T> PageResponse<T> from(Page<T> page) {
    return PageResponse.<T>builder()
        .content(page.getContent())
        .pageNumber(page.getNumber())
        .pageSize(page.getSize())
        .totalElements(page.getTotalElements())
        .totalPages(page.getTotalPages())
        .first(page.isFirst())
        .last(page.isLast())
        .hasNext(page.hasNext())
        .build();
  }
}
