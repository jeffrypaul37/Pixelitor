/*
 * Copyright 2023 Laszlo Balazs-Csiki and Contributors
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
import pixelitor.GUIMode;
import pixelitor.layers.Layer;
import pixelitor.layers.LayerMask;
import pixelitor.layers.MaskViewMode;
import pixelitor.utils.debug.DebugNode;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

/**
 * A PixelitorEdit that represents the deletion of a layer mask
 */
public class DeleteLayerMaskEdit extends PixelitorEdit {
    private final LayerMask oldMask;
    private final Layer layer;
    private final MaskViewMode oldMode;

    public DeleteLayerMaskEdit(Composition comp, Layer layer, LayerMask oldMask, MaskViewMode oldMode) {
        super("Delete Layer Mask", comp);
        this.oldMode = oldMode;

        assert layer.isActive() || GUIMode.isUnitTesting();
        this.layer = layer;
        this.oldMask = oldMask;
    }

    @Override
    public void undo() throws CannotUndoException {
        assert layer.isActive() || GUIMode.isUnitTesting();

        super.undo();

        layer.addConfiguredMask(oldMask);
        oldMode.activate(comp, layer);
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        layer.deleteMask(false);
    }

    @Override
    public DebugNode createDebugNode(String key) {
        DebugNode node = super.createDebugNode(key);

        node.add(layer.createDebugNode());
        node.add(oldMask.createDebugNode("old mask"));
        node.addAsString("old mode", oldMode);

        return node;
    }
}