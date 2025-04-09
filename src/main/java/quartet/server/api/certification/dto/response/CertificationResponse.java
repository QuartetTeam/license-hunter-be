package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public record CertificationResponse(
        long id,
        String name,
        String authorityName,
        String authorityIconImageUrl,
        String applicationUrl,
        String description,
        Set<CertificationQualificationResponse> qualification,
        List<CertificationExamDetailResponse> examDetails
//        Set<CertificationScheduleResponse> scheduleSet,
){
    @QueryProjection
    public CertificationResponse(
        long id,
        String name,
        String authorityName,
        String authorityIconImageUrl,
        String applicationUrl,
        String description,
        Set<CertificationQualificationResponse> qualification,
        List<CertificationExamDetailResponse> examDetails
//        Set<CertificationScheduleResponse> scheduleSet,
    ) {
        this.id = id;
        this.name = name;
        this.authorityName = authorityName;
        this.authorityIconImageUrl = authorityIconImageUrl;
        this.applicationUrl = applicationUrl;
        this.description = description;
        this.qualification = qualification;
        this.examDetails = examDetails;
//        this.scheduleSet = scheduleSet;
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
            ScheduleType scheduleType,
            ExamType examType,
            Instant date,
            String examRound
    ){
        @QueryProjection
        public CertificationScheduleResponse(
                ScheduleType scheduleType,
                ExamType examType,
                Instant date,
                String examRound)
        {
            this.scheduleType = scheduleType;
            this.examType = examType;
            this.date = date;
            this.examRound = examRound;
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
            return examType.getValue();
        }
    }

    public record CertificationExamProcessResponse(
            ProblemType problemType,
            String totalProblems,
            String timeLimit
    ){
        @QueryProjection
        public CertificationExamProcessResponse(
                ProblemType problemType,
                String totalProblems,
                String timeLimit
        )
        {
            this.problemType = problemType;
            this.totalProblems = totalProblems;
            this.timeLimit = timeLimit;
        }

        public String getProblemType() {
            return problemType.getValue();
        }
    }
}

