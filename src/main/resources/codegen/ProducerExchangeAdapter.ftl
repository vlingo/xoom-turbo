package ${packageName};

import io.vlingo.lattice.exchange.ExchangeAdapter;
import io.vlingo.lattice.exchange.ExchangeMapper;
import io.vlingo.lattice.exchange.MessageParameters;
import io.vlingo.lattice.exchange.MessageParameters.DeliveryMode;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeAdapterName} implements ExchangeAdapter<String, IdentifiedDomainEvent, Message> {

  private static final String SCHEMA_PREFIX = "${schemaGroupName}:";

  @Override
  public String fromExchange(final Message exchangeMessage) {
    return exchangeMessage.payloadAsText();
  }

  @Override
  public Message toExchange(final IdentifiedDomainEvent event) {
    final String messagePayload = JsonSerialization.serialized(event);
    final String schemaName = SCHEMA_PREFIX + event.getClass().getSimpleName();
    return new Message(messagePayload, MessageParameters.bare().deliveryMode(DeliveryMode.Durable).typeName(schemaName));
  }

  @Override
  public boolean supports(final Object exchangeMessage) {
    return exchangeMessage.getClass().equals(Message.class);
  }

}