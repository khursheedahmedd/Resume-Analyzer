module com.resumeanalyzer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires org.apache.pdfbox;
    requires org.apache.poi.ooxml;
    requires com.fasterxml.jackson.databind;
    requires okhttp3;
    requires org.commonmark;

    exports com.resumeanalyzer;
    exports com.resumeanalyzer.config;
} 