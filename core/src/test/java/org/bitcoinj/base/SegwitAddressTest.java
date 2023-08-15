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

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.bitcoinj.base.exceptions.AddressFormatException;
import org.bitcoinj.base.internal.ByteUtils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.script.ScriptPattern;
import org.junit.Test;

import java.util.Locale;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.bitcoinj.base.BitcoinNetwork.MAINNET;
import static org.bitcoinj.base.BitcoinNetwork.TESTNET;
import static org.bitcoinj.base.BitcoinNetwork.SIGNET;
import static org.bitcoinj.base.BitcoinNetwork.REGTEST;

public class SegwitAddressTest {
    private static final AddressParser addressParser = AddressParser.getDefault();

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SegwitAddress.class)
                .withPrefabValues(BitcoinNetwork.class, MAINNET, TESTNET)
                .suppress(Warning.NULL_FIELDS)
                .suppress(Warning.TRANSIENT_FIELDS)
                .usingGetClass()
                .verify();
    }

    @Test
    public void example_p2wpkh_mainnet() {
        String bech32 = "grs1qw508d6qejxtdg4y5r3zarvary0c5xw7k3k4sj5";

        SegwitAddress address = SegwitAddress.fromBech32(bech32, MAINNET);

        assertEquals(MAINNET, address.network());
        assertEquals("0014751e76e8199196d454941c45d1b3a323f1433bd6",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_mainnet() {
        String bech32 = "grs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3qhrkhr4";

        SegwitAddress address = SegwitAddress.fromBech32(bech32, MAINNET);

        assertEquals(MAINNET, address.network());
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WSH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wpkh_testnet() {
        String bech32 = "tgrs1qy2lg7uqh56q6trvl2eh6qmrlxpzysank09q2xs";

        SegwitAddress address = SegwitAddress.fromBech32(bech32, TESTNET);

        assertEquals(TESTNET, address.network());
        assertEquals("001422be8f7017a681a58d9f566fa06c7f3044487676",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void equalityOfEquivalentNetworks() {
        String bech32 = "tgrs1qe6lqwswx02mdejyschk9v89hhutkvy6w5t7rkdckshezptuys65q3ku4l4";

        SegwitAddress a = SegwitAddress.fromBech32(bech32, TESTNET);
        SegwitAddress b = SegwitAddress.fromBech32(bech32, SIGNET);

        assertEquals(a, b);
        assertEquals(a.toString(), b.toString());
    }

    @Test
    public void example_p2wpkh_regtest() {
        String bcrt1_bech32 = "grsrt1qspfueag7fvty7m8htuzare3xs898zvh3fr8z65";

        SegwitAddress address = SegwitAddress.fromBech32(bcrt1_bech32, REGTEST);

        assertEquals(REGTEST, address.network());
        assertEquals("00148053ccf51e4b164f6cf75f05d1e62681ca7132f1",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bcrt1_bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bcrt1_bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wpkh_regtest_any_network() {
        String bcrt1_bech32 = "grsrt1qspfueag7fvty7m8htuzare3xs898zvh3fr8z65";

        Address address = addressParser.parseAddress(bcrt1_bech32);

        assertEquals(REGTEST, address.network());
        assertEquals("00148053ccf51e4b164f6cf75f05d1e62681ca7132f1",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WPKH, address.getOutputScriptType());
        assertEquals(bcrt1_bech32.toLowerCase(Locale.ROOT), ((SegwitAddress)address).toBech32());
        assertEquals(bcrt1_bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void example_p2wsh_testnet() {
        String bech32 = "tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3quvjfuq";

        SegwitAddress address = SegwitAddress.fromBech32(bech32, TESTNET);

        assertEquals(TESTNET, address.network());
        assertEquals("00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262",
                ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
        assertEquals(ScriptType.P2WSH, address.getOutputScriptType());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toBech32());
        assertEquals(bech32.toLowerCase(Locale.ROOT), address.toString());
    }

    @Test
    public void validAddresses() {
        for (AddressData valid : VALID_ADDRESSES) {
            SegwitAddress address = (SegwitAddress) addressParser.parseAddress(valid.address);

            assertEquals(valid.expectedNetwork, address.network());
            assertEquals(valid.expectedScriptPubKey,
                    ByteUtils.formatHex(ScriptBuilder.createOutputScript(address).program()));
            assertEquals(valid.address.toLowerCase(Locale.ROOT), address.toBech32());
            if (valid.expectedWitnessVersion == 0) {
                Script expectedScriptPubKey = Script.parse(ByteUtils.parseHex(valid.expectedScriptPubKey));
                assertEquals(address, SegwitAddress.fromHash(valid.expectedNetwork,
                        ScriptPattern.extractHashFromP2WH(expectedScriptPubKey)));
            }
            assertEquals(valid.expectedWitnessVersion, address.getWitnessVersion());
        }
    }

    private static class AddressData {
        final String address;
        final BitcoinNetwork expectedNetwork;
        final String expectedScriptPubKey;
        final int expectedWitnessVersion;

        AddressData(String address, BitcoinNetwork expectedNetwork, String expectedScriptPubKey,
                int expectedWitnessVersion) {
            this.address = address;
            this.expectedNetwork = expectedNetwork;
            this.expectedScriptPubKey = expectedScriptPubKey;
            this.expectedWitnessVersion = expectedWitnessVersion;
        }

        @Override
        public String toString() {
            StringBuilder s = new StringBuilder(this.getClass().getSimpleName()).append('{');
            s.append("address=").append(address).append(',');
            s.append("expected=").append(expectedNetwork.id()).append(',').append(expectedScriptPubKey).append(',').append(expectedWitnessVersion);
            return s.append('}').toString();
        }
    }

    private static AddressData[] VALID_ADDRESSES = {
            // from BIP350 (includes the corrected BIP173 vectors):
            new AddressData("GRS1QW508D6QEJXTDG4Y5R3ZARVARY0C5XW7K3K4SJ5", MAINNET,
                    "0014751e76e8199196d454941c45d1b3a323f1433bd6", 0),
            new AddressData("tgrs1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3quvjfuq", TESTNET,
                    "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262", 0),
            //new AddressData("grs1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k9rhu7x", MAINNET,
            //        "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6", 1),
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
                addressParser.parseAddress(invalid);
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
        addressParser.parseAddress("GRS1QR508D6QEJXTDG4Y5R3ZARVARYVLD3SHW");
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32_tooShort() {
        addressParser.parseAddress("grs1rw5v4pps9");
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32_tooLong() {
        addressParser.parseAddress("grs10w508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7kw5m5pmg5");
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32m_taprootTooShort() {
        // Taproot, valid bech32m encoding, checksum ok, padding ok, but no valid Segwit v1 program
        // (this program is 20 bytes long, but only 32 bytes program length are valid for Segwit v1/Taproot)
        String taprootAddressWith20BytesWitnessProgram = "grs1pzhggmtnpye59jl4nzlvqlc43a0vzuay4ce0v08";
        SegwitAddress.fromBech32(taprootAddressWith20BytesWitnessProgram, MAINNET);
    }

    @Test(expected = AddressFormatException.InvalidDataLength.class)
    public void fromBech32m_taprootTooLong() {
        // Taproot, valid bech32m encoding, checksum ok, padding ok, but no valid Segwit v1 program
        // (this program is 40 bytes long, but only 32 bytes program length are valid for Segwit v1/Taproot)
        String taprootAddressWith40BytesWitnessProgram = "grs1pagn2yuwrjac022u6ysu4v82k3d7z8pzdystynrrfynvf5dls95wrslfmshfdga2altjnn2";
        SegwitAddress.fromBech32(taprootAddressWith40BytesWitnessProgram, MAINNET);
    }

    @Test(expected = AddressFormatException.InvalidPrefix.class)
    public void fromBech32_invalidHrp() {
        addressParser.parseAddress("tc1qw508d6qejxtdg4y5r3zarvary0c5xw7kg3g4ty");
    }

    @Test(expected = AddressFormatException.WrongNetwork.class)
    public void fromBech32_wrongNetwork() {
        SegwitAddress.fromBech32("grs1qa752y23pu76une6pwdw2w3fu8euvwyv6a50np0", TESTNET);
    }
}
