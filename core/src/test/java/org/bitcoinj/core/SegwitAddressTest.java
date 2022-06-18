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

package org.bitcoinj.core;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.bitcoinj.params.BitcoinParams;
import org.bitcoinj.params.BitcoinTestNetParams;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.Script.ScriptType;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.junit.Test;

import com.google.common.base.MoreObjects;

public class SegwitAddressTest {
    private static final MainNetParams MAINNET = MainNetParams.get();
    private static final TestNet3Params TESTNET = TestNet3Params.get();

    private static final BitcoinParams BITCOIN = BitcoinParams.get();

    private static final BitcoinTestNetParams BITCOIN_TESTNET = BitcoinTestNetParams.get();

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SegwitAddress.class)
                .withPrefabValues(NetworkParameters.class, MAINNET, TESTNET)
                .suppress(Warning.NULL_FIELDS)
                .suppress(Warning.TRANSIENT_FIELDS)
                .usingGetClass()
                .verify();
    }

    @Test
    public void example_p2wpkh_mainnet() {
        String bech32 = "grs1qw508d6qejxtdg4y5r3zarvary0c5xw7k3k4sj5";

        SegwitAddress address = SegwitAddress.fromBech32(MAINNET, bech32);

        assertEquals(MAINNET, address.params);
        assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6",
                Utils.HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_mainnet() {
        String bech32 = "grs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3qhrkhr4";

        SegwitAddress address = SegwitAddress.fromBech32(MAINNET, bech32);

        assertEquals(MAINNET, address.params);
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262",
                Utils.HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        assertEquals(ScriptType.P2WSH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wpkh_testnet() {
        String bech32 = "tgrs1qy2lg7uqh56q6trvl2eh6qmrlxpzysank09q2xs";

        SegwitAddress address = SegwitAddress.fromBech32(TESTNET, bech32);

        assertEquals(TESTNET, address.params);
        assertEquals("001422be8f7017a681a58d9f566fa06c7f3044487676",
                Utils.HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_testnet() {
        String bech32 = "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3quvjfuq";

        SegwitAddress address = SegwitAddress.fromBech32(TESTNET, bech32);

        assertEquals(TESTNET, address.params);
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262",
                Utils.HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
        assertEquals(ScriptType.P2WSH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void validAddresses() {
        for (AddressData valid : VALID_ADDRESSES) {
            SegwitAddress address = SegwitAddress.fromBech32(null, valid.address);

            assertEquals(valid.expectedParams, address.params);
            assertEquals(valid.expectedScriptPubKey,
                    Utils.HEX.encode(ScriptBuilder.createOutputScript(address).getProgram()));
            assertEquals(valid.address.toLowerCase(Locale.ROOT), address.toBech32());
            if (valid.expectedWitnessVersion == 0) {
                Script expectedScriptPubKey = new Script(Utils.HEX.decode(valid.expectedScriptPubKey));
                assertEquals(address, SegwitAddress.fromHash(valid.expectedParams,
                        ScriptPattern.extractHashFromP2WH(expectedScriptPubKey)));
            }
            assertEquals(valid.expectedWitnessVersion, address.getWitnessVersion());
        }
    }

    private static class AddressData {
        final String address;
        final NetworkParameters expectedParams;
        final String expectedScriptPubKey;
        final int expectedWitnessVersion;

        AddressData(String address, NetworkParameters expectedParams, String expectedScriptPubKey,
                int expectedWitnessVersion) {
            this.address = address;
            this.expectedParams = expectedParams;
            this.expectedScriptPubKey = expectedScriptPubKey;
            this.expectedWitnessVersion = expectedWitnessVersion;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this).add("address", address).add("params", expectedParams.getId())
                    .add("scriptPubKey", expectedScriptPubKey).add("witnessVersion", expectedWitnessVersion).toString();
        }
    }

    private static AddressData[] VALID_ADDRESSES = {
            // from BIP350 (includes the corrected BIP173 vectors):
            new AddressData("GRS1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7K3K4SJ5", MAINNET,
                    "0014751e76e8199196d454941c45d1b3a323f1433bd6", 0),
            new AddressData("tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3quvjfuq", TESTNET,
                    "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", 0),
            new AddressData("grs1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k9rhu7x", MAINNET,
                    "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", 1),
            new AddressData("GRS1SW50QUA74JP", MAINNET, "6002751e", 16),
            new AddressData("grs1zw508d6qejxtdg4y5r3zarvaryv8vlr2l", MAINNET, "5210751e76e8199196d454941c45d1b3a323", 2),
            new AddressData("tgrs1qqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvsess668a6", TESTNET,
                    "0020000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", 0),
            new AddressData("tgrs1pqqqqp399et2xygdj5xreqhjjvcmzhxw4aywxecjdzew6hylgvses6d6w9x", TESTNET,
                    "5120000000c4a5cad46221b2a187905e5266362b99d5e91c6ce24d165dab93e86433", 1),
            new AddressData("grs1p0xlxvlhemja6c4dqv22uapctqupfhlxm9h8z3k2e72q4k9hcz7vqddt7at", MAINNET,
                    "512079be667ef9dcbbac55a06295ce870b07029bfcdb2dce28d959f2815b16f81798", 1),
    };

    @Test
    public void invalidAddresses() {
        for (String invalid : INVALID_ADDRESSES) {
            try {
                SegwitAddress.fromBech32(null, invalid);
                fail(invalid);
            } catch (AddressFormatException x) {
                // expected
            }
        }
    }

    private static String[] INVALID_ADDRESSES = {
            // from BIP173:
            "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty", // Invalid human-readable part
            "grs1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5", // Invalid checksum
            "GRS13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KWYN0ST", // Invalid witness version
            "grs1rw5v4pps9", // Invalid program length
            "grs10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5m5pmg5", // Invalid program length
            "GRS1QR508D6QEJXTDG4Y5R3ZARVARYVLD3SHW", // Invalid program length for witness version 0 (per BIP141)
            "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pp6XuPj", // Mixed case
            "grs1zw508d6qejxtdg4y5r3zarvaryvqktx56l", // Zero padding of more than 4 bits
            "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pp6xupj", // Non-zero padding in 8-to-5 conversion
            "grs1u6eyss", // Empty data section

            // from BIP350:
            "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty", // Invalid human-readable part
            "grs1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t5", // Invalid checksum
            "GRS13W508D6QEJXTDG4Y5R3ZARVARY0C5XW7KWYN0ST", // Invalid witness version
            "grs1rw5v4pps9", // Invalid program length
            "grs10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5m5pmg5", // Invalid program length
            "GRS1QR508D6QEJXTDG4Y5R3ZARVARYVLD3SHW", // Invalid program length for witness version 0 (per BIP141)
            "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pp6XuPj", // Mixed case
            "grs1zw508d6qejxtdg4y5r3zarvaryvqktx56l", // Zero padding of more than 4 bits
            "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3pp6xupj", // Non-zero padding in 8-to-5 conversion
            "grs1u6eyss", // Empty data section
    };

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32_version0_invalidLength() {
        SegwitAddress.fromBech32(null, "GRS1QR508D6QEJXTDG4Y5R3ZARVARYVLD3SHW");
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32_tooShort() {
        SegwitAddress.fromBech32(null, "grs1rw5v4pps9");
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32_tooLong() {
        SegwitAddress.fromBech32(null, "grs10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5m5pmg5");
    }

    @Test(expected = AddressFormatException.InvalidPrefix.class)
    public void fromBech32_invalidHrp() {
        SegwitAddress.fromBech32(null, "tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty");
    }

    @Test(expected = AddressFormatException.WrongNetwork.class)
    public void fromBech32_wrongNetwork() {
        SegwitAddress.fromBech32(TESTNET, "grs1qa752y23pu76une6pwdw2w3fu8euvwyv6a50np0");
    }

    @Test
    public void testJavaSerialization() throws Exception {
        SegwitAddress address = SegwitAddress.fromBech32(null, "GRS1SW50QUA74JP");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        new ObjectOutputStream(os).writeObject(address);
        PrefixedChecksummedBytes addressCopy = (PrefixedChecksummedBytes) new ObjectInputStream(
                new ByteArrayInputStream(os.toByteArray())).readObject();

        assertEquals(address, addressCopy);
        assertEquals(address.params, addressCopy.params);
        assertArrayEquals(address.bytes, addressCopy.bytes);
    }
}
