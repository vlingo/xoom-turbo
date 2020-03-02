package io.vlingo.xoom.stepflow;

import io.vlingo.actors.Logger;

/**
 * A {@link Transition} is a base interface for a {@link StateTransition} and describes the identity of a source
 * state and a target state.
 *
 * @author Kenny Bastani
 */
public interface Transition {

    String getSourceName();

    String getTargetName();

    public static <T1 extends State, R1 extends State> void logResult(T1 s, R1 t) {
        Logger.basicLogger().info(s.getVersion() + ": [" + s.getName() + "] to [" + t.getName() + "]");
    }
}
