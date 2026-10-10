package com.shopflow.tests.advanced;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import com.shopflow.pages.advanced.FileUploadPage;

public class FileUploadAdvancedCases extends AdvancedFixtureTest {
    private FileUploadPage upload;
    @BeforeMethod(alwaysRun = true)
    public void openUploadFixture() { openFixture("upload.html"); upload = new FileUploadPage(getDriver()); }
    @Test(groups="advanced", description="TC_ADV_030 upload a temporary local file")
    public void uploadTemporaryFile() throws IOException {
        Path file = Files.createTempFile("shopflow-upload-", ".txt");
        try {
            Files.writeString(file, "Phase 7 upload fixture");
            upload.upload(file);
            Assert.assertEquals(upload.status(), file.getFileName().toString());
        } finally {
            Files.deleteIfExists(file);
        }
    }
}
