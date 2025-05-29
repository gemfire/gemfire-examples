package com.broadcom.gemfiremcpserverdemo.service;

import com.broadcom.gemfiremcpserverdemo.model.FinancialDocumentMetadata;
import org.apache.geode.cache.Region;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.gemfire.GemFireVectorStore;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinancialDocumentSearchToolTest {

    @Mock
    private GemFireVectorStore gemFireVectorStore;

    @Mock
    private PdfIngestionService pdfIngestionService;

    @Mock
    private Region<String, FinancialDocumentMetadata> metadataRegion;

    private FinancialDocumentSearchTool searchTool;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        searchTool = new FinancialDocumentSearchTool(gemFireVectorStore, pdfIngestionService, metadataRegion);

        // Inject the temporary path into the private `docsPath` field via reflection
        var docsPathField = FinancialDocumentSearchTool.class.getDeclaredField("docsPath");
        docsPathField.setAccessible(true);
        docsPathField.set(searchTool, tempDir.toAbsolutePath().toString());
    }

    @Test
    void testSearchDocsReturnsConcatenatedResults() {
        List<Document> mockResults = List.of(
                new Document("First result"),
                new Document("Second result")
        );

        when(gemFireVectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(mockResults);

        String result = searchTool.searchDocs("earnings report");

        assertEquals("First result\nSecond result", result);
    }

    @Test
    void testAddFinancialDoc_nullFilename() {
        String result = searchTool.addFinancialDoc(null);
        assertTrue(result.contains("Filename is null or empty."));
    }

    @Test
    void testAddFinancialDoc_nonExistentFile() {
        String result = searchTool.addFinancialDoc("does_not_exist");
        assertTrue(result.contains("File not found"));
    }

    @Test
    void testAddFinancialDoc_invalidPdfSignature() throws Exception {
        File file = tempDir.resolve("bad.pdf").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("NOTPDF1234".getBytes());
        }

        String result = searchTool.addFinancialDoc("bad");
        assertTrue(result.contains("File is not a valid PDF"));
    }

    @Test
    void testAddFinancialDoc_tooLargeFile() throws Exception {
        File file = tempDir.resolve("large.pdf").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("%PDF".getBytes());
            fos.write(new byte[11 * 1024 * 1024]); // 11 MB
        }

        String result = searchTool.addFinancialDoc("large");
        assertTrue(result.contains("File too large"));
    }

    @Test
    void testAddFinancialDoc_successfulIngestion() throws Exception {
        File file = tempDir.resolve("good.pdf").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("%PDF".getBytes());
            fos.write("Some content".getBytes());
        }

        List<Document> pages = List.of(new Document("Page 1"));
        List<Document> chunks = List.of(new Document("Chunk 1"));

        when(pdfIngestionService.readPdf(any())).thenReturn(pages);
        when(pdfIngestionService.createPdfDocumentChunks(pages)).thenReturn(chunks);
        when(metadataRegion.containsKey("good.pdf")).thenReturn(false);

        String result = searchTool.addFinancialDoc("good");
        assertEquals("Document ingested successfully.", result);

        verify(gemFireVectorStore).add(chunks);
        verify(metadataRegion).put(eq("good.pdf"), any(FinancialDocumentMetadata.class));
    }

    @Test
    void testAddFinancialDoc_emptyChunks() throws Exception {
        File file = tempDir.resolve("empty.pdf").toFile();
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write("%PDF".getBytes());
            fos.write("Empty".getBytes());
        }

        List<Document> pages = List.of(new Document("Page 1"));

        when(pdfIngestionService.readPdf(any())).thenReturn(pages);
        when(pdfIngestionService.createPdfDocumentChunks(pages)).thenReturn(Collections.emptyList());
        when(metadataRegion.containsKey("empty.pdf")).thenReturn(false);

        String result = searchTool.addFinancialDoc("empty");
        assertTrue(result.contains("No extractable text found"));
    }

    @Test
    void testAddFinancialDoc_alreadyExists() {
        when(metadataRegion.containsKey("existing.pdf")).thenReturn(true);

        String result = searchTool.addFinancialDoc("existing.pdf");
        assertEquals("Document already exists: existing.pdf", result);
    }

    @Test
    void testListAvailableFinancialDocs_returnsSortedMetadata() {
        FinancialDocumentMetadata doc1 = new FinancialDocumentMetadata(
                "file1.pdf", 3, Instant.parse("2024-01-01T10:00:00Z"), true);
        FinancialDocumentMetadata doc2 = new FinancialDocumentMetadata(
                "file2.pdf", 5, Instant.parse("2025-01-01T10:00:00Z"), true);

        when(metadataRegion.keySetOnServer()).thenReturn(Set.of("file1.pdf", "file2.pdf"));
        when(metadataRegion.getAll(Set.of("file1.pdf", "file2.pdf")))
                .thenReturn(Map.of("file1.pdf", doc1, "file2.pdf", doc2));

        List<String> result = searchTool.listAvailableFinancialDocs();

        assertEquals(2, result.size());
        assertTrue(result.get(0).contains("file2.pdf")); // Newest first
        assertTrue(result.get(1).contains("file1.pdf"));


    }

}
