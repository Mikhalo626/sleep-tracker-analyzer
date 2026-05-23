import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=== ЗАПУСК ЮНИТ-ТЕСТОВ ===");

        try {
            testTotalSessionsAnalyzer();
            testMinMaxAvgDurationAnalyzer();
            testBadQualitySessionsAnalyzer();
            testSleeplessNightsAllNightSlept();
            testSleeplessNightsOneSleepless();
            testUserClassificationOwl();
            testUserClassificationDoveOnTie();

            System.out.println("\n🎉 ВСЕ ТЕСТЫ УСПЕШНО ПРОЙДЕНЫ!");
        } catch (AssertionError e) {
            System.err.println("\n❌ ТЕСТ ПРОВАЛЕН: " + e.getMessage());
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + " (Ожидалось: " + expected + ", Получено: " + actual + ")");
        }
    }

    private static void testTotalSessionsAnalyzer() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0), LocalDateTime.of(2025, 10, 3, 8, 0), SleepQuality.NORMAL)
        );
        TotalSessionsAnalyzer analyzer = new TotalSessionsAnalyzer();
        SleepAnalysisResult result = analyzer.apply(sessions);
        assertEquals(2, result.getValue(), "Ошибка в TotalSessionsAnalyzer");
        System.out.println("✓ testTotalSessionsAnalyzer — пройден");
    }

    private static void testMinMaxAvgDurationAnalyzer() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 0), LocalDateTime.of(2025, 10, 1, 23, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 22, 0), LocalDateTime.of(2025, 10, 3, 0, 0), SleepQuality.NORMAL)
        );
        assertEquals(60L, new MinDurationAnalyzer().apply(sessions).getValue(), "Ошибка в MinDurationAnalyzer");
        assertEquals(120L, new MaxDurationAnalyzer().apply(sessions).getValue(), "Ошибка в MaxDurationAnalyzer");
        assertEquals(90L, new AvgDurationAnalyzer().apply(sessions).getValue(), "Ошибка в AvgDurationAnalyzer");
        System.out.println("✓ testMinMaxAvgDurationAnalyzer — пройден");
    }

    private static void testBadQualitySessionsAnalyzer() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 22, 15), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.BAD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 23, 0), LocalDateTime.of(2025, 10, 3, 8, 0), SleepQuality.NORMAL)
        );
        assertEquals(1L, new BadQualitySessionsAnalyzer().apply(sessions).getValue(), "Ошибка в BadQualitySessionsAnalyzer");
        System.out.println("✓ testBadQualitySessionsAnalyzer — пройден");
    }

    private static void testSleeplessNightsAllNightSlept() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 0), LocalDateTime.of(2025, 10, 2, 8, 0), SleepQuality.GOOD)
        );
        assertEquals(0L, new SleeplessNightsAnalyzer().apply(sessions).getValue(), "Ошибка в SleeplessNightsAnalyzer (все спали)");
        System.out.println("✓ testSleeplessNightsAllNightSlept — пройден");
    }

    private static void testSleeplessNightsOneSleepless() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 0), LocalDateTime.of(2025, 10, 2, 5, 0), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 3, 23, 0), LocalDateTime.of(2025, 10, 4, 5, 0), SleepQuality.GOOD)
        );
        assertEquals(1L, new SleeplessNightsAnalyzer().apply(sessions).getValue(), "Ошибка в SleeplessNightsAnalyzer (одна бессонная)");
        System.out.println("✓ testSleeplessNightsOneSleepless — пройден");
    }

    private static void testUserClassificationOwl() {
        List<SleepingSession> sessions = Collections.singletonList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30), LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD)
        );
        assertEquals(Chronotype.СОВА, new UserClassificationAnalyzer().apply(sessions).getValue(), "Ошибка классификации совы");
        System.out.println("✓ testUserClassificationOwl — пройден");
    }

    private static void testUserClassificationDoveOnTie() {
        List<SleepingSession> sessions = Arrays.asList(
                new SleepingSession(LocalDateTime.of(2025, 10, 1, 23, 30), LocalDateTime.of(2025, 10, 2, 9, 30), SleepQuality.GOOD),
                new SleepingSession(LocalDateTime.of(2025, 10, 2, 21, 0), LocalDateTime.of(2025, 10, 3, 6, 0), SleepQuality.GOOD)
        );
        assertEquals(Chronotype.ГОЛУБЬ, new UserClassificationAnalyzer().apply(sessions).getValue(), "Ошибка классификации при равенстве (должен быть голубь)");
        System.out.println("✓ testUserClassificationDoveOnTie — пройден");
    }
}
