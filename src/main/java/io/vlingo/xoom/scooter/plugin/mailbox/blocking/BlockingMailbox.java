// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.scooter.plugin.mailbox.blocking;

import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Mailbox;
import io.vlingo.actors.Message;

public class BlockingMailbox implements Mailbox {
  public static final String Name = "blockingMailbox";

  private final AtomicBoolean closed;
  private final AtomicBoolean delivering;
  private final Queue<Message> queue;
  private final AtomicReference<Stack<List<Class<?>>>> suspendedOverrides;

  public BlockingMailbox() {
    this.closed = new AtomicBoolean(false);
    this.delivering = new AtomicBoolean(false);
    this.queue = new ConcurrentLinkedQueue<>();
    this.suspendedOverrides = new AtomicReference<>(new Stack<>());
  }

  @Override
  public void run() {
    throw new UnsupportedOperationException("BlockingMailbox does not support this operation.");
  }

  @Override
  public void close() {
    closed.set(true);
  }

  @Override
  public boolean isClosed() {
    return closed.get();
  }

  @Override
  public boolean isDelivering() {
    return delivering.get();
  }

  @Override
  public int concurrencyCapacity() {
    return 1;
  }

  @Override
  public void resume(final String name) {
    if (!suspendedOverrides.get().empty()) {
      suspendedOverrides.get().pop();
    }
    deliverAll();
  }

  @Override
  public void send(final Message message) {
    if (isClosed()) return;

    queue.add(message);

    if (isSuspended()) {
      return;
    }

    try {
      boolean deliver = true;

      while (deliver) {
        if (delivering.compareAndSet(false, true)) {
          while (deliverAll())
            ;
          delivering.set(false);
        }
        deliver = false;
      }
    } catch (Throwable t) {
      // should never happen because message
      // delivery is protected by supervision,
      // although it could be a mailbox problem
      if (delivering.get()) {
        delivering.set(false);
      }
      throw new RuntimeException(t.getMessage(), t);
    }
  }

  @Override
  public void suspendExceptFor(final String name, final Class<?>... overrides) {
    suspendedOverrides.get().push(Arrays.asList(overrides));
  }

  @Override
  public boolean isSuspended() {
    return !suspendedOverrides.get().empty();
  }

  @Override
  public Message receive() {
    throw new UnsupportedOperationException("BlockingMailbox does not support this operation.");
  }

  @Override
  public int pendingMessages() {
    return queue.size();
  }

  private boolean deliverAll() {
    boolean any = false;

    while (!queue.isEmpty()) {
      final Message queued = queue.poll();
      if (queued != null) {
        final Actor actor = queued.actor();
        if (actor != null) {
          any = true;
          actor.viewTestStateInitialization(null);
          queued.deliver();
        }
      }
    }

    return any;
  }
}