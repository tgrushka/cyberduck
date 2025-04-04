package ch.cyberduck.core.dav;

/*
 * Copyright (c) 2002-2013 David Kocher. All rights reserved.
 * http://cyberduck.ch/
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * Bug fixes, suggestions and comments should be sent to feedback@cyberduck.ch
 */

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class DAVMetadataFeatureTest extends AbstractDAVTest {

    @Test
    public void testSetMetadataFile() throws Exception {
        final TransferStatus status = new TransferStatus();
        final Path test = new DAVTouchFeature(session).touch(new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file)), status);
        final String v = new AlphanumericRandomStringService().random();
        final DAVMetadataFeature feature = new DAVMetadataFeature(session);
        feature.setMetadata(test, status.setMetadata(Collections.singletonMap("Test", v)));
        final Map<String, String> metadata = feature.getMetadata(test);
        assertFalse(metadata.isEmpty());
        assertTrue(metadata.containsKey("Test"));
        assertEquals(v, metadata.get("Test"));
        assertEquals(status.getMetadata(), metadata);
        test.attributes().setMetadata(metadata);
        feature.setMetadata(test, status.setMetadata(Collections.emptyMap()));
        assertFalse(feature.getMetadata(test).containsKey("Test"));
        new DAVDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testSetMetadataFolder() throws Exception {
        final TransferStatus status = new TransferStatus();
        final Path test = new DAVDirectoryFeature(session).mkdir(new Path(new DefaultHomeFinderService(session).find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), status);
        final String v = new AlphanumericRandomStringService().random();
        final DAVMetadataFeature feature = new DAVMetadataFeature(session);
        feature.setMetadata(test, status.setMetadata(Collections.singletonMap("Test", v)));
        final Map<String, String> metadata = feature.getMetadata(test);
        assertFalse(metadata.isEmpty());
        assertTrue(metadata.containsKey("Test"));
        assertEquals(v, metadata.get("Test"));
        assertEquals(status.getMetadata(), metadata);
        new DAVDeleteFeature(session).delete(Collections.singletonList(test), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }
}
