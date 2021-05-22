// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.turbo.annotation.codegen.storage;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.turbo.annotation.codegen.projections.ProjectionType;

import java.util.ArrayList;
import java.util.List;

public class StorageTemplateDataFactory {

  public static List<TemplateData> build(final String basePackage,
                                         final List<Content> contents,
                                         final StorageType storageType,
                                         final ProjectionType projectionType,
                                         final Boolean useCQRS) {
    final List<TemplateData> templatesData = new ArrayList<>();
    final String persistencePackage = PersistenceDetail.resolvePackage(basePackage);
    templatesData.addAll(AdapterTemplateData.from(persistencePackage, storageType, contents));
    templatesData.addAll(StorageProviderTemplateData.from(persistencePackage, storageType, projectionType,
            templatesData, contents, Model.applicableTo(useCQRS)));
    return templatesData;
  }

}
