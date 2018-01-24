package org.itsallcode.openfasttrace.importer;

/*-
 * #%L
 \* OpenFastTrace
 * %%
 * Copyright (C) 2016 - 2017 itsallcode.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.itsallcode.openfasttrace.core.SpecificationItem;
import org.itsallcode.openfasttrace.core.SpecificationItemId;
import org.itsallcode.openfasttrace.importer.SpecificationListBuilder;
import org.junit.Test;;

public class TestSpecificationItemListBuilder
{
    private static final String DESCRIPTION = "description";
    private final static SpecificationItemId ID = SpecificationItemId.parseId("feat~id~1");

    @Test
    public void testBuildBasicItem()
    {
        final SpecificationListBuilder itemsBuilder = new SpecificationListBuilder();
        itemsBuilder.beginSpecificationItem();
        itemsBuilder.setId(ID);
        itemsBuilder.appendDescription(DESCRIPTION);
        final List<SpecificationItem> items = itemsBuilder.build();
        assertThat(items.size(), equalTo(1));
        assertThat(items.get(0).getId(), equalTo(ID));
        assertThat(items.get(0).getDescription(), equalTo(DESCRIPTION));
    }
}
