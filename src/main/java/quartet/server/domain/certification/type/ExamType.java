package quartet.server.domain.certification.type;

public enum ExamType {
    WRITTEN("필기"),
    PRACTICAL("실기");

    private final String value;

    ExamType(String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
