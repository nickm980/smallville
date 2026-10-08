package io.github.nickm980.smallville.ueia.fixture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class CanonicalFixtureTextLoaderTest {

    @Test
    public void load_returns_same_role_relative_path_and_resolved_path_as_discovery() throws IOException {
	assertLoadedFixture(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD);
	assertLoadedFixture(CanonicalFixtures.FixtureRole.DERIVED_PACKET);
	assertLoadedFixture(CanonicalFixtures.FixtureRole.AGENT_BRIEF);
	assertLoadedFixture(CanonicalFixtures.FixtureRole.REVIEW_DECISION);
    }

    @Test
    public void load_all_returns_four_items_in_canonical_order_with_exact_content() throws IOException {
	List<CanonicalFixtureTextLoader.LoadedFixtureText> fixtures = CanonicalFixtureTextLoader.loadAll();

	assertEquals(4, fixtures.size());
	assertEquals(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD, fixtures.get(0).role());
	assertEquals(CanonicalFixtures.FixtureRole.DERIVED_PACKET, fixtures.get(1).role());
	assertEquals(CanonicalFixtures.FixtureRole.AGENT_BRIEF, fixtures.get(2).role());
	assertEquals(CanonicalFixtures.FixtureRole.REVIEW_DECISION, fixtures.get(3).role());

	for (CanonicalFixtureTextLoader.LoadedFixtureText fixture : fixtures) {
	    String expected = Files.readString(fixture.resolvedPath(), StandardCharsets.UTF_8);
	    assertEquals(expected, fixture.content());
	}
    }

    @Test
    public void repeated_loads_stay_under_docs_examples_and_do_not_create_outputs() throws IOException {
	Path docsExamples = CanonicalFixtures.require(CanonicalFixtures.FixtureRole.ARCHIVE_RECORD).resolvedPath().getParent();
	List<String> before = directoryEntries(docsExamples);

	List<CanonicalFixtureTextLoader.LoadedFixtureText> first = CanonicalFixtureTextLoader.loadAll();
	List<CanonicalFixtureTextLoader.LoadedFixtureText> second = CanonicalFixtureTextLoader.loadAll();
	List<String> after = directoryEntries(docsExamples);

	assertEquals(before, after);
	assertEquals(4, first.size());
	assertEquals(4, second.size());
	assertTrue(first.stream().allMatch(fixture -> fixture.relativePath().startsWith("docs/examples/")));
	assertTrue(first.stream().allMatch(fixture -> fixture.resolvedPath().startsWith(docsExamples)));
	assertEquals(first, second);
    }

    private void assertLoadedFixture(CanonicalFixtures.FixtureRole role) throws IOException {
	CanonicalFixtures.FixtureRef expected = CanonicalFixtures.require(role);
	CanonicalFixtureTextLoader.LoadedFixtureText loaded = CanonicalFixtureTextLoader.load(role);

	assertEquals(expected.role(), loaded.role());
	assertEquals(expected.relativePath(), loaded.relativePath());
	assertEquals(expected.resolvedPath(), loaded.resolvedPath());
	assertEquals(Files.readString(expected.resolvedPath(), StandardCharsets.UTF_8), loaded.content());
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
