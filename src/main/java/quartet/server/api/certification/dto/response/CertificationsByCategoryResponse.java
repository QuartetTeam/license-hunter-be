package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.Instant;

public record CertificationsByCategoryResponse(
        long id,
        String name,
        Instant applicationDate,
        Instant examDate,
        int CalendarSubscriptionCount
) {
    @QueryProjection
    public CertificationsByCategoryResponse(
        long id,
        String name,
        Instant applicationDate,
        Instant examDate,
        int CalendarSubscriptionCount
    ){
        this.id = id;
        this.name = name;
        this.applicationDate = applicationDate;
        this.examDate = examDate;
        this.CalendarSubscriptionCount = CalendarSubscriptionCount;
    }
}
