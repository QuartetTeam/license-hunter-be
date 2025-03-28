package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record SubCategoryResponse(
        long id,
        String name,
        long mainCategoryId
) {
    @QueryProjection
    public SubCategoryResponse {
    }
} 