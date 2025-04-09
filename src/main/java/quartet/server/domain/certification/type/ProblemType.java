package quartet.server.domain.certification.type;

public enum ProblemType {
    MULTIPLE_CHOICE("객관식"), // 개수 구분 없는 경우
    SHORT_ANSWER("단답형"),
    ESSAY("논술형"),
    MULTIPLE_CHOICE_4("객관식 4지 택일형"),
    MULTIPLE_CHOICE_5("객관식 5지 택일형"),
    SHORT_ANSWER_OR_ESSAY("단답형 및 주관식 논술형"),
    THEORETICAL("필답형"),
    PRACTICAL("작업형"),
    COMPOSITE("복합형"),
    NONE("해당 없음"); // 임시


    private final String value;

    ProblemType(final String value) {
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
