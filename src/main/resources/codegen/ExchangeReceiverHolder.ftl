package ${packageName};

import io.vlingo.lattice.exchange.ExchangeReceiver;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeReceiverHolderName} {

  <#list exchangeReceivers as receiver>
  static class ${receiver.schemaTypeName} implements ExchangeReceiver<${receiver.localTypeName}> {
    @Override
    public void receive(final ${receiver.localTypeName} data) {
      //TODO: Handle ${receiver.schemaTypeName} here
    }
  }

  </#list>
}