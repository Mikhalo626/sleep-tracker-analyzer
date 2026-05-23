import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SleepTrackerApp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: не передан путь к файлу лога сна.");
            return;
        }

        String filePath = args[0];
        List<SleepingSession> sessions;

        try (Stream<String> lines = Files.lines(Paths.get(filePath))) {
            sessions = lines
                    .filter(line -> !line.trim().isEmpty())
                    .map(SleepTrackerApp::parseSession)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
            return;
        }

        List<Function<List<SleepingSession>, SleepAnalysisResult>> analyzers = Arrays.asList(
                new TotalSessionsAnalyzer(),
                new MinDurationAnalyzer(),
                new MaxDurationAnalyzer(),
                new AvgDurationAnalyzer(),
                new BadQualitySessionsAnalyzer(),
                new SleeplessNightsAnalyzer(),
                new UserClassificationAnalyzer()
        );

        analyzers.stream()
                .map(analyzer -> analyzer.apply(sessions))
                .forEach(result -> System.out.println(result.getDescription() + ": " + result.getValue()));
    }

    private static SleepingSession parseSession(String line) {
        String[] parts = line.split(";");
        LocalDateTime startTime = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime endTime = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim().toUpperCase());
        return new SleepingSession(startTime, endTime, quality);
    }
}
