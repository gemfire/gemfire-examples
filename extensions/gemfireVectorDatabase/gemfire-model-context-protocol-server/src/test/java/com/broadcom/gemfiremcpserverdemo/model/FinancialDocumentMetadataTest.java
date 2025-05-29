package com.broadcom.gemfiremcpserverdemo.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class FinancialDocumentMetadataTest {

    @Test
    void testAllArgsConstructor() {
        Instant now = Instant.now();

        FinancialDocumentMetadata metadata = new FinancialDocumentMetadata(
                "report.pdf",
                12,
                now,
                false
        );

        assertEquals("report.pdf", metadata.getFilename());
        assertEquals(12, metadata.getChunkCount());
        assertEquals(now, metadata.getUploadedAt());
        assertFalse(metadata.isEmbedded());
    }


}
