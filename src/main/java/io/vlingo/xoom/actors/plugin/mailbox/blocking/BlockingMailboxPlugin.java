// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.actors.plugin.mailbox.blocking;

import io.vlingo.actors.Configuration;
import io.vlingo.actors.Dispatcher;
import io.vlingo.actors.Mailbox;
import io.vlingo.actors.MailboxProvider;
import io.vlingo.actors.Registrar;
import io.vlingo.actors.plugin.AbstractPlugin;
import io.vlingo.actors.plugin.Plugin;
import io.vlingo.actors.plugin.PluginConfiguration;
import io.vlingo.actors.plugin.PluginProperties;

public class BlockingMailboxPlugin extends AbstractPlugin implements Plugin, MailboxProvider {
  private final BlockingMailboxPluginConfiguration configuration;

  public BlockingMailboxPlugin() {
    this.configuration = new BlockingMailboxPluginConfiguration();
  }

  @Override
  public void close() { }

  @Override
  public PluginConfiguration configuration() {
    return configuration;
  }

  @Override
  public String name() {
    return configuration.name();
  }

  @Override
  public int pass() {
    return 1;
  }

  @Override
  public void start(final Registrar registrar) {
    registrar.register(name(), false, this);
  }

  @Override
  public Plugin with(final PluginConfiguration overrideConfiguration) {
    return this;
  }

  @Override
  public Mailbox provideMailboxFor(final int hashCode) {
    return new BlockingMailbox();
  }

  @Override
  public Mailbox provideMailboxFor(final int hashCode, final Dispatcher dispatcher) {
    return new BlockingMailbox();
  }

  public static class BlockingMailboxPluginConfiguration implements PluginConfiguration {
    public BlockingMailboxPluginConfiguration() { }

    @Override
    public void build(final Configuration configuration) { }

    @Override
    public void buildWith(final Configuration configuration, final PluginProperties properties) { }

    @Override
    public String name() {
      return BlockingMailbox.Name;
    }
  }
}