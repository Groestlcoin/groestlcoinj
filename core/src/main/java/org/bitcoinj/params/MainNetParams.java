/*
 * Copyright 2013 Google Inc.
 * Copyright 2015 Andreas Schildbach
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

package org.bitcoinj.params;

import java.net.URI;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Utils;
import org.bitcoinj.net.discovery.HttpDiscovery;

import static com.google.common.base.Preconditions.checkState;

/**
 * Parameters for the main production network on which people trade goods and services.
 */
public class MainNetParams extends AbstractBitcoinNetParams {
    public static final int MAINNET_MAJORITY_WINDOW = 2016;
    public static final int MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED = 1912;
    public static final int MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE = 750;
    private static final long GENESIS_TIME = 1395342829L;
    private static final long GENESIS_NONCE = 220035;
    private static final Sha256Hash GENESIS_HASH = Sha256Hash.wrap("00000ac5927c594d49cc0bdb81759d0da8297eb614683d3acb62f0703b639023");

    public MainNetParams() {
        super();
        id = ID_MAINNET;

        targetTimespan = TARGET_TIMESPAN;
        maxTarget = Utils.decodeCompactBits(Block.STANDARD_MAX_DIFFICULTY_TARGET);

        port = 1331;
        packetMagic = 0xf9beb4d4;
        maxTarget = Utils.decodeCompactBits(0x1e0fffffL);
        dumpedPrivateKeyHeader = 128;
        addressHeader = 36;
        p2shHeader = 5;
        segwitAddressHrp = "grs";
        spendableCoinbaseDepth = 100;
        bip32HeaderP2PKHpub = 0x0488b21e; // The 4 byte header that serializes in base58 to "xpub".
        bip32HeaderP2PKHpriv = 0x0488ade4; // The 4 byte header that serializes in base58 to "xprv"
        bip32HeaderP2WPKHpub = 0x04b24746; // The 4 byte header that serializes in base58 to "zpub".
        bip32HeaderP2WPKHpriv = 0x04b2430c; // The 4 byte header that serializes in base58 to "zprv"

        majorityEnforceBlockUpgrade = MAINNET_MAJORITY_ENFORCE_BLOCK_UPGRADE;
        majorityRejectBlockOutdated = MAINNET_MAJORITY_REJECT_BLOCK_OUTDATED;
        majorityWindow = MAINNET_MAJORITY_WINDOW;

        dnsSeeds = new String[] {
                "dnsseed1.groestlcoin.org",
                "dnsseed2.groestlcoin.org",
                "dnsseed3.groestlcoin.org",
                "dnsseed4.groestlcoin.org",
        };
        httpSeeds = new HttpDiscovery.Details[] {
                new HttpDiscovery.Details(
                        ECKey.fromPublicOnly(Utils.HEX.decode("0248876142c407e9a05a07f96caf212eb5b54b68845ddee44739094b02e24d13e4")),
                        URI.create("http://groestlcoin.org:8080/peers")
                )
        };

        addrSeeds = new int[] {
                0x68EC82DE,
                0x68EC85C4,
                0x68ECB2F5,
                0x904CEF42,
                0x95D2E231,
                0xBC8648D5,
                0xC06306CF,
                0xC18862B8,
                0xC6C7692B,
                0xD2BA3B44,
                0x1715CC22,
                0x253B180F,
                0x2A738931,
                0x05092709,
                0x36DF4CA5,
                0x482E98FA,
                0x4C707B0D,
                0x535418B4,
                0x5402225E,
                0x5762B9F4,
                0x5E17217A,
                0x5E1737D3,
                0x5F85745C,
                0x5F85782F
        };
    }

    private static MainNetParams instance;
    public static synchronized MainNetParams get() {
        if (instance == null) {
            instance = new MainNetParams();
        }
        return instance;
    }

    @Override
    public Block getGenesisBlock() {
        synchronized (GENESIS_HASH) {
            if (genesisBlock == null) {
                genesisBlock = Block.createGenesis(this);
                genesisBlock.setDifficultyTarget(Block.STANDARD_MAX_DIFFICULTY_TARGET);
                genesisBlock.setTime(GENESIS_TIME);
                genesisBlock.setNonce(GENESIS_NONCE);
                checkState(genesisBlock.getHash().equals(GENESIS_HASH), "Invalid genesis hash");
            }
        }
        return genesisBlock;
    }

    @Override
    public String getPaymentProtocolId() {
        return PAYMENT_PROTOCOL_ID_MAINNET;
    }
}
