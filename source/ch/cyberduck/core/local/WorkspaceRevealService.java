package ch.cyberduck.core.local;

/*
 * Copyright (c) 2013 David Kocher. All rights reserved.
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
 * Bug fixes, suggestions and comments should be sent to:
 * feedback@cyberduck.ch
 */

import ch.cyberduck.core.Factory;
import ch.cyberduck.ui.cocoa.application.NSWorkspace;

/**
 * @version $Id$
 */
public class WorkspaceRevealService implements RevealService {

    public static void register() {
        RevealServiceFactory.addFactory(Factory.NATIVE_PLATFORM, new RevealServiceFactory() {
            @Override
            protected RevealService create() {
                return new WorkspaceRevealService();
            }
        });
    }

    @Override
    public void reveal(final Local file) {
        synchronized(NSWorkspace.class) {
            // If a second path argument is specified, a new file viewer is opened. If you specify an
            // empty string (@"") for this parameter, the file is selected in the main viewer.
            NSWorkspace.sharedWorkspace().selectFile(file.getAbsolute(), file.getParent().getAbsolute());
        }
    }
}
