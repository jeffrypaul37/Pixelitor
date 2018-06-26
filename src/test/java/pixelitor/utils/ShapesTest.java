/*
 * Copyright 2018 Laszlo Balazs-Csiki and Contributors
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

package pixelitor.utils;

import org.junit.Before;
import org.junit.Test;
import pixelitor.TestHelper;
import pixelitor.gui.ImageComponent;
import pixelitor.testutils.ShapeChecker;
import pixelitor.tools.pen.AnchorPoint;
import pixelitor.tools.pen.Path;
import pixelitor.tools.pen.SubPath;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import static pixelitor.assertions.PixelitorAssertions.assertThat;
import static pixelitor.tools.pen.AnchorPointType.SMOOTH;

public class ShapesTest {
    private ImageComponent ic;

    @Before
    public void setup() {
        ic = TestHelper.createICWithoutComp();
    }

    @Test
    public void convertRectangle() {
        Shape s = new Rectangle2D.Double(2, 2, 10, 10);
        checkRectangleShape(s);

        Path path = Shapes.shapeToPath(s, ic);
        checkRectanglePath(path.getActiveSubpath());

        s = path.toImageSpaceShape();
        checkRectangleShape(s);

        path = Shapes.shapeToPath(s, ic);
        checkRectanglePath(path.getActiveSubpath());
    }

    @Test
    public void convertEllipse() {
        Shape s = new Ellipse2D.Double(2, 2, 10, 10);
        checkEllipseShape(s);

        Path path = Shapes.shapeToPath(s, ic);
        checkEllipsePath(path.getActiveSubpath());

        s = path.toImageSpaceShape();
        checkEllipseShape(s);

        path = Shapes.shapeToPath(s, ic);
        checkEllipsePath(path.getActiveSubpath());
    }

    private void checkRectangleShape(Shape s) {
        ShapeChecker checker = new ShapeChecker(s);
        checker.assertNumMoveTosWas(1);
        checker.assertNumLineTosWas(4);
        checker.assertNumQuadTosWas(0);
        checker.assertNumCubicTosWas(0);
        checker.assertNumClosesWas(1);
    }

    private void checkRectanglePath(SubPath sp) {
        assertThat(sp)
                .numPointsIs(4)
                .isClosed();

        AnchorPoint p1 = sp.getPoint(0);
        assertThat(p1).locIs(2, 2);
        assertThat(p1.ctrlOut).isRetracted();
        assertThat(p1.ctrlIn).isRetracted();

        AnchorPoint p2 = sp.getPoint(1);
        assertThat(p2).locIs(12, 2);
        assertThat(p2.ctrlOut).isRetracted();
        assertThat(p2.ctrlIn).isRetracted();

        AnchorPoint p3 = sp.getPoint(2);
        assertThat(p3).locIs(12, 12);
        assertThat(p3.ctrlOut).isRetracted();
        assertThat(p3.ctrlIn).isRetracted();

        AnchorPoint p4 = sp.getPoint(3);
        assertThat(p4).locIs(2, 12);
        assertThat(p4.ctrlOut).isRetracted();
        assertThat(p4.ctrlIn).isRetracted();
    }

    private void checkEllipseShape(Shape s) {
        ShapeChecker checker = new ShapeChecker(s);
        checker.assertNumMoveTosWas(1);
        checker.assertNumLineTosWas(0);
        checker.assertNumQuadTosWas(0);
        checker.assertNumCubicTosWas(4);
        checker.assertNumClosesWas(1);
    }

    private void checkEllipsePath(SubPath sp) {
        assertThat(sp)
                .numPointsIs(4)
                .isClosed();

        AnchorPoint p1 = sp.getPoint(0);
        assertThat(p1)
                .locIs(12, 7)
                .anchorPointTypeIs(SMOOTH);
        assertThat(p1.ctrlOut).locIs(12, 9.76);
        assertThat(p1.ctrlIn).locIs(12, 4.24);

        AnchorPoint p2 = sp.getPoint(1);
        assertThat(p2).locIs(7, 12);
        assertThat(p2.ctrlOut).locIs(4.24, 12.00);
        assertThat(p2.ctrlIn).locIs(9.76, 12);

        AnchorPoint p3 = sp.getPoint(2);
        assertThat(p3).locIs(2, 7);
        assertThat(p3.ctrlOut).locIs(2, 4.24);
        assertThat(p3.ctrlIn).locIs(2, 9.76);

        AnchorPoint p4 = sp.getPoint(3);
        assertThat(p4).locIs(7, 2);
        assertThat(p4.ctrlOut).locIs(9.76, 2);
        assertThat(p4.ctrlIn).locIs(4.24, 2);
    }
}