package ${packageName}

import io.vlingo.actors.Stage
import io.vlingo.xoom.actors.Settings
import io.vlingo.lattice.exchange.Exchange
import io.vlingo.xoom.exchange.ExchangeSettings
import io.vlingo.lattice.exchange.rabbitmq.ExchangeFactory
import io.vlingo.lattice.exchange.ConnectionSettings
import io.vlingo.lattice.exchange.rabbitmq.Message
import io.vlingo.lattice.exchange.rabbitmq.MessageSender
import io.vlingo.lattice.exchange.Covey
import io.vlingo.symbio.store.dispatch.Dispatcher

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

public class ExchangeBootstrap {

  companion object {
    var instance: ExchangeBootstrap

    public fun init(stage: Stage): ExchangeBootstrap {
      if(instance != null) {
        return instance
      }

      ExchangeSettings.load(Settings.properties())

      <#list exchanges as exchange>
        val ${exchange.settingsName}: ConnectionSettings =
        ExchangeSettings.of("${exchange.name}").mapToConnection()

        val ${exchange.variableName}: Exchange =
        ExchangeFactory.fanOutInstance(${exchange.settingsName}, "${exchange.name}", true)

          <#list exchange.coveys as covey>
              ${exchange.variableName}.register(Covey.of(
            MessageSender(${exchange.variableName}.connection()),
              ${covey.receiverInstantiation},
              ${covey.adapterInstantiation},
              ${covey.localClass}::class.java,
              ${covey.externalClass}::class.java,
            Message::class.java))

          </#list>
      </#list>
      Runtime.getRuntime().addShutdownHook(Thread{
      <#list exchanges as exchange>
        ${exchange.variableName}.close()
      </#list>

        out.println("\n")
        out.println("==================")
        out.println("Stopping exchange.")
        out.println("==================")
      })

      instance = ExchangeBootstrap(${producerExchanges})

      return instance
    }
  }

  val dispatcher: Dispatcher

  constructor(vararg exchanges: Exchange) {
    this.dispatcher = ExchangeDispatcher(exchanges)
  }

  public fun dispatcher(): Dispatcher {
    return dispatcher
  }

}
