package ${packageName}

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

import io.vlingo.common.serialization.JsonSerialization
import io.vlingo.symbio.Metadata
import io.vlingo.symbio.State.TextState
import io.vlingo.symbio.StateAdapter

/**
 * See
 * <a href="https://docs.vlingo.io/vlingo-lattice/entity-cqrs#stateadapter-and-stateadapterprovider">
 *   StateAdapter and StateAdapterProvider
 * </a>
 */
public class ${adapterName} : StateAdapter<${sourceName},TextState> {

  public override fun typeVersion(): Int {
    return 1
  }

  public override fun fromRawState(raw: TextState): ${sourceName} {
    return JsonSerialization.deserialized(raw.data, raw.typed())
  }

  public override fun <ST> fromRawState(raw: TextState, stateType: Class<ST>): ST {
    return JsonSerialization.deserialized(raw.data, stateType)
  }

  public override fun toRawState(id: String, state: ${sourceName}, stateVersion: Int, metadata: Metadata): TextState {
    val serialization = JsonSerialization.serialized(state)
    return TextState(id, ${sourceName}::class.java, typeVersion(), serialization, stateVersion, metadata)
  }
}
