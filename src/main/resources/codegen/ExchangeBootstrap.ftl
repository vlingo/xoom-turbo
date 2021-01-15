package ${packageName};

import io.vlingo.xoom.actors.Settings;
import io.vlingo.lattice.exchange.Exchange;
import io.vlingo.xoom.exchange.ExchangeParameters;
import io.vlingo.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.lattice.exchange.ConnectionSettings;
import io.vlingo.lattice.exchange.rabbitmq.Message;
import io.vlingo.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.lattice.exchange.Covey;
import io.vlingo.symbio.store.dispatch.Dispatcher;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ExchangeBootstrap {

  private static ExchangeBootstrap instance;

  private final Dispatcher dispatcher;

  public static ExchangeBootstrap init() {
    if(instance != null) {
      return instance;
    }

    ExchangeParameters.load(Settings.properties());

    <#list exchanges as exchange>
    final ConnectionSettings ${exchange.settingsName} = ExchangeParameters.of("${exchange.name}").mapToConnectionSettings();
    final Exchange ${exchange.variableName} = ExchangeFactory.fanOutInstance(${exchange.settingsName}, "${exchange.name}", true);
      <#list exchange.coveys as covey>
    ${exchange.variableName}.register(Covey.of(
        new MessageSender(${exchange.variableName}.connection()),
        ${covey.receiverInstantiation},
        ${covey.adapterInstantiation},
        ${covey.localClass}.class,
        ${covey.externalClass}.class,
        Message.class));

      </#list>
    </#list>

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        <#list exchanges as exchange>
        ${exchange.variableName}.close();
        </#list>

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));

    instance = new ExchangeBootstrap(${producerExchanges});

    return instance;
  }

  private ExchangeBootstrap(final Exchange ...exchanges) {
    this.dispatcher = new ExchangeDispatcher(exchanges);
  }

  public Dispatcher dispatcher() {
    return dispatcher;
  }

}