package ${packageName}

import io.vlingo.lattice.exchange.ExchangeAdapter
import io.vlingo.lattice.exchange.MessageParameters
import io.vlingo.lattice.exchange.MessageParameters.DeliveryMode
import io.vlingo.lattice.exchange.rabbitmq.Message

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

/**
 * See <a href="https://docs.vlingo.io/vlingo-lattice/exchange#exchangeadapter">ExchangeAdapter</a>
 */
public class ${exchangeAdapterName} : ExchangeAdapter<${localTypeName}, String, Message> {

  val supportedSchemaName: String

  public fun constructor(supportedSchemaName: String) {
    this.supportedSchemaName = supportedSchemaName
  }

  public override fun fromExchange(exchangeMessage: Message): ${localTypeName} {
    return ${exchangeMapperName}().externalToLocal(exchangeMessage.payloadAsText())
  }

  public override fun toExchange(local: ${localTypeName}): Message {
    val messagePayload = ${exchangeMapperName}().localToExternal(local)
    return Message(messagePayload, MessageParameters.bare().deliveryMode(DeliveryMode.Durable))
  }

  public override boolean supports(exchangeMessage: Object) {
    if(!exchangeMessage.getClass().equals(Message::class.java)) {
      return false
    }
    val schemaName = ((Message) exchangeMessage).messageParameters.typeName()
    return supportedSchemaName.equalsIgnoreCase(schemaName)
  }

}
