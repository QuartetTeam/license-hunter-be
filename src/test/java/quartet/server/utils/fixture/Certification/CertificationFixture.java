package quartet.server.utils.fixture.Certification;

import quartet.server.api.certification.dto.response.CertificationResponse;
import quartet.server.api.certification.dto.response.CertificationsByCategoryResponse;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CertificationFixture {
    public static List<CertificationResponse.CertificationScheduleResponse> certificationScheduleResList() {
        List<CertificationResponse.CertificationScheduleResponse> roundSchedules = new ArrayList<>();
        
        List<CertificationResponse.ScheduleDetailResponse> scheduleDetails = new ArrayList<>();
        scheduleDetails.add(new CertificationResponse.ScheduleDetailResponse(
            "접수일",
            ExamType.PRACTICAL,
            List.of(Instant.parse("2025-05-01T09:00:00Z"), Instant.parse("2025-05-02T09:00:00Z"))
        ));
        scheduleDetails.add(new CertificationResponse.ScheduleDetailResponse(
            "시험일",
            ExamType.PRACTICAL,
            List.of(Instant.parse("2025-06-15T14:00:00Z"), Instant.parse("2025-06-16T14:00:00Z"))
        ));
        scheduleDetails.add(new CertificationResponse.ScheduleDetailResponse(
            "합격일",
            ExamType.PRACTICAL,
            List.of(Instant.parse("2025-07-01T09:00:00Z"))
        ));

        roundSchedules.add(new CertificationResponse.CertificationScheduleResponse(
            "1회차",
            scheduleDetails
        ));

        return roundSchedules;
    }

    public static List<CertificationResponse.CertificationExamDetailResponse> certificationExamDetailResList() {
        return List.of(
            new CertificationResponse.CertificationExamDetailResponse(
                ExamType.WRITTEN,
                "데이터베이스",
                new CertificationResponse.CertificationExamProcessResponse(
                    ProblemType.MULTIPLE_CHOICE_4,
                    "과목당 60문항",
                    "총 60분"
                )
            ),
            new CertificationResponse.CertificationExamDetailResponse(
                ExamType.PRACTICAL,
                "프로그래밍",
                new CertificationResponse.CertificationExamProcessResponse(
                    ProblemType.SHORT_ANSWER,
                    "20문항",
                    "총 60분"
                )
            )
        );
    }

    public static Set<CertificationResponse.CertificationQualificationResponse> certificationQualificationResList() {
        return Set.of(
            new CertificationResponse.CertificationQualificationResponse("관련 전공", List.of("경영학과")),
            new CertificationResponse.CertificationQualificationResponse("실무 경험", List.of("실무 경험 2년"))
        );
    }

    public static CertificationResponse certificationRes(long certificationId){
        return new CertificationResponse(
            certificationId,
            "정보처리기사",
            "한국산업인력공단",
            "https://www.q-net.or.kr/man001.do?gSite=Q&gIntro=Y",
            null,
            "정보처리 능력 평가",
            Integer.valueOf(0),
            Integer.valueOf(0),
            certificationQualificationResList(),
            certificationExamDetailResList(),
            certificationScheduleResList()
        );
    }

    public static List<CertificationsByCategoryResponse> certificationsByCategoryRes(){
        return List.of(
            new CertificationsByCategoryResponse(
                1L,
                "자격증 1",
                Instant.parse("2025-05-01T09:00:00Z"),
                Instant.parse("2025-05-01T10:00:00Z"),
                100
            ),
            new CertificationsByCategoryResponse(
                2L,
                "자격증 2",
                Instant.parse("2025-05-01T08:00:00Z"),
                Instant.parse("2025-05-01T10:00:00Z"),
                50
            )
        );
    }

    public static CertificationResponse certificationResponse() {
        return new CertificationResponse(
            1L,
            "정보처리기사",
            "한국산업인력공단",
            "https://example.com/apply",
            "https://example.com/icon.png",
            "정보처리기사 자격증 설명",
            Integer.valueOf(0),
            Integer.valueOf(0),
            Set.of(new CertificationResponse.CertificationQualificationResponse(
                "4년제 학사",
                List.of("학력")
            )),
            List.of(new CertificationResponse.CertificationExamDetailResponse(
                ExamType.PRACTICAL,
                "정보처리기사 과목",
                new CertificationResponse.CertificationExamProcessResponse(
                    ProblemType.SHORT_ANSWER,
                    "과목당 20문항",
                    "총 60분"
                )
            )),
            certificationScheduleResList()
        );
    }
}