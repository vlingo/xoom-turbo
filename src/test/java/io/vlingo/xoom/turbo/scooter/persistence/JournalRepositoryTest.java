// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.scooter.persistence;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.lattice.model.DomainEvent;
import io.vlingo.xoom.symbio.*;
import io.vlingo.xoom.symbio.store.journal.EntityStream;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.StreamReader;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.xoom.turbo.scooter.model.sourced.EventSourcedEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class JournalRepositoryTest {
  @SuppressWarnings("rawtypes")
  private EntryAdapter adapter;
  private EntryAdapterProvider adapterProvider;
  private Journal<String> journal;
  private World world;
  private MockDispatcher<String, SnapshotState> dispatcher;

  @Test
  public void testThatAppendAwaits() {
    final TestEntityRepository repository = new TestEntityRepository(journal, adapterProvider);

    final String id = "123";

    final TestEntity entity1 = new TestEntity(id);

    Assert.assertFalse(entity1.test1);
    Assert.assertFalse(entity1.test2);

    entity1.doTest1();

    Assert.assertTrue(Test1Happened.class == entity1.applied().sourceTypeAt(0));
    Assert.assertTrue(entity1.test1);
    Assert.assertFalse(entity1.test2);

    repository.save(entity1);

    final TestEntity entity2 = repository.testOf(id);

    Assert.assertEquals(entity1.id(), entity2.id());
    Assert.assertTrue(entity2.test1);
    Assert.assertFalse(entity2.test2);

    entity2.doTest2();

    repository.save(entity2);

    final TestEntity entity3 = repository.testOf(id);

    Assert.assertEquals(entity1.id(), entity3.id());
    Assert.assertTrue(entity3.test1);
    Assert.assertTrue(entity3.test2);
  }

  @Before
  @SuppressWarnings({ "unchecked" })
  public void setUp() {
    world = World.startWithDefaults("repo-test");

    this.dispatcher = new MockDispatcher<>(new MockConfirmDispatchedResultInterest());

    journal = Journal.using(world.stage(), InMemoryJournalActor.class, Arrays.asList(this.dispatcher));

    adapter = new DefaultTextEntryAdapter();

    adapterProvider = EntryAdapterProvider.instance(world);

    adapterProvider.registerAdapter(Test1Happened.class, adapter);
    adapterProvider.registerAdapter(Test2Happened.class, adapter);
  }

  private static class TestEntity extends EventSourcedEntity {
    public boolean test1;
    public boolean test2;
    private String id;

    public TestEntity(final String id) {
      this.id = id;
    }

    public void doTest1() {
      apply(new Test1Happened(id));
    }

    public void doTest2() {
      apply(new Test2Happened(id));
    }

    @Override
    public String id() {
      return streamName();
    }

    @Override
    protected String streamName() {
      return id;
    }

    private void whenDoTest1(final Test1Happened event) {
      this.id = event.id;
      this.test1 = true;
    }

    private void whenDoTest2(final Test2Happened event) {
      this.id = event.id;
      this.test2 = true;
    }

    public TestEntity(final List<Source<DomainEvent>> entries, final int streamVersion) {
      super(entries, streamVersion);
    }

    static {
      registerConsumer(TestEntity.class, Test1Happened.class, TestEntity::whenDoTest1);
      registerConsumer(TestEntity.class, Test2Happened.class, TestEntity::whenDoTest2);
    }
  }

  private static class SnapshotState extends State<String> {
    public SnapshotState() {
      super("123", String.class, 1, "data", 1, null);
    }
  }

  private static final class Test1Happened extends DomainEvent {
    private final String id;

    public Test1Happened(final String id) {
      this.id = id;
    }
  }

  private static final class Test2Happened extends DomainEvent {
    private final String id;

    public Test2Happened(final String id) {
      this.id = id;
    }
  }

  private static class TestEntityRepository extends JournalRepository {
    private final EntryAdapterProvider adapterProvider;
    private final Journal<String> journal;
    private final StreamReader<String> reader;

    public TestEntityRepository(final Journal<String> journal, final EntryAdapterProvider adapterProvider) {
      this.journal = journal;
      this.reader = journal.streamReader("TestRepository").await();
      this.adapterProvider = adapterProvider;
    }

    public TestEntity testOf(final String id) {
      final EntityStream<String> stream = reader.streamFor(id).await();
      final List<Source<DomainEvent>> sources = adapterProvider.asSources(stream.entries);
      return new TestEntity(sources, stream.streamVersion);
    }

    public void save(final TestEntity test) {
      final AppendInterest interest = appendInterest();
      journal.appendAll(test.id(), test.nextVersion(), test.applied().sources(), interest, null);
      await(interest);
    }
  }
}
