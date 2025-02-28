package quartet.server.api.certification.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import quartet.server.api.certification.dto.response.*;
import quartet.server.domain.category.exception.CategoryNotFoundException;
import quartet.server.domain.category.model.QCategory;
import quartet.server.domain.certification.model.*;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
public class CertificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<CertificationRes> getCertification(Long certificationId) {
        QCertification certification = QCertification.certification;
        QAuthority authority = QAuthority.authority;
        QCertificationDescription description = QCertificationDescription.certificationDescription;
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
        QCertificationExamDetail examDetail = QCertificationExamDetail.certificationExamDetail;

        Map<Long, CertificationRes> result =
                queryFactory
                    .from(certification)
                    .leftJoin(certification.authority, authority)
                    .leftJoin(description).on(description.certification.eq(certification))
                    .leftJoin(schedule).on(schedule.certification.eq(certification))
                    .leftJoin(examDetail).on(examDetail.certification.eq(certification))
                    .where(certification.id.eq(certificationId))
                    .transform(GroupBy.groupBy(certification.id).as(
                        new QCertificationRes(
                                certification.id,
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

        return Optional.ofNullable(result.get(certificationId));
    }

    public long getDefaultSubCategoryId(long categoryId){
        QCategory category = QCategory.category;

        // TODO 최지희: default 서브 카테고리 선정 기준 논의 필요
        return Optional.ofNullable(
                        queryFactory
                        .select(category.id)
                        .from(category)
                        .where(category.parentCategory.id.eq(categoryId))
                        .fetchFirst())
                .orElseThrow(CategoryNotFoundException::new);
    }

    private JPAQuery<Long> findAllCertificationsByCategoryBaseQuery(long subCategoryId){
            QCertification certification = QCertification.certification;
            return queryFactory
                .select(certification.id)
                .from(certification)
                .where(certification.category.id.eq(subCategoryId));
    }

    public Page<CertificationsByCategoryRes> findAllCertificationByCategory(long subCategoryId, Pageable pageable){
            QCertification certification = QCertification.certification;
            QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;

            List<Long> certificationIds =
                        findAllCertificationsByCategoryBaseQuery(subCategoryId)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

            if (certificationIds.isEmpty()) return Page.empty(pageable);

            List<CertificationsByCategoryRes> certificationList =
                queryFactory
                        .from(certification)
                        .leftJoin(schedule).on(schedule.certification.eq(certification)
                                .and(schedule.examType.eq(ExamType.WRITTEN))
                                .and(schedule.scheduleType.in(ScheduleType.APPLICATION_DATE,ScheduleType.EXAM_DATE))
                        )
                        .where(certification.category.id.eq(subCategoryId))
                        .transform(GroupBy.groupBy(certification.id).list(
                            new QCertificationsByCategoryRes(
                                    certification.id,
                                    certification.name,
                                    cases()
                                            .when(schedule.scheduleType.eq(ScheduleType.APPLICATION_DATE))
                                            .then(schedule.scheduledDate)
                                            .otherwise((Instant) null),
                                    cases()
                                            .when(schedule.scheduleType.eq(ScheduleType.EXAM_DATE))
                                            .then(schedule.scheduledDate)
                                            .otherwise((Instant) null),
                                    Expressions.constant(0)
                            )));

            JPAQuery<Long> countQuery = findAllCertificationsByCategoryBaseQuery(subCategoryId)
                                        .select(certification.count());

            return PageableExecutionUtils.getPage(certificationList, pageable, countQuery::fetchOne);
    }
}



