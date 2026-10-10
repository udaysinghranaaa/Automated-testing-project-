package com.shopflow.pages.advanced;

import java.nio.file.Path;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import com.shopflow.pages.BasePage;

public final class FileUploadPage extends BasePage {
    private static final By FILE_INPUT = By.id("file-input");
    private static final By STATUS = By.id("upload-status");
    public FileUploadPage(WebDriver driver) { super(driver); }
    public void upload(Path file) { visible(FILE_INPUT).sendKeys(file.toAbsolutePath().toString()); }
    public String status() { return text(STATUS); }
}
