"""Contract tests for the deterministic memory packet."""


def test_memory_packet_validates(sidecar_packets, sidecar_validator):
    sidecar_validator.validate(
        sidecar_packets["memory"],
        "memory-packet.schema.yaml",
    )


def test_memory_packet_tracks_memory_config_and_agents(sidecar_packets):
    packet = sidecar_packets["memory"]
    agents = {agent["agentName"]: agent for agent in packet["agents"]}

    assert packet["packetType"] == "memory"
    assert packet["memoryConfig"]["ponderEnabled"] is True
    assert packet["memoryConfig"]["eveningSocialBoost"] == 0.2
    assert sorted(agents) == ["Alex", "Jamie"]
    assert agents["Alex"]["memoryIndex"]["lastAffectPresent"] is True
    assert agents["Jamie"]["memoryIndex"]["ponderCount"] >= 0
