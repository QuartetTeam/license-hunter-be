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
import quartet.server.domain.certification.type.QualificationType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.category.model.QMainCategory;
import quartet.server.domain.category.model.QSubCategory;
import quartet.server.domain.certification.type.TechnicalGradeType;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static com.querydsl.core.types.dsl.Expressions.cases;

@Repository
@RequiredArgsConstructor
public class CertificationQueryRepository {
    private final JPAQueryFactory queryFactory;

    public Optional<CertificationResponse> getCertification(final Long certificationId) {
        QCertification certification = QCertification.certification;
        QAuthority authority = QAuthority.authority;
        QCertificationDescription description = QCertificationDescription.certificationDescription;
        QCertificationExamDetail examDetail = QCertificationExamDetail.certificationExamDetail;
        QHrdTechnicalQualification hrdTechnicalQualification = QHrdTechnicalQualification.hrdTechnicalQualification;
        QCertificationQualification normalQualification = QCertificationQualification.certificationQualification;

        // 자격증 기본 정보 조회
        Certification certificationInfo = queryFactory
                .selectFrom(certification)
                .leftJoin(certification.authority, authority).fetchJoin()
                .where(certification.id.eq(certificationId))
                .fetchOne();

        if (certificationInfo == null) {
            return Optional.empty();
        }

        // 자격 요건 데이터 조회 및 변환
        Set<CertificationResponse.CertificationQualificationResponse> qualifications;
        if (certificationInfo.getQualificationType() == QualificationType.T) {
            // 국가기술자격의 경우(T)
            QHrdCertificationDetail hrdCertificationDetail = QHrdCertificationDetail.hrdCertificationDetail;
            
            // HrdCertificationDetail에서 grade 조회
            TechnicalGradeType grade = queryFactory
                    .select(hrdCertificationDetail.grade)
                    .from(hrdCertificationDetail)
                    .where(hrdCertificationDetail.certification.id.eq(certificationId))
                    .fetchOne();

            if (grade == null) {
                return Optional.empty();
            }

            // 국가기술자격 자격요건 조회
            Map<String, List<String>> qualificationMap = queryFactory
                    .from(hrdTechnicalQualification)
                    .where(hrdTechnicalQualification.grade.eq(grade))
                    .transform(GroupBy.groupBy(hrdTechnicalQualification.type)
                            .as(GroupBy.list(hrdTechnicalQualification.qualification)));

            qualifications = qualificationMap.entrySet().stream()
                    .map(entry -> new CertificationResponse.CertificationQualificationResponse(
                            entry.getKey(),
                            entry.getValue()
                    ))
                    .collect(Collectors.toSet());
        } else {
            // 그 외 자격증의 경우
            Map<String, List<String>> qualificationMap = queryFactory
                    .from(normalQualification)
                    .where(normalQualification.certification.id.eq(certificationId))
                    .transform(GroupBy.groupBy(normalQualification.type)
                            .as(GroupBy.list(normalQualification.qualification)));

            qualifications = qualificationMap.entrySet().stream()
                    .map(entry -> new CertificationResponse.CertificationQualificationResponse(
                            entry.getKey(),
                            entry.getValue()
                    ))
                    .collect(Collectors.toSet());
        }

        // 시험 일정 조회
        List<CertificationResponse.CertificationScheduleResponse> schedules = getScheduleDetails(certificationId);

        // 시험 상세 정보 조회
        List<CertificationResponse.CertificationExamDetailResponse> examDetails = queryFactory
                .from(examDetail)
                .where(examDetail.certification.id.eq(certificationId))
                .transform(GroupBy.groupBy(examDetail.id).list(
                        new QCertificationResponse_CertificationExamDetailResponse(
                                examDetail.examType,
                                examDetail.subject,
                                new QCertificationResponse_CertificationExamProcessResponse(
                                        examDetail.problemType,
                                        examDetail.totalProblemsCount,
                                        examDetail.duration
                                )
                        )
                ));

        // 자격증 설명 조회
        String certificationDescription = queryFactory
                .select(description.description)
                .from(description)
                .where(description.certification.id.eq(certificationId))
                .fetchOne();

        return Optional.of(new CertificationResponse(
                certificationInfo.getId(),
                certificationInfo.getName(),
                certificationInfo.getAuthority().getName(),
                certificationInfo.getAuthority().getWebsiteUrl(),
                certificationInfo.getAuthority().getApplicationUrl(),
                certificationDescription,
                0,
                0,
                qualifications,
                examDetails,
                schedules
        ));
    }

