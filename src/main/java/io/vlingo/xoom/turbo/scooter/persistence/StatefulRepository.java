// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.persistence;

import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest;
import io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public abstract class StatefulRepository {
  protected StatefulRepository() {
  }

  /**
   * Answer the T afer awaiting the read to be completed. The {@code interest}
   * must be requested upon each new {@code read()}.
   *
   * @param interest the ReadInterest on which the await is based
   * @param <T>      the type of object to answer
   * @return T
   */
  @SuppressWarnings("unchecked")
  protected <T> T await(final ReadInterest interest) {
    while (!interest.isRead()) {
      interest.throwIfException();
    }

    return (T) interest.state.get();
  }

  /**
   * Await on the write to be completed. The {@code interest} must be
   * requested upon each new {@code write()}.
   *
   * @param interest the WriteInterest on which the await is based
   */
  protected void await(final WriteInterest interest) {
    while (!interest.isWritten()) {
      interest.throwIfException();
    }
  }

  /**
   * Answer a {@code StatefulRepository.ReadInterest} for each new {@code read()}.
   *
   * @return ReadInterest
   */
  protected ReadInterest readInterest() {
    return new ReadInterest();
  }

  /**
   * Answer a {@code StatefulRepository.WriteInterest} for each new {@code write()}.
   *
   * @return WriteInterest
   */
  protected WriteInterest writeInterest() {
    return new WriteInterest();
  }

  public static class ReadInterest extends Exceptional implements ReadResultInterest {
    private final AtomicBoolean read;
    private AtomicReference<Object> state = new AtomicReference<>();

    @Override
    public <S> void readResultedIn(final Outcome<StorageException, Result> outcome, final String id, final S state, final int stateVersion, final Metadata metadata, final Object object) {
      readConsidering(outcome, state);
    }

    private void read(final Object state) {
      synchronized (read) {
        this.state.set(state);
        this.read.set(true);
      }
    }

    private boolean isRead() {
      synchronized (read) {
        return this.read.get();
      }
    }

    private <S> void readConsidering(final Outcome<StorageException, Result> outcome, final S state) {
      outcome
              .andThen(result -> {
                if (result.isSuccess()) {
                  read(state);
                }
                return result;
              })
              .otherwise(ex -> {
                exception.set(ex);
                return outcome.getOrNull();
              });
    }

    private ReadInterest() {
      this.read = new AtomicBoolean();
      this.state = new AtomicReference<>();
    }
  }

  public static class WriteInterest extends Exceptional implements WriteResultInterest {
    private final AtomicBoolean written;

    @Override
    public <S, C> void writeResultedIn(final Outcome<StorageException, Result> outcome, final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Object object) {
      writtenConsidering(outcome);
    }

    private void written() {
      synchronized (written) {
        this.written.set(true);
      }
    }

    private boolean isWritten() {
      synchronized (written) {
        return this.written.get();
      }
    }

    private WriteInterest() {
      this.written = new AtomicBoolean();
    }

    private <S> void writtenConsidering(final Outcome<StorageException, Result> outcome) {
      outcome
              .andThen(result -> {
                if (result.isSuccess()) {
                  written();
                }
                return result;
              })
              .otherwise(ex -> {
                exception.set(ex);
                return outcome.getOrNull();
              });
    }
  }

  private static class Exceptional {
    protected final AtomicReference<StorageException> exception;

    protected Exceptional() {
      this.exception = new AtomicReference<>();
    }

    void throwIfException() {
      synchronized (exception) {
        final Throwable t = exception.get();
        if (t != null) {
          throw new IllegalStateException("Append failed because: " + t.getMessage(), t);
        }
      }
    }
  }
}
