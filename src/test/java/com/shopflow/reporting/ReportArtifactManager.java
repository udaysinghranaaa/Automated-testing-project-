package com.shopflow.reporting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public final class ReportArtifactManager {
    private static final DateTimeFormatter RUN_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS", Locale.ROOT);
    private static volatile RunArtifacts current;

    private ReportArtifactManager() {
    }

    public static synchronized RunArtifacts initializeRun() {
        if (current != null) {
            return current;
        }
        Path root = Paths.get(System.getProperty("shopflow.report.root", "target/shopflow-reports"))
                .toAbsolutePath().normalize();
        String runId = RUN_FORMAT.format(LocalDateTime.now()) + "-" + UUID.randomUUID().toString().substring(0, 8);
        Path runDirectory = root.resolve(runId).normalize();
        if (!runDirectory.startsWith(root)) {
            throw new IllegalStateException("Report directory escaped configured report root");
        }
        try {
            Path screenshots = Files.createDirectories(runDirectory.resolve("screenshots"));
            Path diagnostics = Files.createDirectories(runDirectory.resolve("diagnostics"));
            current = new RunArtifacts(runId, runDirectory, runDirectory.resolve("index.html"), screenshots, diagnostics);
            return current;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to create report artifact directories", exception);
        }
    }

    public static RunArtifacts currentRun() {
        return initializeRun();
    }

    public static String safeFileName(String value) {
        String safe = value == null ? "unnamed" : value.replaceAll("[^A-Za-z0-9._-]+", "-");
        safe = safe.replaceAll("-+", "-").replaceAll("^-|-$", "");
        return safe.isBlank() ? "unnamed" : safe.substring(0, Math.min(safe.length(), 120));
    }

    public record RunArtifacts(
            String runId,
            Path runDirectory,
            Path reportFile,
            Path screenshotsDirectory,
            Path diagnosticsDirectory) {
    }
}
