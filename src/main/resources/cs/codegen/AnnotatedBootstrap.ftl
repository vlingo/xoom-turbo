namespace ${packageName};

<#list usings as using>
using ${using.qualifiedClassName};
</#list>

using io.vlingo.actors.Stage;
using io.vlingo.xoom.XoomInitializationAware;
using io.vlingo.xoom.annotation.initializer.AddressFactory;
using io.vlingo.xoom.annotation.initializer.Xoom;

using static io.vlingo.xoom.annotation.initializer.AddressFactory.Type.UUID;

@Xoom(name = "${appName}", addressFactory = @AddressFactory(type = UUID))
<#if restResourcePackage?has_content>
@ResourceHandlers(packages = "${restResourcePackage}")
</#if>
public class Bootstrap : XoomInitializationAware {

  @Override
  public void onInit(Stage stage) {
  }

<#if hasProducerExchange>
  @Override
  public io.vlingo.symbio.store.dispatch.Dispatcher exchangeDispatcher(Stage stage) {
     return ExchangeBootstrap.init(stage).dispatcher();
  }
</#if>
}
