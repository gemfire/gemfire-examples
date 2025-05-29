package com.broadcom.gemfiremcpserverdemo.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PdfIngestionService {

    public List<Document> readPdf(FileSystemResource financialPDF) {

        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader(
                financialPDF,
                PdfDocumentReaderConfig.builder()
                        .withPageTopMargin(0)
                        .withPageExtractedTextFormatter(
                                ExtractedTextFormatter.builder()
                                        .withNumberOfTopTextLinesToDelete(0)
                                        .build())
                        .withPagesPerDocument(1)
                        .build()
        );

        return pdfReader.read();
    }

    public List<Document> createPdfDocumentChunks (List<Document> pdfPages){
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(pdfPages);
    }

}

