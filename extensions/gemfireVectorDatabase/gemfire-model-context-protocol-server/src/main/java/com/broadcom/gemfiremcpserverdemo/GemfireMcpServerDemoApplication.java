package com.broadcom.gemfiremcpserverdemo;

import com.broadcom.gemfiremcpserverdemo.model.FinancialDocumentMetadata;
import com.broadcom.gemfiremcpserverdemo.service.FinancialDocumentSearchTool;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication

public class GemfireMcpServerDemoApplication {

    @Value("${gemfire.region.docsMetadata}")
    private String financialDocumentsMetadataRegion;

    public static void main(String[] args) {
        SpringApplication.run(GemfireMcpServerDemoApplication.class, args);
    }

    @Bean
    public List<ToolCallback> gemfireMCPTools (FinancialDocumentSearchTool financialDocumentSearchTool) {
        return List.of(ToolCallbacks.from(financialDocumentSearchTool));
    }

    @Bean
    public ClientCache clientCache() {
        return new ClientCacheFactory()
                .setPdxReadSerialized(false)
                .addPoolLocator("localhost", 10334)
                .create();
    }

    @Bean
    public Region<String, FinancialDocumentMetadata> financialDocumentMetadataRegion(ClientCache clientCache) {
        return clientCache
                .<String, FinancialDocumentMetadata>createClientRegionFactory(ClientRegionShortcut.PROXY)
                .create(financialDocumentsMetadataRegion);
    }

    @Bean
    public List<McpServerFeatures.SyncPromptSpecification> myPrompts() {
        var prompt = new McpSchema.Prompt("greeting", "A friendly greeting prompt",
                List.of(new McpSchema.PromptArgument("name", "The name to greet", true)));

        var promptSpecification = new McpServerFeatures.SyncPromptSpecification(prompt, (exchange, getPromptRequest) -> {
            String nameArgument = (String) getPromptRequest.arguments().get("name");
            if (nameArgument == null) { nameArgument = "friend"; }
            var userMessage = new McpSchema.PromptMessage(McpSchema.Role.USER, new McpSchema.TextContent("Hello " + nameArgument + "! How can I assist you today?"));
            return new McpSchema.GetPromptResult("A personalized greeting message", List.of(userMessage));
        });

        return List.of(promptSpecification);
    }


}
