/*
 * Copyright 2024 Laszlo Balazs-Csiki and Contributors
 *
 * This file is part of Pixelitor. Pixelitor is free software: you
 * can redistribute it and/or modify it under the terms of the GNU
 * General Public License, version 3 as published by the Free
 * Software Foundation.
 *
 * Pixelitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Pixelitor. If not, see <http://www.gnu.org/licenses/>.
 */

package pixelitor.history;

import pixelitor.Composition;
import pixelitor.tools.pen.AnchorPoint;
import pixelitor.tools.pen.ControlPoint;
import pixelitor.tools.util.DraggablePoint;
import pixelitor.utils.debug.DebugNode;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import java.awt.geom.Point2D;

/**
 * Represents the moving of a {@link DraggablePoint}.
 */
public class HandleMovedEdit extends PixelitorEdit {
    private final DraggablePoint handle;
    private final Point2D before;
    private final Point2D after;

    public HandleMovedEdit(String name, DraggablePoint handle, Point2D before, Composition comp) {
        super(name, comp);
        this.handle = handle;
        this.before = before;
        after = handle.getCoLocationCopy();

        assert before.getX() != after.getX() || before.getY() != after.getY();
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        moveHandleTo(before);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        moveHandleTo(after);
    }

    private void moveHandleTo(Point2D before) {
        handle.setLocation(before);

        if (handle instanceof AnchorPoint ap) {
            ap.getSubPath().getComp().pathChanged();
        } else if (handle instanceof ControlPoint cp) {
            cp.getAnchor().getSubPath().getComp().pathChanged();
        }

        comp.repaint();
    }

    @Override
    public DebugNode createDebugNode(String key) {
        DebugNode node = super.createDebugNode(key);

        node.add(handle.createDebugNode());
        node.addAsString("before", before);
        node.addAsString("after", after);

        return node;
    }
}
