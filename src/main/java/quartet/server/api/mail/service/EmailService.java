package quartet.server.api.mail.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import quartet.server.domain.mail.exception.EmailSendingFailedException;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendTemplateEmail(final String to, final String subject, final String templateName, final Map<String, Object> contextData) {
        try {
            // 이메일 메시지 생성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 기본 정보 설정
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("oneul.team3@gmail.com", "자격저격");

            // Thymeleaf 컨텍스트 생성 및 데이터 설정
            Context context = new Context();
            contextData.forEach(context::setVariable);

            // 템플릿을 처리하여 HTML 내용 생성
            String htmlContent = templateEngine.process("email/" + templateName, context);
            helper.setText(htmlContent, true);

            // 이메일 발송
            mailSender.send(message);
            log.debug("이메일 발송 성공: to={}, subject={}, template={}", to, subject, templateName);
        } catch (Exception e) {
            log.error("이메일 발송 실패: to={}, subject={}, template={}", to, subject, templateName, e);
            throw new EmailSendingFailedException(e);
        }
    }
}