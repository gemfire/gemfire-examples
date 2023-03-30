package io.vmware.controller;


import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.repository.GemfireRepository;

@Region("/item")
public interface Repository extends GemfireRepository<String, String> {
}
