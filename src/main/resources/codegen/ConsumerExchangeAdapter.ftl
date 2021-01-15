package ${packageName};

import io.vlingo.lattice.exchange.ExchangeAdapter;
import io.vlingo.lattice.exchange.ExchangeMapper;
import io.vlingo.lattice.exchange.MessageParameters;
import io.vlingo.lattice.exchange.MessageParameters.DeliveryMode;
import io.vlingo.lattice.exchange.rabbitmq.Message;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeAdapterName} implements ExchangeAdapter<${localTypeName}, String, Message> {

  private final String supportedSchemaName;

  public ${exchangeAdapterName}(final String supportedSchemaName) {
    this.supportedSchemaName = supportedSchemaName;
  }

  @Override
  public ${localTypeName} fromExchange(final Message exchangeMessage) {
    return new ${exchangeMapperName}().externalToLocal(exchangeMessage.payloadAsText());
  }

  @Override
  public Message toExchange(final ${localTypeName} local) {
    final String messagePayload = new ${exchangeMapperName}().localToExternal(local);
    return new Message(messagePayload, MessageParameters.bare().deliveryMode(DeliveryMode.Durable));
  }

  @Override
  public boolean supports(final Object exchangeMessage) {
    if(!exchangeMessage.getClass().equals(Message.class)) {
      return false;
    }
    final String schemaName = ((Message) exchangeMessage).messageParameters.typeName();
    return supportedSchemaName.equalsIgnoreCase(schemaName);
  }

}