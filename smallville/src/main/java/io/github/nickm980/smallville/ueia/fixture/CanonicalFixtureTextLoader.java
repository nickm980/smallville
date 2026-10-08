package io.github.nickm980.smallville.ueia.fixture;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

public final class CanonicalFixtureTextLoader {

    private CanonicalFixtureTextLoader() {
    }

    public static LoadedFixtureText load(CanonicalFixtures.FixtureRole role) {
	return load(CanonicalFixtures.require(role));
    }

    public static List<LoadedFixtureText> loadAll() {
	return CanonicalFixtures.all().stream().map(CanonicalFixtureTextLoader::load).toList();
    }

    private static LoadedFixtureText load(CanonicalFixtures.FixtureRef fixture) {
	try {
	    String content = Files.readString(fixture.resolvedPath(), StandardCharsets.UTF_8);
	    return new LoadedFixtureText(fixture.role(), fixture.relativePath(), fixture.resolvedPath(), content);
	} catch (IOException e) {
	    throw new IllegalStateException("Could not load canonical fixture text: " + fixture.relativePath(), e);
	}
    }

    public record LoadedFixtureText(CanonicalFixtures.FixtureRole role, String relativePath, Path resolvedPath,
	    String content) {
	public LoadedFixtureText {
	    Objects.requireNonNull(role, "role");
	    Objects.requireNonNull(relativePath, "relativePath");
	    Objects.requireNonNull(resolvedPath, "resolvedPath");
	    Objects.requireNonNull(content, "content");
	}
    }
}
