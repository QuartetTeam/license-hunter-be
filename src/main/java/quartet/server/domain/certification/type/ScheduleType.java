package quartet.server.domain.certification.type;

public enum ScheduleType {
    APPLICATION_START_DATE("접수 시작일"),
    EXAM_START_DATE("시험 시작일"),
    PASS_ANNOUNCEMENT("합격자 발표일");

    private final String value;

    ScheduleType(final String value){
        this.value = value;
    }

    public String getValue(){
        return value;
    }
}
