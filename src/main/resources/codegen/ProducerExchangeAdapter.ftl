package ${packageName};

import io.vlingo.lattice.exchange.ExchangeAdapter;
import io.vlingo.lattice.exchange.ExchangeMapper;
import io.vlingo.lattice.exchange.MessageParameters;
import io.vlingo.lattice.exchange.MessageParameters.DeliveryMode;
import io.vlingo.lattice.exchange.rabbitmq.Message;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeAdapterName} implements ExchangeAdapter<IdentifiedDomainEvent, IdentifiedDomainEvent, Message> {

  private static final String SCHEMA_PREFIX = "${schemaGroupName}:";

  private final ${exchangeMapperName} mapper = new ${exchangeMapperName}();

  @Override
  public IdentifiedDomainEvent fromExchange(final Message exchangeMessage) {
    return mapper.externalToLocal(exchangeMessage);
  }

  @Override
  public Message toExchange(final IdentifiedDomainEvent event) {
    final Message message = mapper.localToExternal(event);
    message.messageParameters.typeName(SCHEMA_PREFIX + event.getClass().getSimpleName());
    return message;
  }

  @Override
  public boolean supports(final Object exchangeMessage) {
    if(!exchangeMessage.getClass().equals(Message.class)) {
      return false;
    }
    final String schemaName = ((Message) exchangeMessage).messageParameters.typeName();
    return schemaName.startsWith(SCHEMA_PREFIX);
  }

}