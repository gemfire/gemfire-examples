package com.broadcom.gemfiremcpserverdemo.model;

import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializable;
import org.apache.geode.pdx.PdxWriter;

import java.time.Instant;

public class FinancialDocumentMetadata implements PdxSerializable {
    private String filename;
    private int chunkCount;
    private Instant uploadedAt;
    private boolean embedded;


    public FinancialDocumentMetadata() {}
    public FinancialDocumentMetadata(String filename, int chunkCount, Instant uploadedAt, boolean embedded) {
        this.filename = filename;
        this.chunkCount = chunkCount;
        this.uploadedAt = uploadedAt;
        this.embedded = embedded;
    }

    @Override
    public void fromData(PdxReader reader) {
        this.filename = reader.readString("filename");
        this.chunkCount = reader.readInt("chunkCount");
        this.uploadedAt = Instant.ofEpochMilli(reader.readLong("uploadedAt"));
        this.embedded = reader.readBoolean("embedded");
    }

    @Override
    public void toData(PdxWriter writer) {
        writer.writeString("filename", this.filename);
        writer.writeInt("chunkCount", this.chunkCount);
        writer.writeLong("uploadedAt", this.uploadedAt.toEpochMilli());
        writer.writeBoolean("embedded", this.embedded);
    }


    public int getChunkCount() {
        return chunkCount;
    }

    public String getFilename() {
        return filename;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }


    public boolean isEmbedded() {
        return embedded;
    }

    public void setChunkCount(int chunkCount) {
        this.chunkCount = chunkCount;
    }

    public void setEmbedded(boolean embedded) {
        this.embedded = embedded;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
