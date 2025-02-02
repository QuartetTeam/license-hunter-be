package quartet.server.core.code;

import org.springframework.http.HttpStatus;

public interface CommonCode {
    HttpStatus getHttpStatus();
    String getMessage();
    int getStatusValue();
}