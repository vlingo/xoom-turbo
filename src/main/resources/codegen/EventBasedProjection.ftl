package ${packageName};

import java.util.ArrayList;
import java.util.List;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Entry;

public class ${projectionName} extends StateStoreProjectionActor<${dataName}> {
  private static final ${dataName} Empty = ${dataName}.empty();

  private String dataId;
  private final List<IdentifiedDomainEvent> events;

  public ${projectionName}() {
    super(${storeProviderName}.instance().store);
    this.events = new ArrayList<>(2);
  }

  @Override
  protected ${dataName} currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected String dataIdFor(final Projectable projectable) {
    dataId = events.get(0).identity();
    return dataId;
  }

  @Override
  protected ${dataName} merge(
      final ${dataName} previousData,
      final int previousVersion,
      final ${dataName} currentData,
      final int currentVersion) {

    if (previousVersion == currentVersion) {
      return currentData;
    }

    for (final DomainEvent event : events) {
      switch (${eventTypesName}.valueOf(event.typeName())) {
      <#list eventsNames as name>
        case ${name}:
          return ${dataName}.from(currentData.id, "Handle ${name} here");   // TODO: implement actual merge
      </#list>
      default:
        logger().warn("Event of type " + event.typeName() + " was not matched.");
        break;
      }
    }

    return previousData;
  }

  @Override
  protected void prepareForMergeWith(final Projectable projectable) {
    events.clear();

    for (Entry <?> entry : projectable.entries()) {
      events.add(entryAdapter().anyTypeFromEntry(entry));
    }
  }
}
