package quartet.server.domain.member.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class MemberException extends BaseException {
    protected MemberException(ResponseCode errorCode) {
        super(errorCode);
    }
}
