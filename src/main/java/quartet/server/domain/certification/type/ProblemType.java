package quartet.server.domain.certification.type;

public enum ProblemType {
    MULTIPLE_CHOICE_4("객관식 4지 택일형"),
    MULTIPLE_CHOICE_5("객관식 5지 택일형"),
    SHORT_ANSWER("주관식"),
    LONG_ANSWER("논술형");

    private final String value;

    ProblemType(final String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
