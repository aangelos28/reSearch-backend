package edu.cs518.angelopoulos.research.common.models;

import lombok.Getter;

public enum EtdClaimReproducible {
    NO(0), PARTIALLY(1), YES(2);

    @Getter
    private final int value;

    EtdClaimReproducible(int value) {
        this.value = value;
    }
}
