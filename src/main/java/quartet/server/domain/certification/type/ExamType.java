package quartet.server.domain.certification.type;

public enum ExamType {
    WRITTEN("필기"),
    PRACTICAL("실기"),
    INTERVIEW("면접"),
    NONE("구분 없음");

    private final String value;

    ExamType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
