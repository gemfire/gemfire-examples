package com.broadcom.gemfiremcpserverdemo;

import com.broadcom.gemfiremcpserverdemo.model.FinancialDocumentMetadata;
import com.broadcom.gemfiremcpserverdemo.service.FinancialDocumentSearchTool;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.gemfire.GemFireVectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@SpringBootTest(properties = "spring.main.allow-bean-definition-overriding=true")
class GemfireMcpServerDemoApplicationTests {

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ClientCache clientCache() {
            return mock(ClientCache.class);
        }

        @Bean
        public Region<String, FinancialDocumentMetadata> financialDocumentMetadataRegion() {
            return mock(Region.class);
        }

        @Bean
        public GemFireVectorStore gemFireVectorStore() {
            return mock(GemFireVectorStore.class);
        }

        @Bean
        public FinancialDocumentSearchTool financialDocumentSearchTool() {
            return mock(FinancialDocumentSearchTool.class);
        }
    }

    @Test
    void contextLoads() {

    }
}



