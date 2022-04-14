// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.symbio.store.journal.Journal.AppendResultInterest;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A base class for all concrete {@code Journal} repositories. This implementation
 * blocks and thus must be used only by {@code xoom-scooter} services.
 */
public abstract class JournalRepository {
  protected JournalRepository() {
  }

  /**
   * Answer an {@code JournalRepository.AppendInterest} for each new
   * {@code append()} and {@code appendAll()}.
   *
   * @return AppendInterest
   */
  protected AppendInterest appendInterest() {
    return new AppendInterest();
  }

  /**
   * Await on the append to be completed. The {@code interest} must be
   * requested upon each new {@code append()} and {@code appendAll()}.
   *
   * @param interest the AppendInterest on which the await is based.
   */
  protected void await(final AppendInterest interest) {
    while (!interest.isAppended()) {
      interest.throwIfException();
    }
  }

  public class AppendInterest implements AppendResultInterest {
    private AtomicBoolean appended;
    private AtomicReference<StorageException> exception;

    @Override
    public <S, ST> void appendResultedIn(final Outcome<StorageException, Result> outcome, final String streamName, final int streamVersion, Source<S> source, final Optional<ST> snapshot, final Object object) {
      appendConsidering(outcome);
    }

    @Override
    public <S, ST> void appendResultedIn(final Outcome<StorageException, Result> outcome, final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final Optional<ST> snapshot, final Object object) {
      appendConsidering(outcome);
    }

    @Override
    public <S, ST> void appendAllResultedIn(final Outcome<StorageException, Result> outcome, final String streamName, final int streamVersion, final List<Source<S>> sources, final Optional<ST> snapshot, final Object object) {
      appendConsidering(outcome);
    }

    @Override
    public <S, ST> void appendAllResultedIn(final Outcome<StorageException, Result> outcome, final String streamName, final int streamVersion, final List<Source<S>> sources, final Metadata metadata, final Optional<ST> snapshot, final Object object) {
      appendConsidering(outcome);
    }

    private void appended() {
      synchronized (appended) {
        this.appended.set(true);
      }
    }

    private boolean isAppended() {
      synchronized (appended) {
        return this.appended.get();
      }
    }

    private AppendInterest() {
      this.appended = new AtomicBoolean();
      this.exception = new AtomicReference<>();
    }

    private <S, ST> void appendConsidering(final Outcome<StorageException, Result> outcome) {
      outcome
              .andThen(result -> {
                if (result.isSuccess()) {
                  appended();
                }
                return result;
              })
              .otherwise(ex -> {
                exception.set(ex);
                return outcome.getOrNull();
              });
    }

    private void throwIfException() {
      synchronized (exception) {
        final Throwable t = exception.get();
        if (t != null) {
          throw new IllegalStateException("Append failed because: " + t.getMessage(), t);
        }
      }
    }
  }
}
