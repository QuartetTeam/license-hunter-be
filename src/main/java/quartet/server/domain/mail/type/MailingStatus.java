package quartet.server.domain.mail.type;

import lombok.Getter;

@Getter
public enum MailingStatus {
    ACTIVE, PAUSED;

    public static MailingStatus opposite(MailingStatus status) {
        if(status == null || status == PAUSED) {
            return ACTIVE;
        }else{
            return PAUSED;
        }
    }
}
