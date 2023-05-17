package io.github.nickm980.smallville;

import org.junit.Test;

import io.github.nickm980.smallville.math.SmallvilleMath;

import static org.junit.Assert.*;

public class DecayTest {

    @Test
    public void testDecay() {
	double original = 10.0;
	double changeInTime = 1.0;
	double expected = 0.1;
	double actual = SmallvilleMath.decay(original, changeInTime);
	assertEquals(expected, actual, 0.001);
    }
}