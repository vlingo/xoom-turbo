// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.infra;

import io.vlingo.xoom.annotation.autodispatch.exchange.Adapter;
import io.vlingo.xoom.annotation.autodispatch.exchange.Entry;
import io.vlingo.xoom.annotation.autodispatch.exchange.Exchange;
import io.vlingo.xoom.annotation.autodispatch.exchange.MessageHandler;
import io.vlingo.xoom.annotation.model.Dummy;
import io.vlingo.xoom.annotation.model.DummyEntity;
import io.vlingo.xoom.annotation.model.OtherDummyAccepted;
import io.vlingo.xoom.annotation.model.OtherDummyPromoted;

@Exchange(protocol = Dummy.class, actor = DummyEntity.class)
public class DummyExchangeHandlers {

    @Entry(exchangeName = "dummy-exchange")
    public final MessageHandler.WithStage<OtherDummyAccepted> defineWithHandler =
            (stage, otherDummyAccepted) -> Dummy.defineWith(stage, otherDummyAccepted.name);

    @Entry(exchangeName = "dummy-exchange")
    @Adapter(with = SomeExchangeAdapter.class)
    public final MessageHandler.Basic<Dummy, OtherDummyPromoted> withNameHandler =
            (dummy, otherDummyPromoted) -> dummy.withName(otherDummyPromoted.name);

}
