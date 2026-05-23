import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public class SleeplessNightsAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final LocalTime NIGHT_END_BOUNDARY = LocalTime.of(6, 0);
    private static final long SINGLE_DAY_STEP = 1L;

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        SleepingSession firstSession = sessions.stream()
                .min(Comparator.comparing(SleepingSession::getStartTime))
                .get();
        SleepingSession lastSession = sessions.stream()
                .max(Comparator.comparing(SleepingSession::getEndTime))
                .get();

        LocalDate startDate = firstSession.getStartTime().toLocalDate();
        if (firstSession.getStartTime().toLocalTime().isAfter(LocalTime.NOON)) {
            startDate = startDate.plusDays(SINGLE_DAY_STEP);
        }

        LocalDate endDate = lastSession.getEndTime().toLocalDate();
        long totalNights = ChronoUnit.DAYS.between(startDate, endDate) + SINGLE_DAY_STEP;

        if (totalNights <= 0) {
            return new SleepAnalysisResult("Количество бессонных ночей", 0L);
        }

        LocalDate finalStartDate = startDate;
        long sleeplessNightsCount = Stream.iterate(finalStartDate, date -> date.plusDays(SINGLE_DAY_STEP))
                .limit(totalNights)
                .filter(nightDate -> {
                    LocalDateTime intervalStart = nightDate.atStartOfDay();
                    LocalDateTime intervalEnd = nightDate.atTime(NIGHT_END_BOUNDARY);

                    return sessions.stream().noneMatch(session ->
                            session.getStartTime().isBefore(intervalEnd) && session.getEndTime().isAfter(intervalStart)
                    );
                })
                .count();

        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNightsCount);
    }
}