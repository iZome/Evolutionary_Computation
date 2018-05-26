import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EvolutionTest3 {

    @Test
    void testReverseStringOdd() {
        Evolution evolution = new Evolution();

        int [] orderArray = {1,2,3,4,5};
        int [] reverserArray = {1,4,3,2,5};

        int [] returnedArray = evolution.reverseSubArray(orderArray, 1, 3);

        assertArrayEquals(reverserArray, returnedArray);

    }

    @Test
    void testReverseStringEven() {
        Evolution evolution = new Evolution();

        int [] orderArray = {1,2,3,4,5,6};
        int [] reverserArray = {1,5,4,3,2,6};

        int [] returnedArray = evolution.reverseSubArray(orderArray, 1, 4);

        assertArrayEquals(reverserArray, returnedArray);

    }

}