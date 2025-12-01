package com.becker.freelance.math;/*
 * Copyright (c) 2005, 2025, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 6325535
 * @summary Test for the rounding behavior of negate(MathContext)
 */


import java.math.MathContext;
import java.math.RoundingMode;

public class NegateTests {

    static Decimal negateThenRound(Decimal bd, MathContext mc) {
        return bd.negate().plus(mc);
    }


    static Decimal absThenRound(Decimal bd, MathContext mc) {
        return bd.abs().plus(mc);
    }


    static int negateTest(Decimal[][] testCases, MathContext mc) {
        int failures = 0;

        for (Decimal[] testCase : testCases) {

            Decimal bd = testCase[0];
            Decimal neg1 = bd.negate(mc);
            Decimal neg2 = negateThenRound(bd, mc);
            Decimal expected = testCase[1];

            if (!neg1.equals(expected)) {
                failures++;
                System.err.println("(" + bd + ").negate(" + mc + ") => " +
                        neg1 + " != expected " + expected);
            }

            if (!neg1.equals(neg2)) {
                failures++;
                System.err.println("(" + bd + ").negate(" + mc + ")  => " +
                        neg1 + " != ntr " + neg2);
            }

            // Test abs consistency
            Decimal abs = bd.abs(mc);
            Decimal expectedAbs = absThenRound(bd, mc);
            if (!abs.equals(expectedAbs)) {
                failures++;
                System.err.println("(" + bd + ").abs(" + mc + ")  => " +
                        abs + " != atr " + expectedAbs);
            }

        }

        return failures;
    }

    static int negateTests() {
        int failures = 0;
        Decimal[][] testCasesCeiling = {
                {new Decimal("1.3"), new Decimal("-1")},
                {new Decimal("-1.3"), new Decimal("2")},
        };

        failures += negateTest(testCasesCeiling,
                new MathContext(1, RoundingMode.CEILING));

        Decimal[][] testCasesFloor = {
                {new Decimal("1.3"), new Decimal("-2")},
                {new Decimal("-1.3"), new Decimal("1")},
        };

        failures += negateTest(testCasesFloor,
                new MathContext(1, RoundingMode.FLOOR));

        return failures;
    }

    public static void main(String argv[]) {
        int failures = 0;

        failures += negateTests();

        if (failures > 0)
            throw new RuntimeException("Incurred " + failures + " failures" +
                    " testing the negate and/or abs.");
    }
}
