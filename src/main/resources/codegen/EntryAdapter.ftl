package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Metadata;

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/entity-cqrs#entryadapter-and-entryadapterprovider">
 *   EntryAdapter and EntryAdapterProvider
 * </a>
 */
public final class ${adapterName} implements EntryAdapter<${sourceName},TextEntry> {

  @Override
  public ${sourceName} fromEntry(final TextEntry entry) {
    return JsonSerialization.deserialized(entry.entryData(), entry.typed());
  }

  @Override
  public TextEntry toEntry(final ${sourceName} source, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(${sourceName}.class, 1, serialization, metadata);
  }

  @Override
  public TextEntry toEntry(final ${sourceName} source, final String id, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(id, ${sourceName}.class, 1, serialization, metadata);
  }

  @Override
  public TextEntry toEntry(final ${sourceName} source, final int version, final String id, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(id, ${sourceName}.class, 1, serialization, version, metadata);
  }
}
