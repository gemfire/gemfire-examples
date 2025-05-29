package com.broadcom.gemfiremcpserverdemo.service;


import com.broadcom.gemfiremcpserverdemo.model.FinancialDocumentMetadata;
import org.apache.commons.io.FilenameUtils;
import org.apache.geode.cache.Region;
import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.gemfire.GemFireVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FinancialDocumentSearchTool {

    @Value("${docs.path}")
    private String docsPath;

    private final GemFireVectorStore gemFireVectorStore;
    private final PdfIngestionService pdfIngestionService;
    private final Region<String, FinancialDocumentMetadata> financialDocumentMetadataRegion;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());



    public FinancialDocumentSearchTool(GemFireVectorStore gemFireVectorStore, PdfIngestionService pdfIngestionService, Region<String, FinancialDocumentMetadata> financialDocumentMetadataRegion) {
        this.gemFireVectorStore = gemFireVectorStore;
        this.pdfIngestionService = pdfIngestionService;
        this.financialDocumentMetadataRegion = financialDocumentMetadataRegion;
    }

    @Tool(name="search_financial_docs", description = "Search embedded financial documents using vector similarity in GemFire.")
    public String searchDocs(String query) {

        List<Document> results = gemFireVectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(query)
                        .topK(3)
                        .build()
        );

        return results.stream()
                .map(document -> document.getText())
                .collect(Collectors.joining("\n"));


    }

    @Tool(name = "add_financial_doc", description = "Add a financial PDF document to the GemFire vector store.")
    public String addFinancialDoc(String filename) {
        List<String> failures = new ArrayList<>();

        if (filename == null || filename.trim().isEmpty()) {
            failures.add("Filename is null or empty.");
            return "Error: " + String.join(", ", failures);
        }

        filename = FilenameUtils.getName(filename);


        if (!filename.toLowerCase().endsWith(".pdf")) {
            filename += ".pdf";
        }

        if (financialDocumentMetadataRegion.containsKey(filename)) {
            return "Document already exists: " + filename;
        }

        File file = Path.of(docsPath, filename).toFile();


        if (!file.exists()) {
            failures.add("File not found: " + file.getPath());
        } else {
            try {
                byte[] fileBytes = Files.readAllBytes(file.toPath());
                if (!isValidPdf(fileBytes)) {
                    failures.add("File is not a valid PDF: " + filename);
                }
            } catch (IOException e) {
                failures.add("Error reading file: " + filename + " (error: " + e.getMessage() + ")");
            }

            long fileSize = file.length();
            if (fileSize > 10 * 1024 * 1024) {
                failures.add("File too large: " + filename);
            }

            if (failures.isEmpty()) {
                try {
                    FileSystemResource resource = new FileSystemResource(file.getAbsolutePath());
                    List<Document> pages = pdfIngestionService.readPdf(resource);
                    List<Document> chunks = pdfIngestionService.createPdfDocumentChunks(pages);

                    if (chunks.isEmpty()) {
                        return "No extractable text found in: " + filename;
                    }

                    for (int i = 0; i < chunks.size(); i++) {
                        Document chunk = chunks.get(i);
                        chunk.getMetadata().put("filename", filename);
                        chunk.getMetadata().put("chunk_index", i);
                    }

                    gemFireVectorStore.add(chunks);

                    FinancialDocumentMetadata metadata = new FinancialDocumentMetadata(
                            filename,
                            chunks.size(),
                            Instant.now(),
                            true
                    );

                    financialDocumentMetadataRegion.put(filename, metadata);
                    return "Document ingested successfully.";
                } catch (Exception e) {
                    return "Failed to ingest document: " + e.getMessage();
                }
            }
        }

        return "Some documents failed to ingest: " + String.join(", ", failures);

    }


    @Tool(name = "list_available_financial_docs", description = "Show a list of financial documents available.")
    public List<String> listAvailableFinancialDocs() {

        Set<String> keys = financialDocumentMetadataRegion.keySetOnServer();

        return financialDocumentMetadataRegion.getAll(keys).values().stream()
                    .sorted(Comparator.comparing(FinancialDocumentMetadata::getUploadedAt).reversed())
                    .map(meta -> meta.getFilename() +  ", uploaded: " + formatter.format(meta.getUploadedAt()) + ")")
                    .collect(Collectors.toList());
    }


    private boolean isValidPdf(byte[] fileBytes) {
        String fileSignature = new String(fileBytes, 0, 4);
        return "%PDF".equalsIgnoreCase(fileSignature);
    }


}

