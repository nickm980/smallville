package io.github.nickm980.smallville;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Governance assertion tests for UEIA Seam 23 contract documents.
 * Verifies that the three governance documents exist and contain
 * their required structural sections.
 */
public class FixtureGovernanceDocumentationTest {

    private static final Path CONTRACTS_DIR = Paths.get("..", "docs", "contracts");

    // --- Document existence ---

    @Test
    public void test_governance_gap_mapping_document_exists() {
	Path doc = CONTRACTS_DIR.resolve("governance-gap-mapping-unresolved-cells-v1.md");
	assertTrue(Files.exists(doc), "governance-gap-mapping-unresolved-cells-v1.md must exist");
    }

    @Test
    public void test_conformance_validator_result_semantics_document_exists() {
	Path doc = CONTRACTS_DIR.resolve("conformance-validator-result-semantics-v1.md");
	assertTrue(Files.exists(doc), "conformance-validator-result-semantics-v1.md must exist");
    }

    @Test
    public void test_conformance_matrix_traceability_document_exists() {
	Path doc = CONTRACTS_DIR.resolve("conformance-matrix-traceability-v1.md");
	assertTrue(Files.exists(doc), "conformance-matrix-traceability-v1.md must exist");
    }

    // --- Required sections: governance-gap-mapping ---

    @Test
    public void test_gap_mapping_contains_unresolved_cells_table() throws IOException {
	String content = readDoc("governance-gap-mapping-unresolved-cells-v1.md");
	assertTrue(content.contains("Unresolved Cell"), "Gap mapping must contain Unresolved Cell reference");
	assertTrue(content.contains("Gap reason"), "Gap mapping must contain Gap reason entries");
    }

    @Test
    public void test_gap_mapping_covers_required_cell_count() throws IOException {
	String content = readDoc("governance-gap-mapping-unresolved-cells-v1.md");
	// Must reference at least 8 unresolved cells (U1-U8)
	int cellCount = 0;
	for (int i = 1; i <= 8; i++) {
	    if (content.contains("U" + i)) {
		cellCount++;
	    }
	}
	assertTrue(cellCount >= 8, "Gap mapping must reference all 8 unresolved cells (U1-U8). Found: " + cellCount);
    }

    // --- Required sections: conformance-validator-result-semantics ---

    @Test
    public void test_result_semantics_documents_optional_empty() throws IOException {
	String content = readDoc("conformance-validator-result-semantics-v1.md");
	assertTrue(content.contains("Optional.empty()") || content.contains("Optional.empty"),
	    "Result semantics must document Optional.empty() return value");
    }

    @Test
    public void test_result_semantics_documents_three_classifications() throws IOException {
	String content = readDoc("conformance-validator-result-semantics-v1.md");
	// Must document the three classifications: ALLOWED, NOT_ALLOWED, ALLOWED_WITH_STOP_RULES
	assertTrue(content.contains("ALLOWED"),
	    "Result semantics must document ALLOWED classification");
	assertTrue(content.contains("NOT_ALLOWED"),
	    "Result semantics must document NOT_ALLOWED classification");
	assertTrue(content.contains("ALLOWED_WITH_STOP_RULES"),
	    "Result semantics must document ALLOWED_WITH_STOP_RULES classification");
    }

    // --- Required sections: conformance-matrix-traceability ---

    @Test
    public void test_traceability_covers_resolved_rules() throws IOException {
	String content = readDoc("conformance-matrix-traceability-v1.md");
	// Must reference resolved rules R1-R8
	int ruleCount = 0;
	for (int i = 1; i <= 8; i++) {
	    if (content.contains("R" + i)) {
		ruleCount++;
	    }
	}
	assertTrue(ruleCount >= 8, "Traceability must reference all 8 resolved rules (R1-R8). Found: " + ruleCount);
    }

    @Test
    public void test_traceability_covers_unresolved_rules() throws IOException {
	String content = readDoc("conformance-matrix-traceability-v1.md");
	// Must reference unresolved rules U1-U8
	int cellCount = 0;
	for (int i = 1; i <= 8; i++) {
	    if (content.contains("U" + i)) {
		cellCount++;
	    }
	}
	assertTrue(cellCount >= 8, "Traceability must reference all 8 unresolved cells (U1-U8). Found: " + cellCount);
    }

    @Test
    public void test_traceability_references_source_tests() throws IOException {
	String content = readDoc("conformance-matrix-traceability-v1.md");
	assertTrue(content.contains("Test") || content.contains("test"),
	    "Traceability must reference test sources");
    }

    // --- Ladder map ---

    @Test
    public void test_ladder_map_contains_seam_23() throws IOException {
	String content = Files.readString(CONTRACTS_DIR.resolve("ueia-ladder-phase-map-v1.md"));
	assertTrue(content.contains("23"), "Ladder map must contain Seam 23");
	assertTrue(content.contains("UEIA Conformance Documentation"),
	    "Ladder map must reference UEIA Conformance Documentation seam");
    }

    // --- Helper ---

    private String readDoc(String filename) throws IOException {
	return Files.readString(CONTRACTS_DIR.resolve(filename));
    }
}
