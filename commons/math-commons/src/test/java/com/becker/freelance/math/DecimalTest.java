package com.becker.freelance.math;

import org.junit.jupiter.api.Test;

class DecimalTest {

    @Test
    void testDecimal() throws Exception {
        AddTests.main(null);
        CompareToTests.main(null);
        ConstructorUnscaledValue.main();
        DivideMcTests.main(null);
        DivideTests.main(null);
        DoubleFloatValueTests.main(null);
        EqualsTests.main(null);
        FloatDoubleValueTests.main(null);
        IntegralDivisionTests.main(null);
        IntegralValueTests.main();
        IntValueExactTests.main();
        LongValueExactTests.main();
        MovePointTests.main(null);
        MultiplyTests.main(null);
        NegateTests.main(null);
        PowTests.main(null);
        PrecisionTests.main(null);
        RangeTests.main(null);
        RoundingTests.main(null);
        ScaleByPowerOfTenTests.main(null);
        SquareRootTests.main();
        StringConstructor.main(null);
        StrippingZerosTest.main(null);
        ToPlainStringTests.main(null);
        ZeroScalingTests.main(null);
    }

}