package edu.cs518.angelopoulos.research.common.models;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum EtdClaimCommentLikeStatus {
    DISLIKED(-1), NONE(0), LIKED(1);

    @JsonValue
    @Getter
    private final int value;

    EtdClaimCommentLikeStatus(int value) {
        this.value = value;
    }
}
