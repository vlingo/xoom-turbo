package ${packageName};

import io.vlingo.lattice.exchange.ExchangeReceiver;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeReceiverHolderName} {

  <#list exchangeReceivers as receiver>
  class ${receiver.schemaName} implements ExchangeReceiver<${receiver.localTypeName}> {
    @Override
    public void receive(final ${receiver.localTypeName} data) {
      //TODO: Handle ${receiver.schemaName} here
    }
  }

  </#list>
}