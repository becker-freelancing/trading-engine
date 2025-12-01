package com.becker.freelance.math;/*
 * Copyright (c) 2022, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test
 * @bug 8289260
 */



public class MovePointTests {

    public static void main(String argv[]) {
        Decimal bd;

        bd = Decimal.valueOf(1, -3);
        checkNotIdentical(bd, bd.movePointLeft(0));
        checkNotIdentical(bd, bd.movePointRight(0));

        bd = Decimal.valueOf(1, 0);
        checkIdentical(bd, bd.movePointLeft(0));
        checkIdentical(bd, bd.movePointRight(0));

        bd = Decimal.valueOf(1, 3);
        checkIdentical(bd, bd.movePointLeft(0));
        checkIdentical(bd, bd.movePointRight(0));

        bd = Decimal.valueOf(1, -3);
        checkNotEquals(bd, bd.movePointLeft(1));
        checkNotEquals(bd, bd.movePointLeft(-1));
        checkNotEquals(bd, bd.movePointRight(1));
        checkNotEquals(bd, bd.movePointRight(-1));

        bd = Decimal.valueOf(1, 0);
        checkNotEquals(bd, bd.movePointLeft(1));
        checkNotEquals(bd, bd.movePointLeft(-1));
        checkNotEquals(bd, bd.movePointRight(1));
        checkNotEquals(bd, bd.movePointRight(-1));

        bd = Decimal.valueOf(1, 3);
        checkNotEquals(bd, bd.movePointLeft(1));
        checkNotEquals(bd, bd.movePointLeft(-1));
        checkNotEquals(bd, bd.movePointRight(1));
        checkNotEquals(bd, bd.movePointRight(-1));

        bd = Decimal.valueOf(1, -3);
        checkNotEquals(bd, bd.movePointLeft(10));
        checkNotEquals(bd, bd.movePointLeft(-10));
        checkNotEquals(bd, bd.movePointRight(10));
        checkNotEquals(bd, bd.movePointRight(-10));

        bd = Decimal.valueOf(1, 0);
        checkNotEquals(bd, bd.movePointLeft(10));
        checkNotEquals(bd, bd.movePointLeft(-10));
        checkNotEquals(bd, bd.movePointRight(10));
        checkNotEquals(bd, bd.movePointRight(-10));

        bd = Decimal.valueOf(1, 3);
        checkNotEquals(bd, bd.movePointLeft(10));
        checkNotEquals(bd, bd.movePointLeft(-10));
        checkNotEquals(bd, bd.movePointRight(10));
        checkNotEquals(bd, bd.movePointRight(-10));
    }

    private static void checkIdentical(Decimal bd, Decimal res) {
        if (res != bd) {  // intentionally !=
            throw new RuntimeException("Unexpected result " +
                    bd + " != " + res);
        }
    }

    private static void checkNotIdentical(Decimal bd, Decimal res) {
        if (res == bd) {  // intentionally ==
            throw new RuntimeException("Unexpected result " +
                    bd + " == " + res);
        }
    }

    private static void checkNotEquals(Decimal bd, Decimal res) {
        if (res.equals(bd)) {
            throw new RuntimeException("Unexpected result " +
                    bd + ".equals(" + res + ")");
        }
    }

}
