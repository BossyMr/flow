package com.bossymr.flow.constraint;

/**
 * Whether an instruction is reachable.
 */
public enum Reachable {
    /**
     * The instruction is reachable.
     */
    REACHABLE,

    /**
     * The instruction is not reachable.
     */
    NOT_REACHABLE,

    /**
     * The instruction might be reachable, but we can't determine definitively.
     */
    UNKNOWN
}
