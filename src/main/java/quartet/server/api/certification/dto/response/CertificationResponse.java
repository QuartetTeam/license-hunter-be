package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;
import quartet.server.domain.certification.type.ScheduleType;


import java.time.Instant;
import java.util.Set;

public record CertificationResponse(
        long id,
        String name,
        String authorityName,
        String authorityIconImageUrl,
        String applicationUrl,
        String description,
        String qualification,
        Set<CertificationScheduleResponse> scheduleSet,
        Set<CertificationExamDetailResponse> examDetailSet
){
    @QueryProjection
    public CertificationResponse(
        long id,
        String name,
        String authorityName,
        String authorityIconImageUrl,
        String applicationUrl,
        String description,
        String qualification,
        Set<CertificationScheduleResponse> scheduleSet,
        Set<CertificationExamDetailResponse> examDetailSet
    ) {
        this.id = id;
        this.name = name;
        this.authorityName = authorityName;
        this.authorityIconImageUrl = authorityIconImageUrl;
        this.applicationUrl = applicationUrl;
        this.description = description;
        this.qualification = qualification;
        this.scheduleSet = scheduleSet;
        this.examDetailSet = examDetailSet;
    }

    public record CertificationScheduleResponse(
            ScheduleType scheduleType,
            ExamType examType,
            Instant  date
    ){
        @QueryProjection
        public CertificationScheduleResponse(
                ScheduleType scheduleType,
                ExamType examType,
                Instant  date)
        {
            this.scheduleType = scheduleType;
            this.examType = examType;
            this.date = date;
        }
    }
    public record CertificationExamDetailResponse(
            ExamType examType,
            String subject,
            ProblemType problemType,
            Integer totalProblems,
            Integer timeLimit
    ){
        @QueryProjection
        public CertificationExamDetailResponse(
                 ExamType examType,
                String subject,
                ProblemType problemType,
                Integer totalProblems,
                Integer timeLimit
        ){
            this.examType = examType;
            this.subject = subject;
            this.problemType = problemType;
            this.totalProblems = totalProblems;
            this.timeLimit = timeLimit;
        }
    }
}

