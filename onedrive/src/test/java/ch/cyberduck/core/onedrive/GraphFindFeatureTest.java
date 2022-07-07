package ch.cyberduck.core.onedrive;

/*
 * Copyright (c) 2002-2017 iterate GmbH. All rights reserved.
 * https://cyberduck.io/
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
 */

import ch.cyberduck.core.AlphanumericRandomStringService;
import ch.cyberduck.core.DisabledLoginCallback;
import ch.cyberduck.core.Path;
import ch.cyberduck.core.features.Delete;
import ch.cyberduck.core.onedrive.features.GraphDeleteFeature;
import ch.cyberduck.core.onedrive.features.GraphDirectoryFeature;
import ch.cyberduck.core.onedrive.features.GraphFindFeature;
import ch.cyberduck.core.onedrive.features.GraphTouchFeature;
import ch.cyberduck.core.shared.DefaultHomeFinderService;
import ch.cyberduck.core.transfer.TransferStatus;
import ch.cyberduck.test.IntegrationTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(IntegrationTest.class)
public class GraphFindFeatureTest extends AbstractOneDriveTest {

    @Test
    public void testFindDirectory() throws Exception {
        final Path folder = new GraphDirectoryFeature(session, fileid).mkdir(
                new Path(new OneDriveHomeFinderService().find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.directory)), new TransferStatus());
        assertTrue(new GraphFindFeature(session, fileid).find(folder));
        assertFalse(new GraphFindFeature(session, fileid).find(new Path(folder.getAbsolute(), EnumSet.of(Path.Type.file))));
        new GraphDeleteFeature(session, fileid).delete(Collections.singletonList(folder), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testFindFile() throws Exception {
        final Path file = new Path(new OneDriveHomeFinderService().find(), new AlphanumericRandomStringService().random(), EnumSet.of(Path.Type.file));
        new GraphTouchFeature(session, fileid).touch(file, new TransferStatus());
        assertTrue(new GraphFindFeature(session, fileid).find(file));
        assertFalse(new GraphFindFeature(session, fileid).find(new Path(file.getAbsolute(), EnumSet.of(Path.Type.directory))));
        new GraphDeleteFeature(session, fileid).delete(Collections.singletonList(file), new DisabledLoginCallback(), new Delete.DisabledCallback());
    }

    @Test
    public void testFindFileNotFound() throws Exception {
        final GraphFindFeature f = new GraphFindFeature(session, fileid);
        assertFalse(f.find(new Path(new OneDriveHomeFinderService().find(), UUID.randomUUID().toString(), EnumSet.of(Path.Type.file))));
    }

    @Test
    public void testFindDriveNotFound() throws Exception {
        final GraphFindFeature f = new GraphFindFeature(session, fileid);
        assertFalse(f.find(new Path(new OneDriveHomeFinderService().find(), UUID.randomUUID().toString(), EnumSet.of(Path.Type.file))));
    }
}
