package quartet.server.api.dummy;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/data")
@RequiredArgsConstructor
@Slf4j
public class DataGeneratorController {

    private final DataGeneratorService dataGeneratorService;

    /**
     * 개발 및 테스트용 더미 데이터를 생성합니다.
     *
     * @param certCount 생성할 자격증 수 (기본값: 100)
     * @param memberCount 생성할 회원 수 (기본값: 50)
     * @return 생성 결과 메시지
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateData(
            @RequestParam(name = "certCount", required = false, defaultValue = "100") int certCount,
            @RequestParam(name = "memberCount", required = false, defaultValue = "50") int memberCount
    ) {
        String result = dataGeneratorService.generateData(certCount, memberCount);
        log.info("Generating dummy data - Certifications: {}, Members: {}", certCount, memberCount);

        return ResponseEntity.ok(result);
    }

    /**
     * 생성된 더미 데이터를 초기화합니다.
     *
     * @param deleteAll true인 경우 모든 데이터 삭제, false인 경우 더미 데이터만 삭제
     * @return 초기화 결과 메시지
     */
    @PostMapping("/reset")
    public ResponseEntity<String> resetData(
            @RequestParam(name = "deleteAll",required = false, defaultValue = "false") Boolean deleteAll) {

        String result = dataGeneratorService.resetData(deleteAll);
        log.info("Resetting dummy data - Delete all: {}", deleteAll);
        return ResponseEntity.ok(result);
    }
}