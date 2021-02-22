package ${packageName}

import io.vlingo.lattice.exchange.ExchangeMapper
import io.vlingo.common.serialization.JsonSerialization

<#list imports as import>
import ${import.qualifiedClassName}
</#list>

/**
 * See <a href="https://docs.vlingo.io/vlingo-lattice/exchange#exchangemapper">ExchangeMapper</a>
 */
public class ${exchangeMapperName} : ExchangeMapper<${localTypeName}, String> {

  public override fun localToExternal(local: ${localTypeName}): String {
    return JsonSerialization.serialized(local)
  }

  public override fun externalToLocal(external: String): ${localTypeName} {
    return JsonSerialization.deserialized(external, ${localTypeName}::class.java)
  }
}
