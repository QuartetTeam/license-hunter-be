package quartet.server.domain.member.exception;

import quartet.server.core.code.MemberErrorCode;

public class MemberNotFoundException extends MemberException {
    public MemberNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}