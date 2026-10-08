"""Contract tests for the deterministic world packet."""


def test_world_packet_validates(sidecar_packets, sidecar_validator):
    sidecar_validator.validate(
        sidecar_packets["world"],
        "world-packet.schema.yaml",
    )


def test_world_packet_supplements_evening_wind_from_simulation(sidecar_packets):
    packet = sidecar_packets["world"]

    assert packet["dailyRhythmRuntime"]["eveningWind"] is None
    assert packet["dailyRhythmScenario"]["eveningWind"] == "19:30-21:30"
    assert packet["dailyRhythmMerged"]["eveningWind"] == "19:30-21:30"
    assert packet["dailyRhythmSupplementedFields"] == ["eveningWind"]
    assert packet["worldSnapshot"]["pendingProposalCount"] == 0
