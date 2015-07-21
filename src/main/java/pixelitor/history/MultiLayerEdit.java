/*
 * Copyright 2015 Laszlo Balazs-Csiki
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
import pixelitor.layers.ImageLayer;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import static pixelitor.Composition.ImageChangeActions.FULL;

/**
 * A PixelitorEdit representing an operation that can affect multiple layers,
 * such as resize, a crop, flip, or image rotation.
 * These are undoable only if the composition has a single image layer
 */
public class MultiLayerEdit extends PixelitorEdit {
    private ImageLayer layer;

    private final ImageEdit imageEdit;
    private final CanvasChangeEdit canvasChangeEdit;
    private SelectionChangeEdit selectionChangeEdit;
    private DeselectEdit deselectEdit;

    public MultiLayerEdit(Composition comp, String name, MultiLayerBackup backup) {
        super(comp, name);
        this.canvasChangeEdit = backup.getCanvasChangeEdit();

        int nrLayers = comp.getNrImageLayers();
        if (nrLayers == 1) {
            layer = comp.getAnyImageLayer();
            imageEdit = backup.createImageEdit();
        } else {
            imageEdit = null;
        }

        if (comp.hasSelection()) {
            assert backup.hasSavedSelection();
            selectionChangeEdit = backup.createSelectionChangeEdit();
        } else {
            if (backup.hasSavedSelection()) {
                // it was a deselect:
                // either a selection crop or a crop tool crop without
                // overlap with the existing selection.
                deselectEdit = backup.createDeselectEdit();
            }
        }
    }

//    public void setSelectionChangeEdit(SelectionChangeEdit selectionChangeEdit) {
//        this.selectionChangeEdit = selectionChangeEdit;
//    }

//    public void setDeselectEdit(DeselectEdit deselectEdit) {
//        this.deselectEdit = deselectEdit;
//    }

    @Override
    public boolean canUndo() {
        if (imageEdit == null) {
            return false;
        }
        return super.canUndo();
    }

    @Override
    public boolean canRedo() {
        if (imageEdit == null) {
            return false;
        }
        return super.canRedo();
    }

    @Override
    public boolean canRepeat() {
        return false;
    }

    @Override
    public void undo() throws CannotUndoException {
        super.undo();

        imageEdit.undo();
        if (canvasChangeEdit != null) {
            canvasChangeEdit.undo();
        }
        if (selectionChangeEdit != null) {
            selectionChangeEdit.undo();
        }
        if (deselectEdit != null) {
            deselectEdit.undo();
        }

        updateGUI();
    }

    @Override
    public void redo() throws CannotRedoException {
        super.redo();

        imageEdit.redo();
        if (canvasChangeEdit != null) {
            canvasChangeEdit.redo();
        }
        if (selectionChangeEdit != null) {
            selectionChangeEdit.redo();
        }
        if (deselectEdit != null) {
            deselectEdit.redo();
        }

        updateGUI();
    }

    private void updateGUI() {
        comp.imageChanged(FULL);
        layer.updateIconImage();
        if (imageEdit instanceof ImageAndMaskEdit) {
            layer.getMask().updateIconImage();
        }
        History.notifyMenus(this);
    }

    @Override
    public void die() {
        super.die();

        imageEdit.die();
        if (canvasChangeEdit != null) {
            canvasChangeEdit.die();
        }
        if (selectionChangeEdit != null) {
            selectionChangeEdit.die();
        }
        if (deselectEdit != null) {
            deselectEdit.die();
        }
    }
}