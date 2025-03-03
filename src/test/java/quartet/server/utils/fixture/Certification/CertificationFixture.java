package quartet.server.utils.fixture.Certification;

import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationsByCategoryResponse;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.certification.type.ProblemType;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

public class CertificationFixture {
     public static List<CertificationResponse.CertificationScheduleResponse> certificationScheduleResList() {
        return List.of(
            new CertificationResponse.CertificationScheduleResponse(ScheduleType.APPLICATION_DATE, ExamType.PRACTICAL, Instant.parse("2025-05-01T09:00:00Z")),
            new CertificationResponse.CertificationScheduleResponse(ScheduleType.EXAM_DATE, ExamType.PRACTICAL, Instant.parse("2025-06-15T14:00:00Z")),
            new CertificationResponse.CertificationScheduleResponse(ScheduleType.PASS_ANNOUNCEMENT, ExamType.PRACTICAL, Instant.parse("2025-06-15T14:00:00Z"))
        );
    }

    public static List<CertificationResponse.CertificationExamDetailResponse> certificationExamDetailResList() {
        return List.of(
            new CertificationResponse.CertificationExamDetailResponse(ExamType.WRITTEN, "데이터베이스", ProblemType.MULTIPLE_CHOICE, 50, 90),
            new CertificationResponse.CertificationExamDetailResponse(ExamType.PRACTICAL, "프로그래밍", ProblemType.LONG_ANSWER, 10, 120)
        );
    }

    public static CertificationResponse certificationRes(long certificationId){
        return new CertificationResponse(certificationId,"정보처리기사", "한국산업인력공단",null,
                 "https://www.q-net.or.kr/man001.do?gSite=Q&gIntro=Y", "정보처리 능력 평가",
                "자격요건에 준하는 학위 취득", new HashSet<>(certificationScheduleResList()), new HashSet<>(certificationExamDetailResList()));
    }
    public static List<CertificationsByCategoryResponse> certificationsByCategoryRes(){
            return List.of(
                new CertificationsByCategoryResponse(1L, "자격증 1", Instant.now(), Instant.now().plusSeconds(3600), 100),
                new CertificationsByCategoryResponse(2L, "자격증 2",Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200), 50)
            );
    }

}