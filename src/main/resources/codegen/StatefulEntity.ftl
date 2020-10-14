package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.stateful.StatefulEntity;

public final class ${entityName} extends StatefulEntity<${stateName}> implements ${aggregateProtocolName} {
  private ${stateName} state;

  public ${entityName}(final ${idType} id) {
    super(String.valueOf(id));
    this.state = ${stateName}.identifiedBy(id);
  }

  <#list methods as method>
  ${method}
  </#list>
  @Override
  protected void state(final ${stateName} state) {
    this.state = state;
  }

  @Override
  protected Class<${stateName}> stateType() {
    return ${stateName}.class;
  }
}
