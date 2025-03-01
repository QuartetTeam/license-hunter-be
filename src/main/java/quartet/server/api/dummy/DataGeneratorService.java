package quartet.server.api.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.domain.category.model.Category;
import quartet.server.domain.category.repository.CategoryRepository;
import quartet.server.domain.certification.model.Authority;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.model.CertificationExamDetail;
import quartet.server.domain.certification.model.CertificationSchedule;
import quartet.server.domain.certification.repository.AuthorityRepository;
import quartet.server.domain.certification.repository.CertificationExamDetailRepository;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.certification.repository.CertificationScheduleRepository;
import quartet.server.domain.certification.type.ExamType;
import quartet.server.domain.certification.type.ProblemType;
import quartet.server.domain.certification.type.ScheduleType;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DataGeneratorService { // TODO 박현제: 추후에 삭제 예정

    private final AuthorityRepository authorityRepository;
    private final CertificationRepository certificationRepository;
    private final CertificationExamDetailRepository examDetailRepository;
    private final CertificationScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;

    private final Random random = new Random();

    private final String[] CERT_PREFIXES = {
            "정보처리", "컴퓨터활용능력", "리눅스마스터", "네트워크관리사", "SQL개발자",
            "AWS", "Azure", "GCP", "정보보안", "빅데이터분석", "데이터아키텍처",
            "IoT", "인공지능", "프로젝트관리", "CCNA", "LPIC", "모바일앱개발",
            "디지털마케팅", "클라우드", "블록체인", "소프트웨어설계", "자바", "파이썬"
    };

    private final String[] CERT_SUFFIXES = {
            "기사", "산업기사", "기술사", "전문가", "관리사", "마스터", "자격증",
            "엔지니어", "개발자", "운용사", "지도사", "디자이너", "아키텍트",
            "디벨로퍼", "스페셜리스트", "어드바이저", "consultant", "professional", "expert"
    };

    private final String[] AUTHORITY_NAMES = {
            "한국산업인력공단", "대한상공회의소", "한국데이터산업진흥원", "한국정보통신진흥협회",
            "한국콘텐츠진흥원", "Microsoft", "Amazon", "Google", "Oracle", "Linux Foundation",
            "CISCO", "PMI", "한국생산성본부", "정보통신기술자격검정원", "한국SW산업협회",
            "금융결제원", "과학기술정보통신부", "고용노동부"
    };

    private final String[] EMAIL_DOMAINS = {
            "gmail.com", "naver.com", "daum.net", "kakao.com", "outlook.com",
            "icloud.com", "yahoo.com", "hotmail.com", "protonmail.com"
    };

    private final String[] SOCIAL_PROVIDERS = {
            "GOOGLE", "KAKAO", "NAVER"
    };

    private final String[] NICKNAME_PREFIXES = {
            "행복한", "즐거운", "열정적인", "진취적인", "도전하는", "멋진", "똑똑한", "유머있는",
            "센스있는", "창의적인", "성실한", "믿음직한", "자신감있는", "차분한", "활기찬"
    };

    private final String[] NICKNAME_SUFFIXES = {
            "개발자", "엔지니어", "디자이너", "학생", "직장인", "프로그래머", "마케터", "기획자",
            "컨설턴트", "분석가", "매니저", "리더", "크리에이터", "코더", "해커"
    };

    private final String[] PROFILE_IMAGES = {
            "https://randomuser.me/api/portraits/men/1.jpg",
            "https://randomuser.me/api/portraits/women/1.jpg",
            "https://randomuser.me/api/portraits/men/2.jpg",
            "https://randomuser.me/api/portraits/women/2.jpg",
            "https://randomuser.me/api/portraits/men/3.jpg",
            "https://randomuser.me/api/portraits/women/3.jpg",
            "https://randomuser.me/api/portraits/men/4.jpg",
            "https://randomuser.me/api/portraits/women/4.jpg",
            "https://randomuser.me/api/portraits/men/5.jpg",
            "https://randomuser.me/api/portraits/women/5.jpg"
    };

    private final String[] INTRODUCTIONS = {
            "자격증 취득에 관심이 많습니다.",
            "IT 분야 전문가를 목표로 공부중입니다.",
            "새로운 기술과 트렌드에 관심이 많아요.",
            "개발자로 일하면서 관련 자격증도 취득하고 있어요.",
            "자기계발을 위해 다양한 자격증에 도전하고 있습니다.",
            "취업을 위해 여러 자격증 준비중입니다.",
            "업무 역량 강화를 위해 자격증 공부중입니다.",
            "프리랜서로 일하면서 포트폴리오를 강화하고 있어요.",
            "학생이지만 미리 준비하는 중입니다.",
            "경력 전환을 위해 자격증 취득에 노력하고 있어요."
    };

    private final String[] WEBSITE_DOMAINS = {
            "go.kr", "or.kr", "co.kr", "com", "org", "net"
    };

    private final String[] ICON_URLS = {
            "https://example.com/icons/icon1.png",
            "https://example.com/icons/icon2.png",
            "https://example.com/icons/icon3.png",
            "https://example.com/icons/icon4.png",
            "https://example.com/icons/icon5.png",
            "https://example.com/icons/default.png"
    };

    private final String[] SUBJECT_NAMES = {
            "기초 이론", "실무 응용", "법규", "안전관리", "기술 동향",
            "프로그래밍", "네트워크", "데이터베이스", "알고리즘", "보안",
            "시스템 설계", "프로젝트 관리", "인프라 구축", "클라우드 컴퓨팅", "인공지능"
    };
    @Transactional

    public void initializeCategories() {
        if (categoryRepository.count() == 0) {
            // 대분류 카테고리 생성
            Category itCategory = Category.of("IT", null);
            Category businessCategory = Category.of("경영/사무", null);
            Category engineeringCategory = Category.of("공학", null);

            categoryRepository.save(itCategory);
            categoryRepository.save(businessCategory);
            categoryRepository.save(engineeringCategory);

            // 소분류 카테고리 생성
            Category softwareCategory = Category.of("소프트웨어", itCategory);
            Category networkCategory = Category.of("네트워크", itCategory);
            Category managementCategory = Category.of("경영 일반", businessCategory);
            Category mechanicalCategory = Category.of("기계", engineeringCategory);

            categoryRepository.save(softwareCategory);
            categoryRepository.save(networkCategory);
            categoryRepository.save(managementCategory);
            categoryRepository.save(mechanicalCategory);
        }
    }

    /**
     * 매일 자정에 자동으로 더미 데이터를 생성합니다. (개발 환경에서만 활성화)
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void scheduledDataGeneration() {
        try {
            log.info("Starting scheduled data generation...");
            // 랜덤 자격증과 회원 생성
            int certCount = 2 + random.nextInt(5); // 2~6개의 자격증
            int memberCount = 1 + random.nextInt(3); // 1~3명의 회원

            String result = generateData(certCount, memberCount);
            log.info("Scheduled data generation completed: {}", result);
        } catch (Exception e) {
            log.error("Error in scheduled data generation", e);
        }
    }

    /**
     * 자격증 및 회원 데이터를 생성합니다.
     *
     * @param certCount 생성할 자격증 수
     * @param memberCount 생성할 회원 수
     * @return 생성 결과 메시지
     */
    public String generateData(int certCount, int memberCount) {
        try {
            log.info("Starting data generation... (Certifications: {}, Members: {})", certCount, memberCount);

            initializeCategories();
            // 기관 생성
            List<Authority> authorities = generateAuthorities();

            // 자격증 생성
            int certCreated = generateCertifications(authorities, certCount);

            // 자격증 시험 상세정보 생성
            int examDetailsCreated = generateExamDetails();

            // 자격증 일정 생성
            int schedulesCreated = generateCertificationSchedules();

            // 회원 생성
            int memberCreated = generateMembers(memberCount);

            String resultMessage = String.format("성공적으로 더미 데이터가 생성되었습니다. " +
                            "(기관: %d개, 자격증: %d개, 시험 상세정보: %d개, 자격증 일정: %d개, 회원: %d개)",
                    authorities.size(), certCreated, examDetailsCreated, schedulesCreated, memberCreated);

            log.info(resultMessage);
            return resultMessage;

        } catch (Exception e) {
            log.error("Error generating data", e);
            throw new RuntimeException("데이터 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 생성된 더미 데이터를 초기화합니다.
     *
     * @param deleteAll 모든 데이터 삭제 여부
     * @return 초기화 결과 메시지
     */@Transactional(propagation = Propagation.REQUIRES_NEW)
    public String resetData(boolean deleteAll) {
        try {
            log.info("데이터 초기화 시작 - 전체 삭제: {}", deleteAll);

            if (deleteAll) {
                // 모든 데이터 삭제 (의존성 순서 고려)
                long scheduleCount = scheduleRepository.count();
                long examDetailCount = examDetailRepository.count();
                long memberCount = memberRepository.count();
                long certificationCount = certificationRepository.count();
                long authorityCount = authorityRepository.count();

                scheduleRepository.deleteAll();
                examDetailRepository.deleteAll();
                memberRepository.deleteAll();
                certificationRepository.deleteAll();
                authorityRepository.deleteAll();

                log.info("전체 데이터 삭제 완료 - 삭제된 데이터 수: 자격증 일정({})개, 시험 상세정보({})개, 회원({})개, 자격증({})개, 기관({})개",
                        scheduleCount, examDetailCount, memberCount, certificationCount, authorityCount);

                return "모든 데이터가 성공적으로 초기화되었습니다.";
            } else {
                // 더미 데이터만 선택적으로 삭제
                Instant thresholdDate = Instant.now().plus(3, ChronoUnit.MONTHS);

                // 1. 자격증 일정 삭제
                List<CertificationSchedule> dummySchedules = scheduleRepository.findAll().stream()
                        .filter(s -> s.getScheduledDate().isAfter(thresholdDate))
                        .collect(Collectors.toList());

                int schedulesDeleted = dummySchedules.size();
                scheduleRepository.deleteAll(dummySchedules);
                log.info("삭제된 자격증 일정: {}개", schedulesDeleted);

                // 2. 시험 상세정보 삭제
                List<Long> certIds = dummySchedules.stream()
                        .map(s -> s.getCertification().getId())
                        .distinct()
                        .collect(Collectors.toList());

                int examDetailsDeleted = certIds.isEmpty() ? 0 :
                        examDetailRepository.deleteAllByCertificationIdIn(certIds);
                log.info("삭제된 시험 상세정보: {}개", examDetailsDeleted);

                // 3. 회원 데이터 삭제
                List<Member> dummyMembers = memberRepository.findByEmailContaining("user");
                int membersDeleted = dummyMembers.size();
                memberRepository.deleteAll(dummyMembers);
                log.info("삭제된 회원: {}개", membersDeleted);

                return String.format(
                        "더미 데이터가 성공적으로 초기화되었습니다. (삭제된 회원: %d명, 삭제된 시험 상세정보: %d개, 삭제된 자격증 일정: %d개)",
                        membersDeleted, examDetailsDeleted, schedulesDeleted
                );
            }
        } catch (Exception e) {
            log.error("데이터 초기화 중 오류 발생", e);
            return "데이터 초기화 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected List<Authority> generateAuthorities() {
        List<Authority> authorities = new ArrayList<>();

        for (String name : AUTHORITY_NAMES) {
            // 이미 존재하는 기관인지 확인
            Authority existingAuthority = authorityRepository.findByName(name).orElse(null);

            if (existingAuthority == null) {
                // 기관명에서 영문/한글 구분하여 도메인 생성
                String domain = name.matches(".*[a-zA-Z].*") ?
                        name.toLowerCase().replaceAll("\\s+", "") + ".com" :
                        romanize(name) + "." + WEBSITE_DOMAINS[random.nextInt(WEBSITE_DOMAINS.length)];

                String websiteUrl = "https://www." + domain;
                String applicationUrl = "https://apply." + domain;
                String iconImageUrl = ICON_URLS[random.nextInt(ICON_URLS.length)];

                Authority authority = Authority.of(name, websiteUrl, applicationUrl, iconImageUrl);
                authorities.add(authorityRepository.save(authority));
                log.debug("Created authority: {}", name);
            } else {
                authorities.add(existingAuthority);
                log.debug("Authority already exists: {}", name);
            }
        }

        return authorities;
    }

    /**
     * 한글 기관명을 로마자로 간단하게 변환 (실제 정확한 로마자 변환은 아님)
     */
    private String romanize(String koreanName) {
        // 간단한 변환 로직 (실제로는 더 복잡하게 구현해야 함)
        return koreanName
                .replace("한국", "korea")
                .replace("정보", "info")
                .replace("산업", "industry")
                .replace("기술", "tech")
                .replace("진흥", "promotion")
                .replace("협회", "association")
                .replace("공단", "corporation")
                .replace("부", "ministry")
                .replace(" ", "")
                .toLowerCase();
    }

    public int generateCertifications(List<Authority> authorities, int count) {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            log.warn("카테고리 데이터가 없어 자격증을 생성할 수 없습니다.");
            return 0;
        }

        // 대분류 카테고리 필터링
        List<Category> parentCategories = categories.stream()
                .filter(c -> c.getParentCategory() == null)
                .collect(Collectors.toList());

        // 소분류 카테고리 필터링
        List<Category> subCategories = categories.stream()
                .filter(c -> c.getParentCategory() != null)
                .collect(Collectors.toList());

        if (subCategories.isEmpty()) {
            // 소분류 카테고리가 없다면 기본 소분류 카테고리 생성
            Category parentCategory = parentCategories.get(0);
            Category defaultSubCategory = Category.of("기본", parentCategory);
            subCategories.add(categoryRepository.save(defaultSubCategory));
        }

        int created = 0;

        for (int i = 0; i < count; i++) {
            String name = generateRandomCertName();

            // 이미 존재하는 자격증인지 확인
            if (certificationRepository.findByName(name).isPresent()) {
                continue;
            }

            Authority authority = authorities.get(random.nextInt(authorities.size()));

            // 대분류 카테고리 선택
            Category category = parentCategories.get(random.nextInt(parentCategories.size()));

            // 소분류 카테고리 선택 (해당 대분류의 하위 카테고리 중에서)
            List<Category> categorySubCategories = subCategories.stream()
                    .filter(c -> c.getParentCategory() != null && c.getParentCategory().getId().equals(category.getId()))
                    .collect(Collectors.toList());

            // 소분류 카테고리가 없으면 기본 소분류 사용
            Category subCategory = categorySubCategories.isEmpty() ?
                    subCategories.get(0) :
                    categorySubCategories.get(random.nextInt(categorySubCategories.size()));

            Certification certification = Certification.of(name, authority, category);

            // 임의의 조회수 설정
            int viewCount = random.nextInt(1000);
            for (int j = 0; j < viewCount; j++) {
                certification.incrementViewCount();
            }

            certificationRepository.save(certification);
            created++;

            if (created % 10 == 0) {
                log.debug("Created {} certifications...", created);
            }
        }

        log.info("총 {}개의 자격증 생성 완료", created);
        return created;
    }

    private int generateExamDetails() {
        List<Certification> certifications = certificationRepository.findAll();
        int created = 0;

        if (certifications.isEmpty()) {
            log.warn("No certifications found to create exam details");
            return 0;
        }

        // 각 자격증마다 1~3개의 시험 상세정보 생성
        for (Certification certification : certifications) {
            // 기존 시험 상세정보 확인
            if (!examDetailRepository.findByCertificationId(certification.getId()).isEmpty()) {
                log.debug("Exam details already exist for certification: {}", certification.getName());
                continue;
            }

            // 필기와 실기 중 랜덤으로 선택 또는 둘 다 생성
            boolean hasWritten = random.nextBoolean() || random.nextBoolean(); // 75% 확률로 필기 시험 포함
            boolean hasPractical = random.nextBoolean() || !hasWritten; // 최소한 하나의 시험 유형 포함

            // 필기 시험 생성
            if (hasWritten) {
                createExamDetail(certification, ExamType.WRITTEN, created);
                created++;
            }

            // 실기 시험 생성
            if (hasPractical) {
                createExamDetail(certification, ExamType.PRACTICAL, created);
                created++;
            }
        }

        return created;
    }
    private void createExamDetail(Certification certification, ExamType examType, int counter) {
        // 과목명 생성
        String subject = generateSubjectName(certification.getName(), examType);

        // 문제 유형 (필기는 객관식 위주, 실기는 서술형 위주)
        ProblemType problemType = (examType == ExamType.WRITTEN) ?
                (random.nextInt(10) < 8 ? ProblemType.MULTIPLE_CHOICE : ProblemType.SHORT_ANSWER) :
                (random.nextInt(10) < 7 ? ProblemType.LONG_ANSWER : ProblemType.MULTIPLE_CHOICE);

        // 문제 수
        int totalProblems = examType == ExamType.WRITTEN ?
                20 + random.nextInt(80) : // 필기: 20~100문제
                1 + random.nextInt(10);   // 실기: 1~10문제

        // 시험시간(분)
        int duration = examType == ExamType.WRITTEN ?
                60 + random.nextInt(60) :  // 필기: 60~120분
                90 + random.nextInt(120);  // 실기: 90~210분

        CertificationExamDetail examDetail = CertificationExamDetail.of(
                certification,
                examType,  // 여기서 명시적으로 ExamType 지정
                subject,
                problemType,
                totalProblems,
                duration
        );

        examDetailRepository.save(examDetail);

        if (counter % 10 == 0) {
            log.debug("Created {} exam details...", counter);
        }
    }

    /**
     * 자격증 일정을 생성합니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int generateCertificationSchedules() {
        List<Certification> certifications = certificationRepository.findAll();
        int created = 0;

        if (certifications.isEmpty()) {
            log.warn("자격증 데이터가 없어 일정을 생성할 수 없습니다.");
            return 0;
        }

        // 각 자격증의 ExamDetail을 확인하고 해당하는 시험 일정 생성
        for (Certification certification : certifications) {
            List<CertificationExamDetail> examDetails = examDetailRepository.findByCertificationId(certification.getId());

            // 이미 존재하는 일정 확인
            List<CertificationSchedule> existingSchedules = scheduleRepository.findAllByCertificationId(certification.getId());
            if (!existingSchedules.isEmpty()) {
                log.debug("이미 일정이 존재하는 자격증: {}, 기존 일정 수: {}", certification.getName(), existingSchedules.size());
                continue;
            }

            // 모든 자격증에 대해 필기/실기 일정 생성
            for (ExamType examType : ExamType.values()) {
                // 각 일정 유형(접수, 시험, 발표)별로 1~3개의 일정 생성
                int scheduleRounds = 1 + random.nextInt(2); // 1~3회 시험 일정

                for (int i = 0; i < scheduleRounds; i++) {
                    created += createSchedulesForExamType(certification, examType, i);
                }
            }
        }

        return created;
    }

    private int createSchedulesForExamType(Certification certification, ExamType examType, int round) {
        int created = 0;
        LocalDateTime now = LocalDateTime.now();

        // 연도 기준으로 일정 생성 (상/하반기)
        int year = now.getYear() + (now.getMonthValue() > 6 ? 1 : 0) + (round > 0 ? 1 : 0);

        // 1. 접수 일정
        LocalDateTime applicationStart;
        LocalDateTime applicationEnd;

        // 시즌별 일정 (상반기/하반기)
        if (round % 2 == 0) {
            // 상반기 (1~6월)
            applicationStart = LocalDateTime.of(year, 2 + random.nextInt(2), 1 + random.nextInt(25), 9, 0)
                    .plusDays(random.nextInt(10));
        } else {
            // 하반기 (7~12월)
            applicationStart = LocalDateTime.of(year, 8 + random.nextInt(2), 1 + random.nextInt(25), 9, 0)
                    .plusDays(random.nextInt(10));
        }

        applicationEnd = applicationStart.plusDays(14 + random.nextInt(14)); // 14~28일 접수 기간

        CertificationSchedule applicationSchedule = CertificationSchedule.of(
                certification,
                examType,
                ScheduleType.APPLICATION_DATE,
                applicationStart.toInstant(ZoneOffset.UTC)
        );
        scheduleRepository.save(applicationSchedule);
        created++;

        // 2. 시험 일정 (접수 종료 후)
        LocalDateTime examStart = applicationEnd.plusDays(14 + random.nextInt(14)); // 접수 종료 2~4주 후
        LocalDateTime examEnd;

        // 시험 기간: 필기는 당일, 실기는 2~5일
        if (examType == ExamType.WRITTEN) {
            examEnd = examStart.plusHours(4 + random.nextInt(4)); // 4~8시간 시험
        } else {
            examEnd = examStart.plusDays(1 + random.nextInt(4)); // 2~5일 시험 기간
        }

        CertificationSchedule examSchedule = CertificationSchedule.of(
                certification,
                examType,
                ScheduleType.EXAM_DATE,
                examStart.toInstant(ZoneOffset.UTC)
        );
        scheduleRepository.save(examSchedule);
        created++;

        // 3. 합격자 발표 일정 (시험 종료 후)
        LocalDateTime announcementStart = examEnd.plusDays(14 + random.nextInt(14)); // 시험 종료 2~4주 후

        // 발표 기간: 필기는 당일, 실기도 대부분 당일
        LocalDateTime announcementEnd = announcementStart.plusHours(random.nextBoolean() ? 0 : 24);

        CertificationSchedule announcementSchedule = CertificationSchedule.of(
                certification,
                examType,
                ScheduleType.PASS_ANNOUNCEMENT,
                announcementStart.toInstant(ZoneOffset.UTC)
        );
        scheduleRepository.save(announcementSchedule);
        created++;

        log.debug("자격증 {} 일정 생성 ({}, 차수 {}): {} 개 생성",
                certification.getName(), examType.getValue(), round + 1, created);

        return created;
    }
    private int generateMembers(int count) {
        int created = 0;

        for (int i = 0; i < count; i++) {
            String socialId = UUID.randomUUID().toString();
            String socialProvider = SOCIAL_PROVIDERS[random.nextInt(SOCIAL_PROVIDERS.length)];
            String email = generateRandomEmail();

            // 이미 존재하는 이메일인지 확인
            if (memberRepository.findByEmail(email).isPresent()) {
                continue;
            }

            String nickname = generateRandomNickname();
            String profileImageUrl = PROFILE_IMAGES[random.nextInt(PROFILE_IMAGES.length)];
            String introduction = INTRODUCTIONS[random.nextInt(INTRODUCTIONS.length)];

            Member member = Member.of(socialId, socialProvider, email, nickname, profileImageUrl, introduction);
            memberRepository.save(member);
            created++;

            if (created % 10 == 0) {
                log.debug("Created {} members...", created);
            }
        }

        return created;
    }

    private String generateRandomCertName() {
        String prefix = CERT_PREFIXES[random.nextInt(CERT_PREFIXES.length)];
        String suffix = CERT_SUFFIXES[random.nextInt(CERT_SUFFIXES.length)];

        // 접미사나 접두사를 랜덤하게 생략하거나 숫자 추가
        if (random.nextBoolean()) {
            // 숫자 추가 (1급, 2급 등)
            int level = random.nextInt(3) + 1;
            if (random.nextBoolean()) {
                return prefix + " " + level + "급 " + suffix;
            } else {
                return prefix + " " + suffix + " " + level + "급";
            }
        } else {
            return prefix + " " + suffix;
        }
    }

    private String generateRandomEmail() {
        String username = "user" + random.nextInt(100000);
        String domain = EMAIL_DOMAINS[random.nextInt(EMAIL_DOMAINS.length)];
        return username + "@" + domain;
    }

    private String generateRandomNickname() {
        String prefix = NICKNAME_PREFIXES[random.nextInt(NICKNAME_PREFIXES.length)];
        String suffix = NICKNAME_SUFFIXES[random.nextInt(NICKNAME_SUFFIXES.length)];
        return prefix + " " + suffix;
    }

    private String generateSubjectName(String certName, ExamType examType) {
        // 자격증명에서 키워드 추출
        String keyword = certName.split(" ")[0];

        // 시험 유형에 따른 적절한 과목명 생성
        if (examType == ExamType.WRITTEN) {
            return examType.getValue() + " - " + keyword + " " + SUBJECT_NAMES[random.nextInt(SUBJECT_NAMES.length)];
        } else {
            return examType.getValue() + " - " + keyword + " 실무 응용";
        }
    }
}