package quartet.server.domain.certification.type;

public enum QualificationType {
    T("국가기술자격"),
    S("국가전문자격"),
    P ("민간자격");

    private final String value;

    QualificationType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}