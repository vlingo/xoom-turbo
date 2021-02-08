namespace ${packageName};

using io.vlingo.lattice.exchange.ExchangeAdapter;
using io.vlingo.lattice.exchange.MessageParameters;
using io.vlingo.lattice.exchange.MessageParameters.DeliveryMode;
using io.vlingo.lattice.exchange.rabbitmq.Message;

<#list usings as using>
using ${using.qualifiedClassName};
</#list>

public class ${exchangeAdapterName} : ExchangeAdapter<${localTypeName}, string, Message> {

  private string supportedSchemaName;

  public ${exchangeAdapterName}(string supportedSchemaName) {
    this.supportedSchemaName = supportedSchemaName;
  }

  @Override
  public ${localTypeName} fromExchange(Message exchangeMessage) {
    return new ${exchangeMapperName}().externalToLocal(exchangeMessage.payloadAsText());
  }

  @Override
  public Message toExchange(${localTypeName} local) {
    final string messagePayload = new ${exchangeMapperName}().localToExternal(local);
    return new Message(messagePayload, MessageParameters.bare().deliveryMode(DeliveryMode.Durable));
  }

  @Override
  public bool supports(Object exchangeMessage) {
    if(!exchangeMessage.GetType().Equals(Message.GetType())) {
      return false;
    }
    string schemaName = ((Message) exchangeMessage).messageParameters.GetType().Name();
	return string.Equals(supportedSchemaName, schemaName, StringComparison.OrdinalIgnoreCase)
  }

}