package com.smashingmods.alchemylib.api.storage;

public enum SideMode {
    DISABLED(false, false),
    PULL(true, false),
    PUSH(false, true),
    ENABLED(true, true);

    private final boolean pullEnabled;
    private final boolean pushEnabled;
    // Java's #value() call is cloning, which may cause some slowdowns. For maximum speed we have a cached array.
    private static final SideMode[] SIDE_MODES_VALUES = values();

    private SideMode(boolean pull, boolean push) {
        this.pullEnabled = pull;
        this.pushEnabled = push;
    }

    public boolean isPullEnabled() {
        return pullEnabled;
    }

    public boolean isPushEnabled() {
        return pushEnabled;
    }

    public static SideMode getFromOrdinal(int ordinal) {
        return SIDE_MODES_VALUES[ordinal];
    }
}
