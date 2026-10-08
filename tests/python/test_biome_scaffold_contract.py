"""Contract tests for the biome scaffold packet."""


def test_biome_scaffold_validates(sidecar_packets, sidecar_validator):
    sidecar_validator.validate(
        sidecar_packets["biome_scaffold"],
        "biome-extension-scaffold.schema.yaml",
    )


def test_biome_scaffold_is_explicitly_non_implemented(sidecar_packets):
    packet = sidecar_packets["biome_scaffold"]

    assert packet["status"] == "scaffold-only"
    assert packet["implemented"] is False
    assert packet["scaffold"]["biomes"] == []
    assert "Blue House" in packet["worldLocationRoots"]
