package ${packageName};

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.actors.Grid;
import io.vlingo.xoom.XoomInitializationAware;
import io.vlingo.xoom.annotation.initializer.Xoom;

@Xoom(name = "${appName}")
<#if restResourcePackage?has_content>
@ResourceHandlers(packages = "${restResourcePackage}")
</#if>
public class Bootstrap implements XoomInitializationAware {

  @Override
  public void onInit(final Grid grid) {
    grid.quorumAchieved();
  }

<#if hasProducerExchange>
  @Override
  public io.vlingo.symbio.store.dispatch.Dispatcher exchangeDispatcher(final Grid grid) {
     return ExchangeBootstrap.init(grid).dispatcher();
  }
</#if>
}
