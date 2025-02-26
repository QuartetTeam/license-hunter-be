package quartet.server.api.certification.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import quartet.server.api.certification.dto.response.CertificationRes;
import quartet.server.api.certification.dto.response.QCertificationRes;
import quartet.server.api.certification.dto.response.QCertificationRes_CertificationExamDetailRes;
import quartet.server.api.certification.dto.response.QCertificationRes_CertificationScheduleRes;
import quartet.server.domain.certification.model.*;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class CertificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public CertificationRes getCertification(Long certificationId) {
        QCertification certification = QCertification.certification;
        QAuthority authority = QAuthority.authority;
        QCertificationDescription description = QCertificationDescription.certificationDescription;
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
        QCertificationExamDetail examDetail = QCertificationExamDetail.certificationExamDetail;



        Map<Long, CertificationRes> transformResult =
                queryFactory
                    .from(certification)
                    .leftJoin(certification.authority, authority)
                    .leftJoin(description).on(description.certification.eq(certification))
                    .leftJoin(schedule).on(schedule.certification.eq(certification))
                    .leftJoin(examDetail).on(examDetail.certification.eq(certification))
                    .where(certification.id.eq(certificationId))
                    .transform(GroupBy.groupBy(certification.id).as(
                        new QCertificationRes(
                                certification.name,
                                authority.name,
                                authority.iconImageUrl,
                                authority.applicationUrl,
                                description.description,
                                description.qualification,
                                GroupBy.set(
                                        new QCertificationRes_CertificationScheduleRes(
                                            schedule.scheduleType,
                                            schedule.examType,
                                            schedule.scheduledDate
                                        )
                                ),
                                GroupBy.set(
                                    new QCertificationRes_CertificationExamDetailRes(
                                        examDetail.examType,
                                        examDetail.subject,
                                        examDetail.problemType,
                                        examDetail.totalProblemsCount,
                                        examDetail.duration
                                    )
                                )
                        )
                    ));

        return transformResult.get(certificationId);
    }

}
