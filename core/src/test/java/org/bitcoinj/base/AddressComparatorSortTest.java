/*
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bitcoinj.base;

import org.bitcoinj.base.internal.StreamUtils;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

/**
 * Test sorting of {@link Address} (both {{@link LegacyAddress} and {@link SegwitAddress}}) with
 * the default comparators.
 */
public class AddressComparatorSortTest {
    private static final AddressParser addressParser = AddressParser.getDefault();

    /**
     * A manually sorted list of address for verifying sorting with our default comparator.
     * See {@link Address#compareTo}.
     */
    private static final List<Address> correctlySortedAddresses = Stream.of(
                    // Main net, Legacy
                    "FhyaAVWRzJDADnwGatPXzXzLEwNKszgS8Z",
                    "FihWfpc6qScLBAW6Crx2Wz5ZKGPwbvZQQa",
                    // Main net, Segwit
                    "grs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3qhrkhr4",
                    "grs1qw508d6qejxtdg4y5r3zarvary0c5xw7k3k4sj5",
                    // Test net, Legacy
                    "mpexoDuSkGGqvqrkrjiFng38QPkJRBy8FN",
                    "n1nc7b7hMm8snE6uaCuLK1EXtdXshmasaP",
                    // Test net, Segwit
                    "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3quvjfuq",
                    "tgrs1qy2lg7uqh56q6trvl2eh6qmrlxpzysank09q2xs"
            ).map(addressParser::parseAddress)
            .collect(StreamUtils.toUnmodifiableList());

    @Test
    public void testAddressComparisonSortOrder() {
        // Shuffle the list and then sort with the built-in comparator
        List<Address> shuffled = shuffled(correctlySortedAddresses);    // Shuffled copy
        List<Address> sortedAfterShuffle = sorted(shuffled);            // Sorted copy of shuffled copy

        assertEquals(correctlySortedAddresses, sortedAfterShuffle);
    }

    // shuffle an immutable list producing a new immutable list
    private static List<Address> shuffled(List<Address> addresses) {
        List<Address> shuffled = new ArrayList<>(addresses);            // Make modifiable copy
        Collections.shuffle(shuffled);                                  // shuffle it
        return Collections.unmodifiableList(shuffled);                  // Return unmodifiable view
    }

    // sort an immutable list producing a new immutable list
    private static List<Address> sorted(List<Address> addresses) {
        return addresses.stream()                                       // stream it
                .sorted()                                               // sort it
                .collect(StreamUtils.toUnmodifiableList());
    }
}
