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
import quartet.server.domain.certification.model.*;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.category.model.QMainCategory;
import quartet.server.domain.category.model.QSubCategory;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
public class CertificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<CertificationResponse> getCertification(final Long certificationId) {
        QCertification certification = QCertification.certification;
        QAuthority authority = QAuthority.authority;
        QCertificationDescription description = QCertificationDescription.certificationDescription;
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
        QCertificationExamDetail examDetail = QCertificationExamDetail.certificationExamDetail;

        Map<Long, CertificationResponse> result =
                queryFactory
                    .from(certification)
                    .leftJoin(certification.authority, authority)
                    .leftJoin(description).on(description.certification.eq(certification))
                    .leftJoin(schedule).on(schedule.certification.eq(certification))
                    .leftJoin(examDetail).on(examDetail.certification.eq(certification))
                    .where(certification.id.eq(certificationId))
                    .transform(GroupBy.groupBy(certification.id).as(
                        new QCertificationResponse(
                                certification.id,
                                certification.name,
                                authority.name,
                                authority.iconImageUrl,
                                authority.applicationUrl,
                                description.description,
                                description.qualification,
                                GroupBy.set(
                                        new QCertificationResponse_CertificationScheduleResponse(
                                            schedule.scheduleType,
                                            schedule.examType,
                                            schedule.date,
                                            schedule.examRound
                                        )
                                ),
                                GroupBy.set(
                                    new QCertificationResponse_CertificationExamDetailResponse(
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

    private JPAQuery<Long> findAllCertificationsByCategoryBaseQuery(final long subCategoryId){
            QCertification certification = QCertification.certification;
            return queryFactory
                .select(certification.id)
                .from(certification)
                .where(certification.subCategory.id.eq(subCategoryId));
    }

    public List<CertificationsByCategoryResponse> getAllCertificationsByIds(final List<Long> certificationIds){
            QCertification certification = QCertification.certification;
            QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;

            return queryFactory
                        .from(certification)
                        .leftJoin(schedule).on(schedule.certification.eq(certification)
                                .and(schedule.examType.eq(ExamType.WRITTEN))
                                .and(schedule.scheduleType.in(ScheduleType.APPLICATION_START,ScheduleType.EXAM_START))
                        )
                        .where(certification.id.in(certificationIds))
                        .transform(GroupBy.groupBy(certification.id).list(
                            new QCertificationsByCategoryResponse(
                                    certification.id,
                                    certification.name,
                                    cases()
                                            .when(schedule.scheduleType.eq(ScheduleType.APPLICATION_START))
                                            .then(schedule.date)
                                            .otherwise((Instant) null),
                                    cases()
                                            .when(schedule.scheduleType.eq(ScheduleType.EXAM_START))
                                            .then(schedule.date)
                                            .otherwise((Instant) null),
                                    Expressions.constant(0)
                            )));
    }

    public List<CertificationsByCategoryResponse> findAllCertificationByCategory(final long subCategoryId, int count){

        List<Long> certificationIds =
                findAllCertificationsByCategoryBaseQuery(subCategoryId)
                .limit(count)
                .fetch();

        if (certificationIds.isEmpty()) return List.of();

        return getAllCertificationsByIds(certificationIds);
    }

    public Page<CertificationsByCategoryResponse> findAllCertificationByCategory(final long subCategoryId, final Pageable pageable){
            QCertification certification = QCertification.certification;

            List<Long> certificationIds =
                        findAllCertificationsByCategoryBaseQuery(subCategoryId)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

            if (certificationIds.isEmpty()) return Page.empty(pageable);

            List<CertificationsByCategoryResponse> certifications = getAllCertificationsByIds(certificationIds);

            JPAQuery<Long> countQuery = findAllCertificationsByCategoryBaseQuery(subCategoryId)
                                        .select(certification.count());

            return PageableExecutionUtils.getPage(certifications, pageable, countQuery::fetchOne);
    }

    public List<MainCategoryResponse> findAllMainCategories() {
        QMainCategory mainCategory = QMainCategory.mainCategory;
        return queryFactory
                .select(new QMainCategoryResponse(
                        mainCategory.id,
                        mainCategory.name,
                        mainCategory.isDefault
                ))
                .from(mainCategory)
                .fetch();
    }

    public List<SubCategoryResponse> findAllSubCategoriesByMainCategoryId(final long mainCategoryId) {
        QSubCategory subCategory = QSubCategory.subCategory;
        return queryFactory
                .select(new QSubCategoryResponse(
                        subCategory.id,
                        subCategory.name,
                        subCategory.mainCategory.id
                ))
                .from(subCategory)
                .where(subCategory.mainCategory.id.eq(mainCategoryId))
                .fetch();
    }
}



