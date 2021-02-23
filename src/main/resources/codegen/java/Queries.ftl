package ${packageName};

import java.util.Collection;
import io.vlingo.common.Completes;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public interface ${queriesName} {
  Completes<${dataName}> ${queryByIdMethodName}(String id);
  Completes<Collection<${dataName}>> ${queryAllMethodName}();
}