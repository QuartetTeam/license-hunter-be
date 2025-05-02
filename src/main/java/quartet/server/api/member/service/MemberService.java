package quartet.server.api.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import quartet.server.api.member.dto.response.*;
import quartet.server.core.code.MemberErrorCode;
import quartet.server.domain.auth.repository.RefreshTokenRepository;
import quartet.server.domain.calender.exception.CategorySelectionLimitExceededException;
import quartet.server.domain.calender.repository.CalendarRepository;
import quartet.server.domain.category.model.MainCategory;
import quartet.server.domain.category.repository.MainCategoryRepository;
import quartet.server.domain.image.service.ImageService;
import quartet.server.domain.mail.repository.MailingRepository;
import quartet.server.domain.mail.type.MailingStatus;
import quartet.server.domain.member.exception.MemberException;
import quartet.server.domain.member.exception.MemberNotFoundException;
import quartet.server.domain.member.model.Member;
import quartet.server.domain.member.model.MemberCategory;
import quartet.server.domain.member.repository.MemberCategoryRepository;
import quartet.server.domain.member.repository.MemberRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final MemberCategoryRepository memberCategoryRepository;
    private final CalendarRepository calendarRepository;
    private final MailingRepository mailingRepository;
    private final ImageService imageService;

    public MemberInfoResponse getMyInfo(final long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        List<String> interests =memberCategoryRepository.findByMemberId(memberId)
                .stream()
                .map(memberCategory -> memberCategory.getMainCategory().getName())
                .toList();

        return new MemberInfoResponse(member.getEmail(), member.getNickname(), member.getProfileImageUrl(), member.getMailingStatus(), interests);
    }

    @Transactional
    public MemberNicknameResponse updateNickname(final long memberId, final String nickname) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        member.updateNickname(nickname);

        return new MemberNicknameResponse(member.getNickname());
    }

    @Transactional
    public MemberEmailResponse updateEmail(final long memberId, final String email) {
        if (memberRepository.existsByEmailAndIdNot(email, memberId)) {
            throw new MemberException(MemberErrorCode.EMAIL_ALREADY_EXISTS);
        }

        final Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        member.updateEmail(email);

        return new MemberEmailResponse(member.getEmail());
    }

    @Transactional
    public MemberProfileImageResponse updateProfileImage(final long memberId, final MultipartFile newFile) {
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String oldImageUrl = member.getProfileImageUrl();
        String updatedImageUrl = imageService.updateImage(oldImageUrl, newFile);
        member.updateProfileImage(updatedImageUrl);

        return new MemberProfileImageResponse(updatedImageUrl);
    }

    @Transactional
    public MemberInterestResponse updateInterests(final long memberId, final List<Long> categoryIds) {
        if (categoryIds.size() > 3) {
            throw new CategorySelectionLimitExceededException();
        }

        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);

        // 기존 관심 분야 제거
        member.getMemberCategories().clear();

        // 새 관심 분야 추가
        final List<MainCategory> categories = mainCategoryRepository.findAllById(categoryIds);
        final List<MemberCategory> newInterests = categories.stream()
                .map(category -> MemberCategory.of(member, category))
                .toList();

        member.getMemberCategories().addAll(newInterests);

        // 결과 응답
        List<String> categoryNames = categories.stream()
                .map(MainCategory::getName)
                .collect(Collectors.toList());

        return new MemberInterestResponse(categoryNames);
    }

    @Transactional
    public MemberMailingStatusResponse updateMailingStatus(final long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        final MailingStatus oppositeStatus = MailingStatus.opposite(member.getMailingStatus());
        member.updateMailingStatus(oppositeStatus);
        System.out.println(member.getMailingStatus());

        return new MemberMailingStatusResponse(oppositeStatus);
    }

    @Transactional
    public void deleteMember(final long memberId) {
        final Member member = memberRepository.findById(memberId).orElseThrow(MemberNotFoundException::new);
        mailingRepository.deleteByMemberId(memberId);
        calendarRepository.deleteByMemberId(memberId);
        memberCategoryRepository.deleteByMemberId(memberId);
        refreshTokenRepository.deleteByMemberId(memberId);
        memberRepository.delete(member);
    }
}