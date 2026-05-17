package dev.anchorlikemarlow.alm.intent;

public record IntentDecision(ConfidenceLevel level, float score, String reason) {
    public static IntentDecision low(String reason) {
        return new IntentDecision(ConfidenceLevel.LOW, 0.0f, reason);
    }
}

