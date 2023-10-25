package com.knocknock.domain.category.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FindCategoryListResDto {

    Long categoryId;

    String categoryName;

}
