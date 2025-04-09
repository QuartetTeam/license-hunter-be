package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.Instant;

public record CertificationSearchResponse(
        long id,
        String mainCategory,
        String subCategory,
        String name,
        Instant applicationDate,
        Instant examDate,
        int calendarSubscription
) {
    @QueryProjection
    public CertificationSearchResponse {
    }
} 