package com.becker.freelance.math;/*
 * Copyright (c) 2005, 2023, Oracle and/or its affiliates. All rights reserved.
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

/**
 * @test
 * @bug 6806261 8211936 8305343
 * @summary Tests of Decimal.longValueExact
 */


import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public class LongValueExactTests {
    public static void main(String... args) {
        int failures = 0;

        failures += longValueExactSuccessful();
        failures += longValueExactExceptional();
        failures += longValueExactExceptional8305343();

        if (failures > 0) {
            throw new RuntimeException("Incurred " + failures +
                    " failures while testing longValueExact.");
        }
    }

    private static long simpleLongValueExact(Decimal bd) {
        return bd.toBigIntegerExact().longValue();
    }

    private static int longValueExactSuccessful() {
        int failures = 0;

        // Strings used to create Decimal instances on which invoking
        // longValueExact() will succeed.
        Map<Decimal, Long> successCases =
                Map.ofEntries(entry(new Decimal("9223372036854775807"), Long.MAX_VALUE), // 2^63 -1
                        entry(new Decimal("9223372036854775807.0"), Long.MAX_VALUE),
                        entry(new Decimal("9223372036854775807.00"), Long.MAX_VALUE),

                        entry(new Decimal("-9223372036854775808"), Long.MIN_VALUE), // -2^63
                        entry(new Decimal("-9223372036854775808.0"), Long.MIN_VALUE),
                        entry(new Decimal("-9223372036854775808.00"), Long.MIN_VALUE),

                        entry(new Decimal("1e0"), 1L),
                        entry(new Decimal(BigInteger.ONE, -18), 1_000_000_000_000_000_000L),

                        entry(new Decimal("0e13"), 0L), // Fast path zero
                        entry(new Decimal("0e64"), 0L),
                        entry(new Decimal("0e1024"), 0L),

                        entry(new Decimal("10.000000000000000000000000000000000"), 10L));

        for (var testCase : successCases.entrySet()) {
            Decimal bd = testCase.getKey();
            long expected = testCase.getValue();
            try {
                long longValueExact = bd.longValueExact();
                if (expected != longValueExact ||
                        longValueExact != simpleLongValueExact(bd)) {
                    failures++;
                    System.err.println("Unexpected longValueExact result " + longValueExact +
                            " on " + bd);
                }
            } catch (Exception e) {
                failures++;
                System.err.println("Error on " + bd + "\tException message:" + e.getMessage());
            }
        }
        return failures;
    }

    private static int longValueExactExceptional() {
        int failures = 0;
        List<Decimal> exceptionalCases =
                List.of(new Decimal("9223372036854775808"), // Long.MAX_VALUE + 1
                        new Decimal("9223372036854775808.0"),
                        new Decimal("9223372036854775808.00"),
                        new Decimal("-9223372036854775809"), // Long.MIN_VALUE - 1
                        new Decimal("-9223372036854775808.1"),
                        new Decimal("-9223372036854775808.01"),

                        new Decimal("9999999999999999999"),
                        new Decimal("10000000000000000000"),

                        new Decimal("0.99"),
                        new Decimal("0.999999999999999999999"));

        for (Decimal bd : exceptionalCases) {
            try {
                long longValueExact = bd.longValueExact();
                failures++;
                System.err.println("Unexpected non-exceptional longValueExact on " + bd);
            } catch (ArithmeticException e) {
                // Success;
            }
        }
        return failures;
    }

    private static int longValueExactExceptional8305343() {
        int failures = 0;
        List<Decimal> exceptionalCases =
                List.of(new Decimal("1e" + (Integer.MAX_VALUE - 1)),
                        new Decimal("1e" + (Integer.MAX_VALUE))
                );

        for (Decimal bd : exceptionalCases) {
            try {
                bd.longValueExact();
                failures++;
                System.err.println("Unexpected non-exceptional longValueExact on " + bd);
            } catch (ArithmeticException e) {
                if (!e.getMessage().toLowerCase().contains("overflow")) {
                    failures++;
                    System.err.println("Unexpected non-exceptional longValueExact on " + bd);
                }
            }
        }
        return failures;
    }

}
