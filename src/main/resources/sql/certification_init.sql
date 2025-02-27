INSERT INTO quartet_db.category (id, name, parent_category_id, is_default) VALUES
    (1, '정보통신', NULL,true),
    (2, '통신', 1,NULL,false),
    (3, 'IT', NULL,true),
    (4, '소프트웨어', 3,false),
    (5, '건설', NULL,true),
    (6, '토목', 5,NULL,false),
    (7, '전기', NULL,false),
    (8, '전력설비', 7,false),
    (9, '기계', NULL,true),
    (10, '자동화', 9,false),
    (11, '환경', NULL,true),
    (12, '에너지', 11,false);

INSERT INTO quartet_db.authority (id, application_url, icon_image_url, name, website_url) VALUES
(1, 'https://www.q-net.or.kr/man001.do?gSite=Q&gIntro=Y', NULL, '산업인력공단', 'https://www.hrdkorea.or.kr/'),
(2, 'https://www.kca.kr/', NULL, '한국방송통신전파진흥원', 'https://www.kca.kr/'),
(3, 'https://www.kpe.or.kr/', NULL, '한국전기기술인협회', 'https://www.kpe.or.kr/'),
(4, 'https://www.kef.or.kr/', NULL, '한국기술사회', 'https://www.kef.or.kr/'),
(5, 'https://www.kcs.or.kr/', NULL, '한국컴퓨터학회', 'https://www.kcs.or.kr/'),
(6, 'https://www.kicem.or.kr/', NULL, '한국건설기술인협회', 'https://www.kicem.or.kr/'),
(7, 'https://www.koema.or.kr/', NULL, '한국전기산업진흥회', 'https://www.koema.or.kr/'),
(8, 'https://www.koita.or.kr/', NULL, '한국산업기술진흥협회', 'https://www.koita.or.kr/'),
(9, 'https://www.kwa.or.kr/', NULL, '한국전력공사', 'https://www.kwa.or.kr/'),
(10, 'https://www.kemi.or.kr/', NULL, '한국환경산업기술원', 'https://www.kemi.or.kr/'),
(11, 'https://www.kcm.or.kr/', NULL, '한국기계산업진흥회', 'https://www.kcm.or.kr/');

INSERT INTO quartet_db.certification (id, name, view_count, authority_id, category_id) VALUES
(1, '정보처리기사', 0, 1, 2),
(2, '네트워크관리사', 0, 2, 2),
(3, '전기기사', 0, 3, 8),
(4, '건설안전기사', 0, 6,6),
(5, '정보보안기사', 0, 5, 4),
(6, '기계설계기사', 0, 11,10),
(7, '에너지관리기사', 0, 10, 12),
(8, '소프트웨어설계기사', 0, 4,4),
(9, '전기공사기사', 0, 7,8),
(10, '환경기술사', 0, 10,12),
(11, '자동화제어기사', 0, 11,10);

INSERT INTO quartet_db.certification_description (id, description, qualification, certification_id) VALUES
(1, '정보통신 활용 기술을 평가합니다', '4년제 학사', 1),
(2, '네트워크 유지보수 능력을 평가합니다', '초급 자격', 2),
(3, '전기설비 안전 및 관리 능력을 평가합니다', '4년제 학사', 3),
(4, '건설 현장의 안전관리 역량을 평가합니다', '4년제 학사', 4),
(5, '정보 보안 및 해킹 방어 능력을 평가합니다', '4년제 학사', 5),
(6, '기계 설계 및 3D 모델링 능력을 평가합니다', '4년제 학사', 6),
(7, '에너지 절약 및 효율적 관리 능력을 평가합니다', '4년제 학사', 7),
(8, '소프트웨어 설계 및 유지보수 능력을 평가합니다', '4년제 학사', 8),
(9, '전기 공사 및 설계 역량을 평가합니다', '4년제 학사', 9),
(10, '환경 기술 및 오염 제어 능력을 평가합니다', '4년제 학사', 10),
(11, '자동화 공정 설계 및 제어 능력을 평가합니다', '4년제 학사', 11);

INSERT INTO quartet_db.certification_exam_details (id, duration, exam_type, problem_type, subject, total_problems_count, certification_id) VALUES
(1, 30, 'WRITTEN', 'MULTIPLE_CHOICE', '데이터베이스', 30, 1),
(2, 30, 'WRITTEN', 'MULTIPLE_CHOICE', '프로그래밍 언어', 30, 1),
(3, 60, 'PRACTICAL', 'LONG_ANSWER', '프로그래밍 활용', 20, 1),
(4, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '네트워크 구조', 40, 2),
(5, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '전기 이론', 40, 3),
(6, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '건설 안전 법규', 40, 4),
(7, 60, 'WRITTEN', 'LONG_ANSWER', '정보 보안 원리', 40, 5),
(8, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '기계 설계 기본', 40, 6),
(9, 60, 'WRITTEN', 'LONG_ANSWER', '에너지 절약 기술', 40, 7),
(10, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '소프트웨어 공학', 40, 8),
(11, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '전기 공사 실무', 40, 9),
(12, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '환경 공학 개론', 40, 10),
(13, 60, 'WRITTEN', 'MULTIPLE_CHOICE', '자동화 제어 이론', 40, 11);

INSERT INTO quartet_db.certification_schedule
(id, created_at, scheduled_date, updated_at, exam_type, schedule_type,certification_id) VALUES
(1, '2025-02-25 00:00:00.000', '2025-02-25 00:00:00.000', '2025-02-25 00:00:00.000', 'WRITTEN', 'EXAM_DATE', 1),
(2, '2025-02-25 00:00:00.000', '2025-02-25 00:00:00.000', '2025-02-25 00:00:00.000', 'PRACTICAL', 'APPLICATION_DATE', 1),
(3, '2025-06-01 00:00:00.000', '2025-06-01 00:00:00.000', '2025-06-15 00:00:00.000', 'WRITTEN', 'EXAM_DATE', 2),
(4, '2025-06-01 00:00:00.000', '2025-06-01 00:00:00.000', '2025-06-20 00:00:00.000', 'PRACTICAL', 'APPLICATION_DATE', 2),
(5, '2025-07-01 00:00:00.000', '2025-07-01 00:00:00.000', '2025-07-15 00:00:00.000', 'WRITTEN', 'EXAM_DATE', 3),
(6, '2025-07-01 00:00:00.000', '2025-07-01 00:00:00.000', '2025-07-20 00:00:00.000', 'PRACTICAL', 'APPLICATION_DATE', 3);
