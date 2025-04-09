package quartet.server.domain.certification.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExamType {
    WRITTEN("필기"),
    PRACTICAL("실기"),
    INTERVIEW("면접");

    private final String value;

    public String getValue(){
        return value;
    }
}
