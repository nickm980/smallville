"""Contract tests for the deterministic affect packet."""


def test_affect_packet_validates(sidecar_packets, sidecar_validator):
    sidecar_validator.validate(
        sidecar_packets["affect"],
        "affect-packet.schema.yaml",
    )


def test_affect_packet_mirrors_host_formula_strings(sidecar_packets):
    packet = sidecar_packets["affect"]
    agents = {agent["agentName"]: agent for agent in packet["agents"]}

    assert packet["formulaMirrors"]["ponderValence"] == (
        "clamp((avgImportance / 5.0) - 0.5, -1.0, 1.0)"
    )
    assert packet["formulaMirrors"]["eveningWindModulation"] == (
        "socialDrive + eveningSocialBoost and activation - eveningActivationDamp"
    )
    assert agents["Alex"]["affectAvailable"] is True
    assert agents["Alex"]["affectState"]["moodLabel"] == "distressed"
