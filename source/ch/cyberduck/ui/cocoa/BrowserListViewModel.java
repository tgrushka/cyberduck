package ch.cyberduck.ui.cocoa;

/*
 *  Copyright (c) 2005 David Kocher. All rights reserved.
 *  http://cyberduck.ch/
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  Bug fixes, suggestions and comments should be sent to:
 *  dkocher@cyberduck.ch
 */

import ch.cyberduck.core.AttributedList;
import ch.cyberduck.core.Cache;
import ch.cyberduck.core.Path;
import ch.cyberduck.ui.cocoa.application.NSDraggingInfo;
import ch.cyberduck.ui.cocoa.application.NSPasteboard;
import ch.cyberduck.ui.cocoa.application.NSTableColumn;
import ch.cyberduck.ui.cocoa.application.NSTableView;
import ch.cyberduck.ui.cocoa.foundation.NSArray;
import ch.cyberduck.ui.cocoa.foundation.NSIndexSet;
import ch.cyberduck.ui.cocoa.foundation.NSMutableArray;
import ch.cyberduck.ui.cocoa.foundation.NSObject;
import ch.cyberduck.ui.cocoa.foundation.NSString;
import ch.cyberduck.ui.cocoa.foundation.NSURL;

import org.rococoa.cocoa.foundation.NSInteger;
import org.rococoa.cocoa.foundation.NSUInteger;

import java.util.List;

/**
 * @version $Id$
 */
public class BrowserListViewModel extends BrowserTableDataSource implements NSTableView.DataSource {

    public BrowserListViewModel(final BrowserController controller, final Cache cache) {
        super(controller, cache);
    }

    @Override
    public NSInteger numberOfRowsInTableView(final NSTableView view) {
        if(controller.isMounted()) {
            return new NSInteger(this.list(this.controller.workdir()).size());
        }
        return new NSInteger(0);
    }

    @Override
    public void tableView_setObjectValue_forTableColumn_row(final NSTableView view, final NSObject value,
                                                            final NSTableColumn column, final NSInteger row) {
        super.setObjectValueForItem(this.get(this.controller.workdir()).get(row.intValue()),
                value, column.identifier());
    }

    @Override
    public NSObject tableView_objectValueForTableColumn_row(final NSTableView view,
                                                            final NSTableColumn column, final NSInteger row) {
        if(controller.isMounted()) {
            final List<Path> children = this.get(this.controller.workdir());
            if(row.intValue() < children.size()) {
                return super.objectValueForItem(children.get(row.intValue()), column.identifier());
            }
        }
        return null;
    }

    // ----------------------------------------------------------
    // Drop methods
    // ----------------------------------------------------------

    @Override
    public NSUInteger tableView_validateDrop_proposedRow_proposedDropOperation(final NSTableView view,
                                                                               final NSDraggingInfo draggingInfo,
                                                                               final NSInteger row, final NSUInteger operation) {
        if(controller.isMounted()) {
            Path destination = controller.workdir();
            if(row.intValue() < this.numberOfRowsInTableView(view).intValue()) {
                int draggingColumn = view.columnAtPoint(draggingInfo.draggingLocation()).intValue();
                if(-1 == draggingColumn || 0 == draggingColumn || 1 == draggingColumn) {
                    // Allow drags to icon and filename column
                    if(row.intValue() != -1) {
                        Path p = this.get(this.controller.workdir()).get(row.intValue());
                        if(p.attributes().isDirectory()) {
                            destination = p;
                        }
                    }
                }
                return super.validateDrop(view, destination, row, draggingInfo);
            }
            // Draging to empty area in browser
            return super.validateDrop(view, destination, row, draggingInfo);
        }
        // Passing to super to look for URLs to mount
        return super.validateDrop(view, null, row, draggingInfo);
    }

    @Override
    public boolean tableView_acceptDrop_row_dropOperation(final NSTableView view, final NSDraggingInfo draggingInfo,
                                                          final NSInteger row, final NSUInteger operation) {
        if(controller.isMounted()) {
            Path destination = controller.workdir();
            if(row.intValue() != -1) {
                destination = this.get(this.controller.workdir()).get(row.intValue());
            }
            return super.acceptDrop(view, destination, draggingInfo);
        }
        return super.acceptDrop(view, null, draggingInfo);
    }

    // ----------------------------------------------------------
    // Drag methods
    // ----------------------------------------------------------

    /**
     * Invoked by view after it has been determined that a drag should begin, but before the drag has been started.
     * The drag image and other drag-related information will be set up and provided by the table view once this call
     * returns with true.
     *
     * @param rowIndexes is the list of row numbers that will be participating in the drag.
     * @return To refuse the drag, return false. To start a drag, return true and place the drag data onto pboard (data, owner, and so on).
     */
    @Override
    public boolean tableView_writeRowsWithIndexes_toPasteboard(final NSTableView view, final NSIndexSet rowIndexes,
                                                               final NSPasteboard pboard) {
        if(controller.isMounted()) {
            NSMutableArray items = NSMutableArray.array();
            final AttributedList<Path> children = this.get(this.controller.workdir());
            for(NSUInteger index = rowIndexes.firstIndex(); !index.equals(NSIndexSet.NSNotFound); index = rowIndexes.indexGreaterThanIndex(index)) {
                items.addObject(NSString.stringWithString(children.get(index.intValue()).getAbsolute()));
            }
            return super.writeItemsToPasteBoard(view, items, pboard);
        }
        return false;
    }

    @Override
    public NSArray tableView_namesOfPromisedFilesDroppedAtDestination_forDraggedRowsWithIndexes(final NSTableView view,
                                                                                                final NSURL dropDestination,
                                                                                                final NSIndexSet rowIndexes) {
        return this.namesOfPromisedFilesDroppedAtDestination(dropDestination);
    }
}