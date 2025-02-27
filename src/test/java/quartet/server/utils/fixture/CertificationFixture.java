package quartet.server.utils.fixture;

import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.CertificationsByCategoryRes;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.certification.type.ProblemType;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;

public class CertificationFixture {
     public static List<CertificationRes.CertificationScheduleRes> certificationScheduleResList() {
        return List.of(
            new CertificationRes.CertificationScheduleRes(ScheduleType.APPLICATION_DATE, ExamType.PRACTICAL, Instant.parse("2025-05-01T09:00:00Z")),
            new CertificationRes.CertificationScheduleRes(ScheduleType.EXAM_DATE, ExamType.PRACTICAL, Instant.parse("2025-06-15T14:00:00Z")),
            new CertificationRes.CertificationScheduleRes(ScheduleType.PASS_ANNOUNCEMENT, ExamType.PRACTICAL, Instant.parse("2025-06-15T14:00:00Z"))
        );
    }

    public static List<CertificationRes.CertificationExamDetailRes> certificationExamDetailResList() {
        return List.of(
            new CertificationRes.CertificationExamDetailRes(ExamType.WRITTEN, "데이터베이스", ProblemType.MULTIPLE_CHOICE, 50, 90),
            new CertificationRes.CertificationExamDetailRes(ExamType.PRACTICAL, "프로그래밍", ProblemType.LONG_ANSWER, 10, 120)
        );
    }

    public static CertificationRes certificationRes(long certificationId){
        return new CertificationRes(certificationId,"정보처리기사", "한국산업인력공단",null,
                 "https://www.q-net.or.kr/man001.do?gSite=Q&gIntro=Y", "정보처리 능력 평가",
                "자격요건에 준하는 학위 취득", new HashSet<>(certificationScheduleResList()), new HashSet<>(certificationExamDetailResList()));
    }
    public static List<CertificationsByCategoryRes> certificationsByCategoryRes(){
            return List.of(
                new CertificationsByCategoryRes(1L, "자격증 1", Instant.now(), Instant.now().plusSeconds(3600), 100),
                new CertificationsByCategoryRes(2L, "자격증 2",Instant.now().minusSeconds(7200), Instant.now().plusSeconds(7200), 50)
            );
    }

}