    private List<CertificationResponse.CertificationScheduleResponse> getScheduleDetails(final long certificationId) {
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;
        
        // 일정 정보는 현재 연도 기준만 가져옵니다
        int currentYear = Instant.now().atZone(java.time.ZoneId.systemDefault()).getYear();
        Instant startOfYear = Instant.parse(currentYear + "-01-01T00:00:00Z");
        Instant endOfYear = Instant.parse(currentYear + "-12-31T23:59:59Z");

        var scheduleTypeExpression = cases()
            .when(schedule.scheduleType.in(ScheduleType.APPLICATION_START, ScheduleType.APPLICATION_END))
            .then(Expressions.constant("접수일"))
            .when(schedule.scheduleType.in(ScheduleType.EXAM_START, ScheduleType.EXAM_END))
            .then(Expressions.constant("시험일"))
            .when(schedule.scheduleType.eq(ScheduleType.PASS_ANNOUNCEMENT))
            .then(Expressions.constant("합격일"))
            .otherwise(schedule.scheduleType.stringValue());

        // 스케줄 데이터 조회
        return queryFactory
            .from(schedule)
            .where(
                schedule.certification.id.eq(certificationId),
                schedule.date.between(startOfYear, endOfYear)
            )
            .transform(GroupBy.groupBy(
                schedule.examRound,
                schedule.examType,
                scheduleTypeExpression
            ).list(
                new QCertificationResponse_CertificationScheduleResponse(
                    scheduleTypeExpression,
                    schedule.examType,
                    schedule.examRound,
                    GroupBy.list(schedule.date)
                )
            ));
    }

    private JPAQuery<Long> findAllCertificationsByCategoryBaseQuery(final long subCategoryId){
        QCertification certification = QCertification.certification;
        return queryFactory
            .select(certification.id)
            .from(certification)
            .where(certification.subCategory.id.eq(subCategoryId));
    }


    public List<CertificationSearchResponse> findAllCertificationByCategory(final long subCategoryId, final int count){

        List<Long> certificationIds =
                findAllCertificationsByCategoryBaseQuery(subCategoryId)
                .limit(count)
                .fetch();

        if (certificationIds.isEmpty()) return List.of();

        return getAllCertificationsByIds(certificationIds);
    }

    public Page<CertificationSearchResponse> findAllCertificationByCategory(final long subCategoryId, final Pageable pageable){
            QCertification certification = QCertification.certification;

            List<Long> certificationIds =
                        findAllCertificationsByCategoryBaseQuery(subCategoryId)
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

            if (certificationIds.isEmpty()) return Page.empty(pageable);

            List<CertificationSearchResponse> certifications = getAllCertificationsByIds(certificationIds);

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

    public List<CertificationSearchResponse> getCertificationsBySearch(final String name) {
        QCertification certification = QCertification.certification;


        List<Long> certificationIds = queryFactory
                .select(certification.id)
                .from(certification)
                .where(
                        certification.name.eq(name)
                                .or(certification.name.like(name + "(%"))
                )
                .fetch();

        if (certificationIds.isEmpty()) return List.of();

        return getAllCertificationsByIds(certificationIds);
    }

    private List<CertificationSearchResponse> getAllCertificationsByIds (final List<Long> certificationIds){
        QCertification certification = QCertification.certification;
        QMainCategory mainCategory = QMainCategory.mainCategory;
        QSubCategory subCategory = QSubCategory.subCategory;
        QCertificationSchedule applicationSchedule = new QCertificationSchedule("applicationSchedule");
        QCertificationSchedule examSchedule = new QCertificationSchedule("examSchedule");

        return queryFactory
                .from(certification)
                .join(certification.subCategory, subCategory)
                .join(subCategory.mainCategory, mainCategory)
                .leftJoin(applicationSchedule).on(
                        applicationSchedule.certification.eq(certification)
                                .and(applicationSchedule.date.after(Instant.now()))
                                .and(applicationSchedule.scheduleType.in(ScheduleType.APPLICATION_START,ScheduleType.APPLICATION_END)
                ))
                .leftJoin(examSchedule).on(
                        examSchedule.certification.eq(certification)
                                .and(examSchedule.date.after(Instant.now()))
                                .and(examSchedule.scheduleType.in(ScheduleType.EXAM_START,ScheduleType.EXAM_END)
                ))
                .where(certification.id.in(certificationIds))
                .transform(GroupBy.groupBy(certification.id).list(
                        new QCertificationSearchResponse(
                                certification.id,
                                mainCategory.name,
                                subCategory.name,
                                certification.name,
                                cases()
                                        .when(applicationSchedule.id.isNotNull())
                                        .then(GroupBy.min(applicationSchedule.date))
                                        .otherwise(Expressions.nullExpression(Instant.class)),
                                cases()
                                        .when(examSchedule.id.isNotNull())
                                        .then(GroupBy.min(examSchedule.date))
                                        .otherwise(Expressions.nullExpression(Instant.class)),
                                Expressions.constant(0)
                        )
                ));
    }
}



