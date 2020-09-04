package ${packageName};

import java.util.ArrayList;
import java.util.Collection;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateStoreQueryActor;
import io.vlingo.symbio.store.state.StateStore;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${queriesActorName} extends StateStoreQueryActor implements ${queriesName} {

  public ${queriesActorName}(StateStore store) {
    super(store);
  }

  @Override
  public Completes<${dataName}> ${queryByIdMethodName}(String id) {
    return queryStateFor(id, ${dataName}.class, ${dataName}.empty());
  }

  @Override
  public Completes<Collection<${dataName}>> ${queryAllMethodName}() {
    return streamAllOf(${dataName}.class, new ArrayList<>());
  }

}
