package io.github.nickm980.smallville.ueia.fixture;

import java.util.Arrays;
import java.util.Collections;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class CanonicalFixtures {

	private static final List<FixtureDefinition> DEFINITIONS = Collections.unmodifiableList(Arrays.asList(
	new FixtureDefinition(FixtureRole.ARCHIVE_RECORD, "docs/examples/promoted-archive-record-v1.md"),
	new FixtureDefinition(FixtureRole.DERIVED_PACKET, "docs/examples/promoted-derived-packet-v1.md"),
	new FixtureDefinition(FixtureRole.AGENT_BRIEF, "docs/examples/promoted-agent-brief-v1.md"),
	new FixtureDefinition(FixtureRole.REVIEW_DECISION, "docs/examples/promoted-review-decision-v1.md")
	));

    private CanonicalFixtures() {
    }

    public static List<FixtureRef> all() {
	return DEFINITIONS
	    .stream()
	    .map(CanonicalFixtures::resolve)
	    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    public static FixtureRef require(FixtureRole role) {
	Objects.requireNonNull(role, "role");
	return DEFINITIONS
	    .stream()
	    .filter(definition -> definition.role() == role)
	    .findFirst()
	    .map(CanonicalFixtures::resolve)
	    .orElseThrow(() -> new IllegalStateException("Unknown canonical fixture role: " + role));
    }

    private static FixtureRef resolve(FixtureDefinition definition) {
	Path resolvedPath = resolveCanonicalPath(definition.relativePath());
	return new FixtureRef(definition.role(), definition.relativePath(), resolvedPath);
    }

    private static Path resolveCanonicalPath(String relativePath) {
	Path[] candidates = new Path[] {
	    Paths.get(relativePath),
	    Paths.get("..").resolve(relativePath)
	};

	for (Path candidate : candidates) {
	    Path normalized = candidate.normalize().toAbsolutePath();
	    if (Files.exists(normalized)) {
		return normalized;
	    }
	}

	throw new IllegalStateException("Could not resolve canonical fixture path: " + relativePath);
    }

    public enum FixtureRole {
	ARCHIVE_RECORD,
	DERIVED_PACKET,
	AGENT_BRIEF,
	REVIEW_DECISION
    }

    public static final class FixtureRef {
	private final FixtureRole role;
	private final String relativePath;
	private final Path resolvedPath;

	public FixtureRef(FixtureRole role, String relativePath, Path resolvedPath) {
	    this.role = Objects.requireNonNull(role, "role");
	    this.relativePath = Objects.requireNonNull(relativePath, "relativePath");
	    this.resolvedPath = Objects.requireNonNull(resolvedPath, "resolvedPath");
	}

	public FixtureRole role() {
	    return role;
	}

	public String relativePath() {
	    return relativePath;
	}

	public Path resolvedPath() {
	    return resolvedPath;
	}

	@Override
	public boolean equals(Object other) {
	    if (this == other) {
		return true;
	    }
	    if (!(other instanceof FixtureRef)) {
		return false;
	    }
	    FixtureRef that = (FixtureRef) other;
	    return role == that.role
		&& relativePath.equals(that.relativePath)
		&& resolvedPath.equals(that.resolvedPath);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(role, relativePath, resolvedPath);
	}

	@Override
	public String toString() {
	    return "FixtureRef[role=" + role + ", relativePath=" + relativePath + ", resolvedPath=" + resolvedPath + "]";
	}
    }

    private static final class FixtureDefinition {
	private final FixtureRole role;
	private final String relativePath;

	private FixtureDefinition(FixtureRole role, String relativePath) {
	    this.role = Objects.requireNonNull(role, "role");
	    this.relativePath = Objects.requireNonNull(relativePath, "relativePath");
	}

	private FixtureRole role() {
	    return role;
	}

	private String relativePath() {
	    return relativePath;
	}
    }
}
