package quartet.server.api.certification.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;
import quartet.server.domain.certification.type.ScheduleType;


import java.time.Instant;
import java.util.List;
import java.util.Set;

public record CertificationRes (
        String name,
        String authorityName,
        String authorityIconImagePath,
        String applicationUrl,
        String description,
        String qualification,
        Set<CertificationScheduleRes> scheduleSet,
        Set<CertificationRes.CertificationExamDetailRes> examDetailSet
){
    @QueryProjection
    public CertificationRes(
        String name,
        String authorityName,
        String authorityIconImagePath,
        String applicationUrl,
        String description,
        String qualification,
        Set<CertificationScheduleRes> scheduleSet,
        Set<CertificationExamDetailRes> examDetailSet
    ) {
        this.name = name;
        this.authorityName = authorityName;
        this.authorityIconImagePath = authorityIconImagePath;
        this.applicationUrl = applicationUrl;
        this.description = description;
        this.qualification = qualification;
        this.scheduleSet = scheduleSet;
        this.examDetailSet = examDetailSet;
    }

    public record CertificationScheduleRes(
            ScheduleType scheduleType,
            ExamType examType,
            Instant  date
    ){
        @QueryProjection
        public CertificationScheduleRes(
                ScheduleType scheduleType,
                ExamType examType,
                Instant  date)
        {
            this.scheduleType = scheduleType;
            this.examType = examType;
            this.date = date;
        }
    }
    public record CertificationExamDetailRes(
            ExamType examType,
            String subject,
            ProblemType problemType,
            Integer totalProblems,
            Integer timeLimit
    ){
        @QueryProjection
        public CertificationExamDetailRes(
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

