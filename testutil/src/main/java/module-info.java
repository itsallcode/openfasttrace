/*-
 * #%L
 * OpenFastTrace Test utilities
 * %%
 * Copyright (C) 2016 - 2019 itsallcode.org
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
module org.itsallcode.openfasttrace.testutil
{
    exports org.itsallcode.openfasttrace.testutil.xml;
    exports org.itsallcode.openfasttrace.testutil.core;
    exports org.itsallcode.openfasttrace.testutil;
    exports org.itsallcode.openfasttrace.testutil.matcher;
    exports org.itsallcode.openfasttrace.testutil.cli;
    exports org.itsallcode.openfasttrace.testutil.importer;
    exports org.itsallcode.openfasttrace.testutil.importer.input;
    exports org.itsallcode.openfasttrace.testutil.log;

    requires hamcrest.all;
    requires java.logging;
    requires java.xml;
    requires org.itsallcode.openfasttrace.api;
    requires org.junit.jupiter.api;
    requires org.mockito;
    requires org.opentest4j;
}
