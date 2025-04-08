package quartet.server.domain.certification.type;

public enum TechnicalGradeType {
    ENGINEER("기사"),
    TECHNICIAN("기능사"),
    INDUSTRIAL_ENGINEER("산업기사"),
    TECHNICAL_MASTER("기술사"),
    MASTER_CRAFTSMAN("기능장"),
    PROFESSIONAL("전문자격");

    private final String value;

    TechnicalGradeType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
} 