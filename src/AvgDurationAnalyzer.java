import java.util.List;
import java.util.function.Function;

public class AvgDurationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        double avg = sessions.stream()
                .mapToLong(SleepingSession::getDurationInMinutes)
                .average()
                .orElse(0.0);
        return new SleepAnalysisResult("Средняя продолжительность сессии (в минутах)", Math.round(avg));
    }
}
