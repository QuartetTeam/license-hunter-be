package quartet.server.api.calendar.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quartet.server.api.calendar.dto.response.CalendarResponse;
import quartet.server.api.calendar.query.CalendarQueryRepository;
import quartet.server.domain.calender.exception.CalendarAlreadyExistsException;
import quartet.server.domain.calender.exception.CalendarNotFoundException;
import quartet.server.domain.calender.model.Calendar;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.certification.exception.CertificationNotFoundException;
import quartet.server.domain.certification.model.Certification;
import quartet.server.domain.certification.repository.CertificationRepository;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.repository.MemberRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CalendarService {

    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;
    private final CertificationRepository certificationRepository;
    private final CalendarQueryRepository calendarQueryRepository;

    public List<CalendarResponse> getCalendarsByMemberId(final long memberId) {

        return calendarQueryRepository.findCalendarsByMemberId(memberId);
    }

    @Transactional
    public void subscribeCalendar(final long memberId, final long certificationId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        final Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(CalendarNotFoundException::new);

        if (calendarRepository.existsByMemberIdAndCertificationId(member.getId(), certification.getId())) {
            throw new CalendarAlreadyExistsException();
        }

        final Calendar calendar = Calendar.of(member, certification);

        calendarRepository.save(calendar);
    }

    @Transactional
    public void unsubscribeCalendar(final long memberId, final long certificationId) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        final Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(CertificationNotFoundException::new);

        final Calendar calendar = calendarRepository.findByMemberIdAndCertificationId(member.getId(), certification.getId())
                .orElseThrow(CalendarNotFoundException::new);

        calendarRepository.delete(calendar);
    }
}