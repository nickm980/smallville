package io.github.nickm980.smallville.ueia.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CanonicalFixturesTest {

    @Test
    public void all_returns_four_fixtures_in_canonical_order() {
	List<CanonicalFixtures.FixtureRef> fixtures = CanonicalFixtures.all();

	assertEquals(4, fixtures.size());
	assertEquals(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, fixtures.get(0).role());
	assertEquals("docs/examples/promoted-archive-record-v1.md", fixtures.get(0).relativePath());
	assertEquals(CanonicalFixtures.FixtureRole.DERIVED_PACKET, fixtures.get(1).role());
	assertEquals("docs/examples/promoted-derived-packet-v1.md", fixtures.get(1).relativePath());
	assertEquals(CanonicalFixtures.FixtureRole.AGENT_BRIEF, fixtures.get(2).role());
	assertEquals("docs/examples/promoted-agent-brief-v1.md", fixtures.get(2).relativePath());
	assertEquals(CanonicalFixtures.FixtureRole.REVIEW_DECISION, fixtures.get(3).role());
	assertEquals("docs/examples/promoted-review-decision-v1.md", fixtures.get(3).relativePath());
    }

    @Test
    public void each_role_resolves_expected_relative_path_and_existing_file() {
	assertFixture(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, "docs/examples/promoted-archive-record-v1.md");
	assertFixture(CanonicalFixtures.FixtureRole.DERIVED_PACKET, "docs/examples/promoted-derived-packet-v1.md");
	assertFixture(CanonicalFixtures.FixtureRole.AGENT_BRIEF, "docs/examples/promoted-agent-brief-v1.md");
	assertFixture(CanonicalFixtures.FixtureRole.REVIEW_DECISION, "docs/examples/promoted-review-decision-v1.md");
    }

    @Test
    public void resolved_paths_remain_under_docs_examples_and_do_not_create_outputs() throws IOException {
	Path docsExamples = CanonicalFixtures.require(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD).resolvedPath().getParent();
	List<String> before = directoryEntries(docsExamples);

	List<CanonicalFixtures.FixtureRef> fixtures = CanonicalFixtures.all();
	List<String> after = directoryEntries(docsExamples);

	assertEquals(before, after);
	assertEquals(4, fixtures.size());
	assertTrue(fixtures.stream().allMatch(fixture -> fixture.relativePath().startsWith("docs/examples/")));
	assertTrue(fixtures.stream().allMatch(fixture -> fixture.resolvedPath().startsWith(docsExamples)));
    }

    private void assertFixture(CanonicalFixtures.FixtureRole role, String expectedRelativePath) {
	CanonicalFixtures.FixtureRef fixture = CanonicalFixtures.require(role);

	assertEquals(expectedRelativePath, fixture.relativePath());
	assertTrue(Files.exists(fixture.resolvedPath()));
	assertTrue(Files.isRegularFile(fixture.resolvedPath()));
    }

    private List<String> directoryEntries(Path directory) throws IOException {
	try (var entries = Files.list(directory)) {
	    return entries
		.map(path -> path.getFileName().toString())
		.sorted()
		.collect(Collectors.toList());
	}
    }
}
