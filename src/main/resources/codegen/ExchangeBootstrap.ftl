package ${packageName};

import io.vlingo.xoom.actors.Settings;
import io.vlingo.lattice.exchange.Exchange;
import io.vlingo.xoom.exchange.ExchangeParameters;
import io.vlingo.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.lattice.exchange.ConnectionSettings;
import io.vlingo.lattice.exchange.rabbitmq.Message;
import io.vlingo.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ExchangeBootstrap {

  private final Dispatcher dispatcher;

  public static ExchangeBootstrap init() {
    ExchangeParameters.load(Settings.properties());

    <#list exchanges as exchange>
    final ConnectionSettings ${exchange.settingsName} = ExchangeParameters.of("${exchange.name}");
    final Exchange ${exchange.variableName} = ExchangeFactory.fanOutInstance(${exchange.settingsName}, "${exchange.name}", true);
      <#list exchange.conveys as convey>
    exchange.register(Covey.of(
        new MessageSender(${exchange.variableName}.connection()),
        ${convey.receiverInstantiation},
        ${convey.adapterInstantiation},
        ${convey.localClass},
        ${convey.externalClass},
        Message.class));
      </#list>
    </#list>

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        <#list exchanges as exchange>
        ${exchange.variableName}.stop();
        </#list>

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));

    return new ExchangeBootstrap(${producerExchanges});
  }

  private ExchangeBootstrap(final Exchange ...exchanges) {
    this.dispatcher = new ExchangeDispatcher(exchanges);
  }

  public Dispatcher dispatcher() {
    return dispatcher;
  }

}