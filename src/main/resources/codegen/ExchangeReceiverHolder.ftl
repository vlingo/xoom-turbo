package ${packageName};

import io.vlingo.actors.Stage;
import io.vlingo.lattice.exchange.ExchangeReceiver;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeReceiverHolderName} {

<#list exchangeReceivers as receiver>
  static class ${receiver.schemaTypeName} implements ExchangeReceiver<${receiver.localTypeName}> {

    private final Stage stage;

    public ${receiver.schemaTypeName}(final Stage stage) {
      this.stage = stage;
    }

    @Override
    public void receive(final ${receiver.localTypeName} data) {
      <#if receiver.modelFactoryMethod>
      ${receiver.modelProtocol}.${receiver.modelMethod}(${receiver.modelMethodParameters});
      <#else>
      stage.actorOf(${receiver.modelProtocol}.class, stage.addressFactory().from(data.id), ${receiver.modelActor}.class)
              .andFinallyConsume(${receiver.modelVariable} -> ${receiver.modelVariable}.${receiver.modelMethod}(${receiver.modelMethodParameters}));
      </#if>
    }
  }

</#list>
}