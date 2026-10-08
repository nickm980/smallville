package io.github.nickm980.smallville.ueia.fixture;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public final class CanonicalFixtureSectionSpanIndex {

    private static final Pattern LINE_SPLIT = Pattern.compile("\\r\\n|\\n|\\r");

    private CanonicalFixtureSectionSpanIndex() {
    }

    public static IndexedFixtureSections index(CanonicalFixtures.FixtureRole role) {
	CanonicalFixtureHeadingIndex.IndexedFixtureHeadings headingIndex = CanonicalFixtureHeadingIndex.index(role);
	CanonicalFixtureTextLoader.LoadedFixtureText loaded = CanonicalFixtureTextLoader.load(role);
	int lineCount = visibleLineCount(loaded.content());
	List<SectionSpan> sections = buildSectionSpans(headingIndex.headings(), lineCount);
	return new IndexedFixtureSections(headingIndex.role(), headingIndex.relativePath(), headingIndex.resolvedPath(),
		lineCount, sections);
    }

    public static List<IndexedFixtureSections> indexAll() {
	return CanonicalFixtures.all().stream().map(CanonicalFixtures.FixtureRef::role).map(CanonicalFixtureSectionSpanIndex::index).toList();
    }

    private static int visibleLineCount(String content) {
	String[] segments = LINE_SPLIT.split(content, -1);
	int count = segments.length;

	if (count > 0 && segments[count - 1].isEmpty() && endsWithLineBreak(content)) {
	    count--;
	}

	return count;
    }

    private static boolean endsWithLineBreak(String content) {
	return content.endsWith("\r") || content.endsWith("\n");
    }

    private static List<SectionSpan> buildSectionSpans(List<CanonicalFixtureHeadingIndex.HeadingEntry> headings, int lineCount) {
	List<SectionSpan> spans = new java.util.ArrayList<SectionSpan>();

	for (int i = 0; i < headings.size(); i++) {
	    CanonicalFixtureHeadingIndex.HeadingEntry heading = headings.get(i);
	    int startLine = heading.lineNumber();
	    int endLine = i < headings.size() - 1 ? headings.get(i + 1).lineNumber() - 1 : lineCount;
	    spans.add(new SectionSpan(heading, startLine, endLine));
	}

	return List.copyOf(spans);
    }

    public record SectionSpan(CanonicalFixtureHeadingIndex.HeadingEntry heading, int startLine, int endLine) {
	public SectionSpan {
	    Objects.requireNonNull(heading, "heading");
	    if (startLine < 1) {
		throw new IllegalArgumentException("startLine must be 1-based");
	    }
	    if (endLine < startLine) {
		throw new IllegalArgumentException("endLine must be greater than or equal to startLine");
	    }
	}
    }

    public record IndexedFixtureSections(CanonicalFixtures.FixtureRole role, String relativePath, Path resolvedPath,
	    int lineCount, List<SectionSpan> sections) {
	public IndexedFixtureSections {
	    Objects.requireNonNull(role, "role");
	    Objects.requireNonNull(relativePath, "relativePath");
	    Objects.requireNonNull(resolvedPath, "resolvedPath");
	    if (lineCount < 1) {
		throw new IllegalArgumentException("lineCount must be at least 1");
	    }
	    Objects.requireNonNull(sections, "sections");
	}
    }
}
