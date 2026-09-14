# Copyright 2026 Broadcom. All Rights Reserved.
#
# Builds both halves of this example: the Go gRPC client, and the Java GemFire client plus the
# listener jar the servers load. GemFire comes from $GEMFIRE_HOME and protobuf-java comes from
# the extension module there, so nothing here is downloaded and no credentials are needed.

.PHONY: all build check go-client java go-proto java-proto run-gemfire-client clean

GEMFIRE_HOME ?=
EXTENSIONS_DIR ?= $(GEMFIRE_HOME)/extensions
PROTOC ?= protoc

PROTO_DIR := ../proto
BUILD := build
EXTENSION_JARS := $(BUILD)/extension-jars
GENERATED_JAVA := $(BUILD)/generated-java
CLASSES := $(BUILD)/classes
JAR_STAGE := $(BUILD)/jar-stage
EXTENSION_STAMP := $(EXTENSION_JARS)/.unpacked
CLASSES_STAMP := $(CLASSES)/.compiled
GEMFIRE_HOME_STAMP := $(BUILD)/.gemfire-home
DESCRIPTOR_SET := $(BUILD)/person.desc
LISTENER_JAR := $(BUILD)/grpc-interop-listener.jar

JAVA_PACKAGE := com/vmware/gemfire/examples/grpc/interop
JAVA_SOURCES := $(wildcard src/main/java/$(JAVA_PACKAGE)/*.java)
MAIN_CLASS := com.vmware.gemfire.examples.grpc.interop.GemFireClientExample
# Quoted wherever it is used, so javac and java expand the * rather than the shell.
CLASSPATH := $(GEMFIRE_HOME)/lib/*:$(EXTENSION_JARS)/*

all: build

build: go-client java

## Verify the two GemFire prerequisites before anything tries to compile.
check:
	@if [ -z "$(GEMFIRE_HOME)" ]; then \
		echo "ERROR: GEMFIRE_HOME is not set."; \
		echo "Export it as the top-level directory of a GemFire 10.3.2 or newer installation."; \
		echo "See the Prerequisites section in README.md."; \
		exit 1; \
	fi
	@ls $(GEMFIRE_HOME)/lib/gemfire-core-*.jar >/dev/null 2>&1 || { \
		echo "ERROR: no gemfire-core jar under $(GEMFIRE_HOME)/lib."; \
		echo "GEMFIRE_HOME must be a GemFire installation directory."; \
		exit 1; \
	}
	@ls $(EXTENSIONS_DIR)/*grpc*.gfm >/dev/null 2>&1 || { \
		echo "ERROR: no *grpc*.gfm found in $(EXTENSIONS_DIR)."; \
		echo "Copy the VMware Tanzu GemFire gRPC Extension module into \$$GEMFIRE_HOME/extensions/,"; \
		echo "or set GEMFIRE_EXTENSIONS_REPOSITORY_PATH to the directory holding it."; \
		exit 1; \
	}
	@if [ ! -f $(PROTO_DIR)/gemfire/v1/gemfire.proto ]; then \
		echo "ERROR: $(PROTO_DIR)/gemfire/v1/gemfire.proto not found."; \
		echo "Extract gemfire.proto from the extension .tgz into $(PROTO_DIR)/gemfire/v1/."; \
		exit 1; \
	fi
	@if [ -f $(GEMFIRE_HOME_STAMP) ] && [ "$$(cat $(GEMFIRE_HOME_STAMP))" != "$(GEMFIRE_HOME)" ]; then \
		echo "ERROR: GEMFIRE_HOME changed since the last build."; \
		echo "  was: $$(cat $(GEMFIRE_HOME_STAMP))"; \
		echo "  now: $(GEMFIRE_HOME)"; \
		echo "Run 'make clean' first. Classes linked against the previous installation cannot"; \
		echo "be reused, and mixing GemFire lines fails at runtime, not at compile time."; \
		exit 1; \
	fi

# --- Go gRPC client ----------------------------------------------------------

go-proto: check
	@if [ ! -f $(PROTO_DIR)/google/api/field_behavior.proto ]; then \
		echo "Downloading google/api/field_behavior.proto..."; \
		mkdir -p $(PROTO_DIR)/google/api; \
		curl -sSL https://raw.githubusercontent.com/googleapis/googleapis/master/google/api/field_behavior.proto -o $(PROTO_DIR)/google/api/field_behavior.proto; \
	fi
	@echo "Generating Go code from proto files..."
	@mkdir -p gemfire/v1 test/v1
	@$(PROTOC) --go_out=gemfire/v1 --go_opt=paths=source_relative \
		--go-grpc_out=gemfire/v1 --go-grpc_opt=paths=source_relative \
		--proto_path=$(PROTO_DIR)/gemfire/v1 \
		--proto_path=$(PROTO_DIR) \
		$(PROTO_DIR)/gemfire/v1/gemfire.proto
	@$(PROTOC) --go_out=test/v1 --go_opt=paths=source_relative \
		--go-grpc_out=test/v1 --go-grpc_opt=paths=source_relative \
		--proto_path=$(PROTO_DIR)/test/v1 \
		$(PROTO_DIR)/test/v1/person.proto

go-client: go-proto
	@echo "Building Go interop client..."
	@go build -o interop-client main.go

# --- Java GemFire client and the deployed listener ---------------------------

## Unpacks the jars this example compiles against. gemfire-proto-serialization supplies
## ProtoAnyDocument and must be on the runtime classpath of any GemFire client reading
## gRPC-written values: it registers the PROTOBUF DSCODE decoder. protobuf-java comes from here
## too, so the client uses the same version the server does. The installation ships both only
## inside the .gfm.
##
## "check" is an order-only prerequisite here and below: it must run, but being phony it should
## not make the real targets look perpetually out of date.
$(EXTENSION_STAMP): | check
	@echo "Unpacking extension jars from the .gfm..."
	@mkdir -p $(EXTENSION_JARS)
	@cd $(EXTENSION_JARS) && unzip -joq "$$(ls $(EXTENSIONS_DIR)/*grpc*.gfm | head -1)" \
		"*/gemfire-proto-serialization-*.jar" "*/protobuf-java-*.jar"
	@rm -f $(EXTENSION_JARS)/protobuf-java-util-*.jar
	@touch $@

## --include_imports makes the descriptor set self-contained, so the server resolves imported
## well-known types without their .proto files. Deploying it registers test.v1.Person in the
## server's proto type registry, which is what makes Document.getField() and OQL work.
$(DESCRIPTOR_SET): $(PROTO_DIR)/test/v1/person.proto | check
	@echo "Generating Java code and the descriptor set from person.proto..."
	@mkdir -p $(GENERATED_JAVA) $(BUILD)
	@$(PROTOC) --java_out=$(GENERATED_JAVA) \
		--descriptor_set_out=$(DESCRIPTOR_SET) --include_imports \
		--proto_path=$(PROTO_DIR)/test/v1 \
		$(PROTO_DIR)/test/v1/person.proto

java-proto: $(DESCRIPTOR_SET)

$(CLASSES_STAMP): $(JAVA_SOURCES) $(DESCRIPTOR_SET) $(EXTENSION_STAMP)
	@echo "Compiling the Java sources..."
	@mkdir -p $(CLASSES)
	@javac -nowarn -d $(CLASSES) -cp "$(CLASSPATH)" \
		$(JAVA_SOURCES) $$(find $(GENERATED_JAVA) -name '*.java')
	@echo "$(GEMFIRE_HOME)" > $(GEMFIRE_HOME_STAMP)
	@touch $@

## The jar handed to "gfsh deploy" carries the listener and the descriptor set, and nothing else.
## Generated message classes are deliberately left out: GemFire loads a deployed jar in its own
## isolated module that cannot see com.google.protobuf, so they would fail to link and the deploy
## itself would fail. A descriptor set needs no Protobuf classes at all.
$(LISTENER_JAR): $(CLASSES_STAMP)
	@echo "Packaging the listener jar..."
	@rm -rf $(JAR_STAGE)
	@mkdir -p $(JAR_STAGE)/META-INF/gemfire/proto $(JAR_STAGE)/$(JAVA_PACKAGE)
	@cp $(DESCRIPTOR_SET) $(JAR_STAGE)/META-INF/gemfire/proto/
	@cp $(CLASSES)/$(JAVA_PACKAGE)/PersonCacheListener*.class $(JAR_STAGE)/$(JAVA_PACKAGE)/
	@cd $(JAR_STAGE) && jar cf $(CURDIR)/$(LISTENER_JAR) .

java: $(LISTENER_JAR)

run-gemfire-client: $(CLASSES_STAMP)
	@java -cp "$(CLASSES):$(CLASSPATH)" $(MAIN_CLASS)

clean:
	@echo "Cleaning..."
	@rm -rf $(BUILD) gemfire test interop-client locator server1
	@go clean
