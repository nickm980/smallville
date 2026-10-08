package io.github.nickm980.smallville.ueia.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CanonicalFixtureSectionSpanIndexTest {

    @Test
    public void index_preserves_role_relative_path_and_resolved_path() {
	assertIndexedFixture(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, 51, List.of("1-2", "3-7", "8-17", "18-26", "27-40", "41-51"));
	assertIndexedFixture(CanonicalFixtures.FixtureRole.DERIVED_PACKET, 47, List.of("1-2", "3-7", "8-18", "19-27", "28-36", "37-47"));
	assertIndexedFixture(CanonicalFixtures.FixtureRole.AGENT_BRIEF, 45, List.of("1-2", "3-7", "8-16", "17-25", "26-34", "35-45"));
	assertIndexedFixture(CanonicalFixtures.FixtureRole.REVIEW_DECISION, 48, List.of("1-2", "3-7", "8-16", "17-25", "26-37", "38-48"));
    }

    @Test
    public void index_all_returns_four_fixtures_in_canonical_order() {
	List<CanonicalFixtureSectionSpanIndex.IndexedFixtureSections> fixtures = CanonicalFixtureSectionSpanIndex.indexAll();

	assertEquals(4, fixtures.size());
	assertEquals(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, fixtures.get(0).role());
	assertEquals(CanonicalFixtures.FixtureRole.DERIVED_PACKET, fixtures.get(1).role());
	assertEquals(CanonicalFixtures.FixtureRole.AGENT_BRIEF, fixtures.get(2).role());
	assertEquals(CanonicalFixtures.FixtureRole.REVIEW_DECISION, fixtures.get(3).role());
    }

    @Test
    public void repeated_indexing_does_not_create_outputs_or_mutate_docs_examples() throws IOException {
	Path docsExamples = CanonicalFixtures.require(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD).resolvedPath().getParent();
	List<String> before = directoryEntries(docsExamples);

	List<CanonicalFixtureSectionSpanIndex.IndexedFixtureSections> first = CanonicalFixtureSectionSpanIndex.indexAll();
	List<CanonicalFixtureSectionSpanIndex.IndexedFixtureSections> second = CanonicalFixtureSectionSpanIndex.indexAll();
	List<String> after = directoryEntries(docsExamples);

	assertEquals(before, after);
	assertEquals(4, first.size());
	assertEquals(4, second.size());
	assertTrue(first.stream().allMatch(fixture -> fixture.relativePath().startsWith("docs/examples/")));
	assertTrue(first.stream().allMatch(fixture -> fixture.resolvedPath().startsWith(docsExamples)));
	assertEquals(first, second);
    }

    private void assertIndexedFixture(CanonicalFixtures.FixtureRole role, int expectedLineCount, List<String> expectedSpans) {
	CanonicalFixtureHeadingIndex.IndexedFixtureHeadings headings = CanonicalFixtureHeadingIndex.index(role);
	CanonicalFixtureSectionSpanIndex.IndexedFixtureSections indexed = CanonicalFixtureSectionSpanIndex.index(role);

	assertEquals(headings.role(), indexed.role());
	assertEquals(headings.relativePath(), indexed.relativePath());
	assertEquals(headings.resolvedPath(), indexed.resolvedPath());
	assertEquals(expectedLineCount, indexed.lineCount());
	assertEquals(expectedSpans, indexed.sections().stream().map(span -> span.startLine() + "-" + span.endLine()).collect(Collectors.toList()));
	assertEquals(headings.headings().size(), indexed.sections().size());
	assertTrue(indexed.sections().stream().allMatch(span -> span.heading().lineNumber() == span.startLine()));
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
