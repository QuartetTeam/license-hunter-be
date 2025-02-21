package quartet.server.domain.auth.exception;

import quartet.server.core.code.MemberErrorCode;
import quartet.server.domain.member.exception.MemberException;

public class RefreshTokenNotFoundException extends MemberException {
    public RefreshTokenNotFoundException() {
        super(MemberErrorCode.MEMBER_NOT_FOUND);
    }
}