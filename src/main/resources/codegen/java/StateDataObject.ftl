package ${packageName};

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import ${stateQualifiedClassName};

public class ${dataName} {
  <#list members as member>
  ${member}
  </#list>

  public static ${dataName} from(final ${stateName} state) {
    return new ${dataName}(state);
  }

  public static List<${dataName}> from(final List<${stateName}> states) {
    return states.stream().map(${dataName}::from).collect(Collectors.toList());
  }

  public static ${dataName} empty() {
    return new ${dataName}(${stateName}.identifiedBy(${defaultId}));
  }

  private ${dataName} (final ${stateName} state) {
    <#list membersAssignment as assignment>
    ${assignment}
    </#list>
    <#list dataValueObjectAssignment as assignment>
    ${assignment}
    </#list>
  }

}
