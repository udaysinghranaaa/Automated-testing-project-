package com.shopflow.reporting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public final class ScreenshotManager {
    private ScreenshotManager() {
    }

    public static Optional<ScreenshotEvidence> capture(WebDriver driver, String uniqueName) {
        if (!(driver instanceof TakesScreenshot screenshotDriver)) {
            return Optional.empty();
        }
        try {
            String fileName = ReportArtifactManager.safeFileName(uniqueName) + ".png";
            Path destination = ExtentReportManager.artifacts().screenshotsDirectory().resolve(fileName).normalize();
            File source = screenshotDriver.getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination);
            String relative = ExtentReportManager.artifacts().runDirectory().relativize(destination)
                    .toString().replace('\\', '/');
            return Optional.of(new ScreenshotEvidence(destination, relative));
        } catch (RuntimeException | IOException exception) {
            return Optional.empty();
        }
    }

    public record ScreenshotEvidence(Path absolutePath, String relativePath) {
    }
}
