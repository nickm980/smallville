package io.github.nickm980.smallville.ueia.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CanonicalFixtureHeadingIndexTest {

    @Test
    public void index_preserves_role_relative_path_and_resolved_path_from_loader() {
	assertIndexedFixture(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD);
	assertIndexedFixture(CanonicalFixtures.FixtureRole.DERIVED_PACKET);
	assertIndexedFixture(CanonicalFixtures.FixtureRole.AGENT_BRIEF);
	assertIndexedFixture(CanonicalFixtures.FixtureRole.REVIEW_DECISION);
    }

    @Test
    public void index_all_returns_four_fixtures_in_canonical_order_with_expected_heading_shape() {
	List<CanonicalFixtureHeadingIndex.IndexedFixtureHeadings> fixtures = CanonicalFixtureHeadingIndex.indexAll();

	assertEquals(4, fixtures.size());
	assertEquals(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, fixtures.get(0).role());
	assertEquals(CanonicalFixtures.FixtureRole.DERIVED_PACKET, fixtures.get(1).role());
	assertEquals(CanonicalFixtures.FixtureRole.AGENT_BRIEF, fixtures.get(2).role());
	assertEquals(CanonicalFixtures.FixtureRole.REVIEW_DECISION, fixtures.get(3).role());

	for (CanonicalFixtureHeadingIndex.IndexedFixtureHeadings fixture : fixtures) {
	    assertEquals(6, fixture.headings().size());
	    assertEquals(1, fixture.headings().get(0).lineNumber());
	    assertEquals(1, fixture.headings().get(0).level());
	    assertEquals("#", fixture.headings().get(0).marker());

	    for (int i = 1; i < fixture.headings().size(); i++) {
		assertEquals(2, fixture.headings().get(i).level());
		assertEquals("##", fixture.headings().get(i).marker());
	    }
	}
    }

    @Test
    public void repeated_indexing_does_not_create_outputs_or_mutate_docs_examples() throws IOException {
	Path docsExamples = CanonicalFixtures.require(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD).resolvedPath().getParent();
	List<String> before = directoryEntries(docsExamples);

	List<CanonicalFixtureHeadingIndex.IndexedFixtureHeadings> first = CanonicalFixtureHeadingIndex.indexAll();
	List<CanonicalFixtureHeadingIndex.IndexedFixtureHeadings> second = CanonicalFixtureHeadingIndex.indexAll();
	List<String> after = directoryEntries(docsExamples);

	assertEquals(before, after);
	assertEquals(4, first.size());
	assertEquals(4, second.size());
	assertTrue(first.stream().allMatch(fixture -> fixture.relativePath().startsWith("docs/examples/")));
	assertTrue(first.stream().allMatch(fixture -> fixture.resolvedPath().startsWith(docsExamples)));
	assertEquals(first, second);
    }

    private void assertIndexedFixture(CanonicalFixtures.FixtureRole role) {
	CanonicalFixtureTextLoader.LoadedFixtureText loaded = CanonicalFixtureTextLoader.load(role);
	CanonicalFixtureHeadingIndex.IndexedFixtureHeadings indexed = CanonicalFixtureHeadingIndex.index(role);

	assertEquals(loaded.role(), indexed.role());
	assertEquals(loaded.relativePath(), indexed.relativePath());
	assertEquals(loaded.resolvedPath(), indexed.resolvedPath());
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
