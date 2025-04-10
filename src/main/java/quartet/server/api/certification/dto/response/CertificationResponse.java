package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record CertificationResponse(
        long id,
        String name,
        String authorityName,
        String websiteUrl,
        String applicationUrl,
        String description,
        Integer viewCount,
        Integer CalendarSubscription,
        Set<CertificationQualificationResponse> qualification,
        List<CertificationExamDetailResponse> examDetails,
        List<CertificationScheduleResponse> schedules
){
    @QueryProjection
    public CertificationResponse(
        long id,
        String name,
        String authorityName,
        String websiteUrl,
        String applicationUrl,
        String description,
        Integer viewCount,
        Integer CalendarSubscription,
        Set<CertificationQualificationResponse> qualification,
        List<CertificationExamDetailResponse> examDetails,
        List<CertificationScheduleResponse> schedules
    ) {
        this.id = id;
        this.name = name;
        this.authorityName = authorityName;
        this.websiteUrl = websiteUrl;
        this.applicationUrl = applicationUrl;
        this.description = description;
        this.viewCount = viewCount;
        this.CalendarSubscription = CalendarSubscription;
        this.qualification = qualification;
        this.examDetails = examDetails;
        this.schedules = schedules;
    }

    public record CertificationQualificationResponse(
            String type,
            List<String> data
    ){
        @QueryProjection
        public CertificationQualificationResponse(
                String type,
                List<String> data
        ){
            this.type = type;
            this.data = data;
        }
    }

    public record CertificationScheduleResponse(
            String scheduleType,
            ExamType examType,
            String examRound,
            List<Instant> date
    ){
        @QueryProjection
        public CertificationScheduleResponse(
                String scheduleType,
                ExamType examType,
                String examRound,
                List<Instant> date
        ){
            this.scheduleType = scheduleType;
            this.examType = examType;
            this.examRound = examRound;
            this.date = date;
        }

        public String getExamType() {
            return examType != null ? examType.getValue() : null;
        }
    }

    public record CertificationExamDetailResponse(
            ExamType examType,
            String examSubject,
            CertificationExamProcessResponse examProcess
    ){
        @QueryProjection
        public CertificationExamDetailResponse(
                ExamType examType,
                String examSubject,
                CertificationExamProcessResponse examProcess
        ){
            this.examType = examType;
            this.examSubject = examSubject;
            this.examProcess = examProcess;
        }

        public String getExamType() {
            return examType != null ? examType.getValue() : null;
        }
    }

    public record CertificationExamProcessResponse(
            ProblemType problemType,
            String problemNums,
            String examTime
    ){
        @QueryProjection
        public CertificationExamProcessResponse(
                ProblemType problemType,
                String problemNums,
                String examTime
        )
        {
            this.problemType = problemType;
            this.problemNums = problemNums;
            this.examTime = examTime;
        }

        public String getProblemType() {
            return problemType.getValue();
        }
    }
}

