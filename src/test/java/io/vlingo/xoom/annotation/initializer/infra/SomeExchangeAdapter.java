// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.initializer.infra;

import io.vlingo.lattice.exchange.ExchangeAdapter;
import io.vlingo.xoom.annotation.model.OtherDummyPromoted;

public class SomeExchangeAdapter implements ExchangeAdapter<OtherDummyPromoted, String, String> {

    @Override
    public OtherDummyPromoted fromExchange(String s) {
        return null;
    }

    @Override
    public String toExchange(OtherDummyPromoted otherDummyPromoted) {
        return null;
    }

    @Override
    public boolean supports(Object o) {
        return false;
    }
}
