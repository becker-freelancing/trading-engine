package com.becker.freelance.math;/*
 * Copyright (c) 2003, 2025, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 4108852
 * @summary A few tests of stripTrailingZeros
 * @run main com.becker.freelance.math.StrippingZerosTest
 * @run main/othervm -XX:+IgnoreUnrecognizedVMOptions -XX:+EliminateAutoBox -XX:AutoBoxCacheMax=20000 com.becker.freelance.math.StrippingZerosTest
 */



public class StrippingZerosTest {
    public static void main(String argv[]) {
        Decimal[][] testCases = {
                {new Decimal("1.00000"), new Decimal("1")},
                {new Decimal("1.000"), new Decimal("1")},
                {new Decimal("1"), new Decimal("1")},
                {new Decimal("0.1234"), new Decimal("0.1234")},
                {new Decimal("0.12340"), new Decimal("0.1234")},
                {new Decimal("0.12340000000"), new Decimal("0.1234")},
                {new Decimal("1234.5678"), new Decimal("1234.5678")},
                {new Decimal("1234.56780"), new Decimal("1234.5678")},
                {new Decimal("1234.567800000"), new Decimal("1234.5678")},
                {new Decimal("0"), new Decimal("0")},
                {new Decimal("0e2"), Decimal.ZERO},
                {new Decimal("0e-2"), Decimal.ZERO},
                {new Decimal("0e42"), Decimal.ZERO},
                {new Decimal("+0e42"), Decimal.ZERO},
                {new Decimal("-0e42"), Decimal.ZERO},
                {new Decimal("0e-42"), Decimal.ZERO},
                {new Decimal("+0e-42"), Decimal.ZERO},
                {new Decimal("-0e-42"), Decimal.ZERO},
                {new Decimal("0e-2"), Decimal.ZERO},
                {new Decimal("0e100"), Decimal.ZERO},
                {new Decimal("0e-100"), Decimal.ZERO},
                {new Decimal("10"), new Decimal("1e1")},
                {new Decimal("20"), new Decimal("2e1")},
                {new Decimal("100"), new Decimal("1e2")},
                {new Decimal("1000000000"), new Decimal("1e9")},
                {new Decimal("100000000e1"), new Decimal("1e9")},
                {new Decimal("10000000e2"), new Decimal("1e9")},
                {new Decimal("1000000e3"), new Decimal("1e9")},
                {new Decimal("100000e4"), new Decimal("1e9")},
                // BD value which larger than Long.MaxValue
                {new Decimal("1.0000000000000000000000000000"), new Decimal("1")},
                {new Decimal("-1.0000000000000000000000000000"), new Decimal("-1")},
                {new Decimal("1.00000000000000000000000000001"), new Decimal("1.00000000000000000000000000001")},
                {new Decimal("1000000000000000000000000000000e4"), new Decimal("1e34")},
        };

        for (int i = 0; i < testCases.length; i++) {

            if (!(testCases[i][0]).stripTrailingZeros().equals(testCases[i][1])) {
                throw new RuntimeException("For input " + testCases[i][0].toString() +
                        " did not received expected result " +
                        testCases[i][1].toString() + ",  got " +
                        testCases[i][0].stripTrailingZeros());
            }

            testCases[i][0] = testCases[i][0].negate();
            testCases[i][1] = testCases[i][1].negate();

            if (!(testCases[i][0]).stripTrailingZeros().equals(testCases[i][1])) {
                throw new RuntimeException("For input " + testCases[i][0].toString() +
                        " did not received expected result " +
                        testCases[i][1].toString() + ",  got " +
                        testCases[i][0].stripTrailingZeros());
            }

        }
    }
}
