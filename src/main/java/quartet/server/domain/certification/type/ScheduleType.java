package quartet.server.domain.certification.type;

public enum ScheduleType {
    APPLICATION_START("접수 시작일"),
    APPLICATION_END("접수 종료일"),
    EXAM_START("시험 시작일"),
    EXAM_END("시험 종료일"),
    PASS_ANNOUNCEMENT("합격자 발표일");

    private final String value;

    ScheduleType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
