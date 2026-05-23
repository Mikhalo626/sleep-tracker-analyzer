import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class UserClassificationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final LocalTime DAY_START = LocalTime.of(6, 0);
    private static final LocalTime DAY_END = LocalTime.of(18, 0);
    private static final LocalTime EVENING_BOUNDARY = LocalTime.of(21, 0);

    private static final LocalTime OWL_START_BOUNDARY = LocalTime.of(23, 0);
    private static final LocalTime OWL_END_BOUNDARY = LocalTime.of(9, 0);

    private static final LocalTime LARK_START_BOUNDARY = LocalTime.of(22, 0);
    private static final LocalTime LARK_END_BOUNDARY = LocalTime.of(7, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        Map<Chronotype, Long> counts = sessions.stream()
                .filter(s -> !isDaySession(s))
                .map(this::classifySession)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        long larks = counts.getOrDefault(Chronotype.ЖАВОРОНОК, 0L);
        long owls = counts.getOrDefault(Chronotype.СОВА, 0L);
        long doves = counts.getOrDefault(Chronotype.ГОЛУБЬ, 0L);

        Chronotype finalType;
        if (larks > owls && larks > doves) {
            finalType = Chronotype.ЖАВОРОНОК;
        } else if (owls > larks && owls > doves) {
            finalType = Chronotype.СОВА;
        } else {
            finalType = Chronotype.ГОЛУБЬ;
        }

        return new SleepAnalysisResult("Классификация пользователя (хронотип)", finalType);
    }

    private boolean isDaySession(SleepingSession session) {
        LocalTime start = session.getStartTime().toLocalTime();
        LocalTime end = session.getEndTime().toLocalTime();
        return start.isAfter(DAY_START) && start.isBefore(DAY_END) && end.isBefore(EVENING_BOUNDARY);
    }

    private Chronotype classifySession(SleepingSession session) {
        LocalTime start = session.getStartTime().toLocalTime();
        LocalTime end = session.getEndTime().toLocalTime();

        boolean owlCondition = start.isAfter(OWL_START_BOUNDARY) && end.isAfter(OWL_END_BOUNDARY);
        boolean larkCondition = start.isBefore(LARK_START_BOUNDARY) && end.isBefore(LARK_END_BOUNDARY);

        if (owlCondition) return Chronotype.СОВА;
        if (larkCondition) return Chronotype.ЖАВОРОНОК;
        return Chronotype.ГОЛУБЬ;
    }
}
