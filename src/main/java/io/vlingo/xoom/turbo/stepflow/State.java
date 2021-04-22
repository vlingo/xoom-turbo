// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.stepflow;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link State} is an interface definition that should describe a collection of input states and output states.
 */
@SuppressWarnings("rawtypes")
public abstract class State<T extends State> {

  private Long createdAt;
  private UUID version;
  private String name;

  @SuppressWarnings("unchecked")
  public State() {
    setName(this.getName());
    if (name == null) throw new RuntimeException("A state must override getName() for "
            + this.getClass().getSimpleName());
    this.createdAt = new Date().getTime();
    this.version = UUID.randomUUID();
  }

  public Long getCreatedAt() {
    return this.createdAt;
  }

  public UUID getVersion() {
    return version;
  }

  private void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public abstract TransitionHandler[] getTransitionHandlers();

  @SuppressWarnings("unchecked")
  public Map<String, Set<String>> getMap() {
    Map<String, Set<String>> result = new HashMap<>();

    Stream.of(this.getTransitionHandlers())
            .map(t -> (Transition) t.getStateTransition())
            .forEach(t -> result.compute(t.getSourceName(), (k, v) -> {
              Set<String> values = Optional.ofNullable(v)
                      .orElseGet(HashSet::new);
              values.add(t.getTargetName());
              return values;
            }));

    return result;
  }

  public String toGraph() {
    return this.getMap().entrySet().stream()
            .map((kv) -> "(" + kv.getKey() + ")" + "-->(" +
                    Arrays.toString(kv.getValue().toArray()) + ")")
            .collect(Collectors.joining("\n"));
  }

  @Override
  public String toString() {
    return "State{" +
            "createdAt=" + createdAt +
            ", version=" + version +
            ", name='" + name + '\'' +
            '}';
  }
}
