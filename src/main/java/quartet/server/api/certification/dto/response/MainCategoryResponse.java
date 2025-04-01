package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;

public record MainCategoryResponse(
        long id,
        String name,
        boolean isDefault
) {
    @QueryProjection
    public MainCategoryResponse {
    }
} 