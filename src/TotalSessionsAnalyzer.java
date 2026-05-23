import java.util.List;
import java.util.function.Function;

public class TotalSessionsAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult("Общее количество сессий сна за представленный период", sessions.size());
    }
}
