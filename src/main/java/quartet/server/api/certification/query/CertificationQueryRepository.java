package quartet.server.api.certification.query;

import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import quartet.server.api.certification.dto.response.*;
import quartet.server.domain.certification.model.*;
import quartet.server.domain.certification.type.QualificationType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.category.model.QMainCategory;
import quartet.server.domain.category.model.QSubCategory;
import quartet.server.domain.certification.type.TechnicalGradeType;
import quartet.server.domain.member.model.MemberCategory;
import quartet.server.domain.member.model.QMemberCategory;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
                certificationInfo.getViewCount(),
                0,
                qualifications,
                examDetails,
                schedules
        ));
    }

    private List<CertificationResponse.CertificationScheduleResponse> getScheduleDetails(final long certificationId) {

        List<CertificationResponse.CertificationScheduleTemp> temp = getScheduleTemp(certificationId);

        HashMap<String, List<CertificationResponse.ScheduleDetailResponse>> result = new HashMap<>();

        for (CertificationResponse.CertificationScheduleTemp x : temp) {
            String examRound = x.examRound();
            List<CertificationResponse.ScheduleDetailResponse> details = result.computeIfAbsent(examRound, k -> new ArrayList<>());
            details.add(new CertificationResponse.ScheduleDetailResponse(
                    x.scheduleType(),
                    x.examType(),
                    x.dates()
            ));
        }

        List<CertificationResponse.CertificationScheduleResponse> response = new ArrayList<>();
        for (HashMap.Entry<String, List<CertificationResponse.ScheduleDetailResponse>> x : result.entrySet()) {
            response.add(new CertificationResponse.CertificationScheduleResponse(
                    x.getKey(),
                    x.getValue()
            ));
        }
        return response;
    }

    private List<CertificationResponse.CertificationScheduleTemp> getScheduleTemp(final long certificationId) {
        QCertificationSchedule schedule = QCertificationSchedule.certificationSchedule;

        // 일정 정보는 현재 연도 기준만 조회
        int currentYear = LocalDateTime.now(ZoneId.of("Asia/Seoul")).getYear();
        LocalDateTime startOfYear = LocalDateTime.of(currentYear, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(currentYear, 12, 31, 23, 59, 59);

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
                        new QCertificationResponse_CertificationScheduleTemp(
                                scheduleTypeExpression,
                                schedule.examType,
                                schedule.examRound,
                                GroupBy.list(schedule.date)
                        )
                ));
    }

    private JPAQuery<Long> findAllCertificationIdsBaseQuery() {
        QCertification certification = QCertification.certification;
        return queryFactory
                .select(certification.id)
                .from(certification);

    }

    public Page<CertificationSearchResponse> findAllCertificationByCategory(final long subCategoryId, final Pageable pageable) {
        QCertification certification = QCertification.certification;

        List<Long> certificationIds =
                findAllCertificationIdsBaseQuery()
                        .where(certification.subCategory.id.eq(subCategoryId))
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        if (certificationIds.isEmpty()) return Page.empty(pageable);

        List<CertificationSearchResponse> certifications = getAllCertificationsByIds(certificationIds);

        JPAQuery<Long> countQuery = findAllCertificationIdsBaseQuery()
                .where(certification.subCategory.id.eq(subCategoryId))
                .select(certification.count());

        return PageableExecutionUtils.getPage(certifications, pageable, countQuery::fetchOne);
    }

    public Page<CertificationSearchResponse> getCertificationsBySearch(final String name, final Pageable pageable) {
        QCertification certification = QCertification.certification;
        List<Long> certificationIds;
        JPAQuery<Long> countQuery;


        certificationIds = findAllCertificationIdsBaseQuery()
                .where(certification.name.contains(name))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        countQuery = queryFactory
                .select(certification.count())
                .from(certification)
                .where(certification.name.contains(name));

        if (certificationIds.isEmpty()) return Page.empty(pageable);

        List<CertificationSearchResponse> certifications = getAllCertificationsByIds(certificationIds);
        return PageableExecutionUtils.getPage(certifications, pageable, countQuery::fetchOne);
    }

    private List<CertificationSearchResponse> getAllCertificationsByIds(final List<Long> certificationIds) {
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
                                .and(applicationSchedule.date.after(LocalDateTime.now(ZoneId.of("Asia/Seoul"))))
                                .and(applicationSchedule.scheduleType.in(ScheduleType.APPLICATION_START, ScheduleType.APPLICATION_END)
                                ))
                .leftJoin(examSchedule).on(
                        examSchedule.certification.eq(certification)
                                .and(examSchedule.date.after(LocalDateTime.now(ZoneId.of("Asia/Seoul"))))
                                .and(examSchedule.scheduleType.in(ScheduleType.EXAM_START, ScheduleType.EXAM_END)
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
                                        .otherwise(Expressions.nullExpression(LocalDateTime.class)),
                                cases()
                                        .when(examSchedule.id.isNotNull())
                                        .then(GroupBy.min(examSchedule.date))
                                        .otherwise(Expressions.nullExpression(LocalDateTime.class)),
                                Expressions.constant(0)
                        )
                ));
    }

    public void incrementViewCountWithLock(final long certificationId) {
        QCertification certification = QCertification.certification;
        Certification entity = queryFactory
                .selectFrom(certification)
                .where(certification.id.eq(certificationId))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
        if (entity != null) {
            entity.incrementViewCount();
        }
    }


    public List<CertificationSearchResponse> getTop6ByViewCount() {
        QCertification certification = QCertification.certification;
        List<Long> top6Ids = findAllCertificationIdsBaseQuery()
                .orderBy(certification.viewCount.desc())
                .limit(6)
                .fetch();
        if (top6Ids.isEmpty()) return List.of();

        return getAllCertificationsByIds(top6Ids);
    }

    public List<Long> findSubCategoryIdsByMainCategoryIds(final List<Long> mainCategoryIds) {
        QSubCategory subCategory = QSubCategory.subCategory;
        return queryFactory
                .select(subCategory.id)
                .from(subCategory)
                .where(subCategory.mainCategory.id.in(mainCategoryIds))
                .fetch();
    }


    public List<CertificationSearchResponse> findTop6BySubCategoryIdsOrderByViewCountDesc(final List<Long> subCategoryIds) {
        QCertification certification = QCertification.certification;

        List<Long> top6Ids = findAllCertificationIdsBaseQuery()
                .where(certification.subCategory.id.in(subCategoryIds))
                .orderBy(certification.viewCount.desc())
                .limit(6)
                .fetch();
        if (top6Ids.isEmpty()) return List.of();
        return getAllCertificationsByIds(top6Ids);
    }

    public Page<CertificationSearchResponse> findAllCertifications(final Pageable pageable) {
        QCertification certification = QCertification.certification;
        List<Long> certificationIds = findAllCertificationIdsBaseQuery()
                .orderBy(certification.viewCount.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (certificationIds.isEmpty()) return Page.empty(pageable);

        List<CertificationSearchResponse> certifications = getAllCertificationsByIds(certificationIds);

        JPAQuery<Long> countQuery = queryFactory
                .select(certification.count())
                .from(certification);

        return PageableExecutionUtils.getPage(certifications, pageable, countQuery::fetchOne);
    }
}