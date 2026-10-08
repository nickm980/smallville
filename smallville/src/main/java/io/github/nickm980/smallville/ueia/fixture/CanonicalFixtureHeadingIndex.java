package io.github.nickm980.smallville.ueia.fixture;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CanonicalFixtureHeadingIndex {

    private static final Pattern ATX_HEADING = Pattern.compile("^(#{1,6})\\s+(.+)$");
    private static final Pattern LINE_SPLIT = Pattern.compile("\\r\\n|\\n|\\r");

    private CanonicalFixtureHeadingIndex() {
    }

    public static IndexedFixtureHeadings index(CanonicalFixtures.FixtureRole role) {
	CanonicalFixtureTextLoader.LoadedFixtureText loaded = CanonicalFixtureTextLoader.load(role);
	String[] lines = LINE_SPLIT.split(loaded.content(), -1);
	List<HeadingEntry> headings = collectHeadings(lines);
	return new IndexedFixtureHeadings(loaded.role(), loaded.relativePath(), loaded.resolvedPath(), headings);
    }

    public static List<IndexedFixtureHeadings> indexAll() {
	return CanonicalFixtures
	    .all()
	    .stream()
	    .map(CanonicalFixtures.FixtureRef::role)
	    .map(CanonicalFixtureHeadingIndex::index)
	    .toList();
    }

    private static List<HeadingEntry> collectHeadings(String[] lines) {
	List<HeadingEntry> headings = new java.util.ArrayList<HeadingEntry>();

	for (int i = 0; i < lines.length; i++) {
	    Matcher matcher = ATX_HEADING.matcher(lines[i]);
	    if (!matcher.matches()) {
		continue;
	    }

	    String marker = matcher.group(1);
	    String text = matcher.group(2);
	    headings.add(new HeadingEntry(i + 1, marker.length(), marker, text));
	}

	return List.copyOf(headings);
    }

    public record HeadingEntry(int lineNumber, int level, String marker, String text) {
	public HeadingEntry {
	    if (lineNumber < 1) {
		throw new IllegalArgumentException("lineNumber must be 1-based");
	    }
	    if (level < 1 || level > 6) {
		throw new IllegalArgumentException("level must be between 1 and 6");
	    }
	    Objects.requireNonNull(marker, "marker");
	    Objects.requireNonNull(text, "text");
	}
    }

    public record IndexedFixtureHeadings(CanonicalFixtures.FixtureRole role, String relativePath, Path resolvedPath,
	    List<HeadingEntry> headings) {
	public IndexedFixtureHeadings {
	    Objects.requireNonNull(role, "role");
	    Objects.requireNonNull(relativePath, "relativePath");
	    Objects.requireNonNull(resolvedPath, "resolvedPath");
	    Objects.requireNonNull(headings, "headings");
	}
    }
}
