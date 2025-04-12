package quartet.server.domain.certification.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum ScheduleGroup {
    APPLICATION("접수일", Set.of(ScheduleType.APPLICATION_START, ScheduleType.APPLICATION_END)),
    EXAM("시험일", Set.of(ScheduleType.EXAM_START, ScheduleType.EXAM_END)),
    PASS("합격일", Collections.singleton(ScheduleType.PASS_ANNOUNCEMENT)),
    EMPTY("", Collections.EMPTY_SET);

    private final String value;
    private final Set<ScheduleType> scheduleTypes;

    public static ScheduleGroup findByScheduleType(ScheduleType scheduleType) {
        return Arrays.stream(ScheduleGroup.values())
                .filter(group -> group.containsScheduleType(scheduleType))
                .findAny()
                .orElse(EMPTY);
    }

    private boolean containsScheduleType(ScheduleType scheduleType) {
        return this.scheduleTypes.contains(scheduleType);
    }
}
