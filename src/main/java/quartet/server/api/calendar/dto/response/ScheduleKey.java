package quartet.server.api.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;

public record ScheduleKey(
            String scheduleType,
            ExamType examType,
            String examRound
) {
    @QueryProjection
    public ScheduleKey {
    }
}