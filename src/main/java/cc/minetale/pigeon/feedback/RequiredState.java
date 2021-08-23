package cc.minetale.pigeon.feedback;

public enum RequiredState {
    REQUEST,
    RESPONSE,
    BOTH;

    public boolean doesStateMeetRequirement(FeedbackState state) {
        return this == BOTH ||
                (state == FeedbackState.REQUEST && this == REQUEST) ||
                (state == FeedbackState.RESPONSE && this == RESPONSE);
    }
}
