package com.broadcom.gemfiremcpserverdemo.service;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.core.io.FileSystemResource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PdfIngestionServiceTest {

    private final PdfIngestionService pdfIngestionService = new PdfIngestionService();

    @Test
    void testCreatePdfDocumentChunks_splitsText() {
        Document doc = new Document("This is a test document that should be split into smaller parts.");
        List<Document> chunks = pdfIngestionService.createPdfDocumentChunks(List.of(doc));

        assertFalse(chunks.isEmpty());
        assertTrue(chunks.size() >= 1);
        assertTrue(chunks.stream().allMatch(chunk -> chunk.getText() != null && !chunk.getText().isEmpty()));
    }

    @Test
    void testReadPdf_validPdfFile() {
        FileSystemResource resource = new FileSystemResource("src/test/resources/sample-data/sample.pdf");

        List<Document> docs = pdfIngestionService.readPdf(resource);

        assertNotNull(docs);
        assertFalse(docs.isEmpty());
        assertTrue(docs.stream().allMatch(doc -> doc.getText() != null));
    }


    @Test
    void testCreatePdfDocumentChunks_emptyInput() {
        List<Document> chunks = pdfIngestionService.createPdfDocumentChunks(List.of());
        assertTrue(chunks.isEmpty());
    }
}
