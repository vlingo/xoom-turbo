package ${packageName};

import io.vlingo.actors.Grid;
import io.vlingo.xoom.actors.Settings;
import io.vlingo.lattice.exchange.Exchange;
import io.vlingo.xoom.exchange.ExchangeSettings;
import io.vlingo.xoom.exchange.ExchangeInitializer;
import io.vlingo.lattice.exchange.rabbitmq.ExchangeFactory;
import io.vlingo.lattice.exchange.ConnectionSettings;
import io.vlingo.lattice.exchange.rabbitmq.Message;
import io.vlingo.lattice.exchange.rabbitmq.MessageSender;
import io.vlingo.lattice.exchange.Covey;
<#if producerExchanges?has_content>
import io.vlingo.symbio.store.dispatch.Dispatcher;
</#if>

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeBootstrapName} implements ExchangeInitializer {

  <#if producerExchanges?has_content>
  private Dispatcher dispatcher;
  </#if>

  @Override
  public void init(final Grid stage) {
    ExchangeSettings.load(Settings.properties());

    <#list exchanges as exchange>
    final ConnectionSettings ${exchange.settingsName} =
                ExchangeSettings.of("${exchange.name}").mapToConnection();

    final Exchange ${exchange.variableName} =
                ExchangeFactory.fanOutInstance(${exchange.settingsName}, "${exchange.name}", true);

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

    <#if producerExchanges?has_content>
    this.dispatcher = new ExchangeDispatcher(${producerExchanges});
    </#if>

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
        <#list exchanges as exchange>
        ${exchange.variableName}.close();
        </#list>

        System.out.println("\n");
        System.out.println("==================");
        System.out.println("Stopping exchange.");
        System.out.println("==================");
    }));
  }

  <#if producerExchanges?has_content>
  @Override
  public Dispatcher dispatcher() {
    return dispatcher;
  }
  </#if>
}