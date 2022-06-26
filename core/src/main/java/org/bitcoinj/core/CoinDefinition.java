package org.bitcoinj.core;

import org.bitcoinj.net.discovery.HttpDiscovery;

import java.math.BigInteger;
import java.net.URI;
import java.util.Map;

public class CoinDefinition {

    public static final String coinName = "Groestlcoin";
    public static final String coinTicker = "GRS";
    public static final String coinURIScheme = "groestlcoin";
    public static final String PATTERN_PRIVATE_KEY_START = "[56]";
    public static final String PATTERN_PRIVATE_KEY_START_COMPRESSED = "[KL]";
    public static final String PATTERN_PRIVATE_KEY_START_TESTNET = "9";
    public static final String PATTERN_PRIVATE_KEY_START_COMPRESSED_TESTNET = "c";

    public static String lowerCaseCoinName() { return coinName.toLowerCase(); }

    public static final String DONATION_ADDRESS = "FkknEYnex1MeZyPRnEebFK5ZBHHsFZbvaf";  //HashEngineering donation GRS address

    public static boolean checkpointFileSupport = true;
    public static int checkpointDaysBack = 21;

    public static final int TARGET_TIMESPAN = 1 * 24 * 60 * 60;  // 1 day per difficulty cycle, on average.
    public static final int TARGET_SPACING = 60;  // 60 seconds per block.
    public static final int INTERVAL = TARGET_TIMESPAN / TARGET_SPACING;  //108 blocks

    public static final int getIntervalCheckpoints() {
            return 2016;    //1080

    }
    public static int spendableCoinbaseDepth = 120; //main.h: static const int CINBASE_MATURITY
    public static final int MAX_COINS = 105000000;                 //main.h:  MAX_MONEY
    public static final Coin DEFAULT_MIN_TX_FEE = Coin.valueOf(10000);   // MIN_TX_FEE
    public static final Coin DUST_LIMIT = Coin.valueOf(10000); //main.h CTransaction::GetMinFee        0.01 coins
    public static final int PROTOCOL_VERSION = 70013;          //version.h PROTOCOL_VERSION
    public static final int MIN_PROTOCOL_VERSION = 31800;        //version.h MIN_PROTO_VERSION
    public static final int INIT_PROTO_VERSION = 209;            //version.h
    public static final int BLOCK_CURRENTVERSION = 112;   //CBlock::CURRENT_VERSION
    public static final int MAX_BLOCK_SIZE = 1 * 1000 * 1000;

    public static final boolean supportsBloomFiltering = true; //Requires PROTOCOL_VERSION 70000 in the client
    public static boolean supportsIrcDiscovery() {
        return PROTOCOL_VERSION <= 70000;
    }

    public static final int Port    = 1331;       //protocol.h GetDefaultPort(testnet=false)
    public static final int TestPort = 17777;     //protocol.h GetDefaultPort(testnet=true)

    //
    //  Production
    //
    public static final int AddressHeader = 36;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS
    public static final int p2shHeader = 5;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS
    public static final long PacketMagic = 0xf9beb4d4;      //0xf9, 0xbe, 0xb4, 0xd4

    //Genesis Block Information from main.cpp: LoadBlockIndex
    static public long genesisBlockDifficultyTarget = (0x1e0fffffL);         //main.cpp: LoadBlockIndex
    static public long genesisBlockTime = 1395342829L;                       //main.cpp: LoadBlockIndex
    static public long genesisBlockNonce = (220035);                         //main.cpp: LoadBlockIndex
    static public String genesisHash = "00000ac5927c594d49cc0bdb81759d0da8297eb614683d3acb62f0703b639023"; //main.cpp: hashGenesisBlock
    static public int genesisBlockValue = 0;
    static public int genesisBlockVersion = 112; //main.cpp: LoadBlockIndex
    //taken from the raw data of the block explorer

    // "Pressure must be put on Vladimir Putin over Crimea"
    static public String genesisTxInBytes = "04ffff001d0104325072657373757265206d75737420626520707574206f6e20566c6164696d697220507574696e206f766572204372696d6561";
    static public String genesisTxOutBytes = "04678afdb0fe5548271967f1a67130b7105cd6a828e03909a67962e0ea1f61deb649f6bc3f4cef38c4f35504e51ec112de5c384df7ba0b8d578a4c702b6bf11d5f";
    static public String genesisMerkleRoot = "3ce968df58f9c8a752306c4b7264afab93149dbc578bd08a42c446caaa6628bb";

    //net.cpp strDNSSeed
    static public String[] dnsSeeds = new String[] {
            "dnsseed1.groestlcoin.org",
            "dnsseed2.groestlcoin.org",
            "dnsseed3.groestlcoin.org",
            "dnsseed4.groestlcoin.org",
    };

    public static final HttpDiscovery.Details[] httpSeeds = new HttpDiscovery.Details[] {
            new HttpDiscovery.Details(
                    ECKey.fromPublicOnly(Utils.HEX.decode("0248876142c407e9a05a07f96caf212eb5b54b68845ddee44739094b02e24d13e4")),
                    URI.create("http://groestlcoin.org:8080/peers")
            )
    };

    public static final int[] addrSeeds = new int[] {
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
            0x5F85782F};

    public static int minBroadcastConnections = 1;   //0 for default; we need more peers.

    //
    // TestNet
    //
    public static final boolean supportsTestNet = true;
    public static final int testnetAddressHeader = 111;             //base58.h CBitcoinAddress::PUBKEY_ADDRESS_TEST
    public static final int testnetp2shHeader = 196;             //base58.h CBitcoinAddress::SCRIPT_ADDRESS_TEST
    public static final long testnetPacketMagic = 0x0b110907;      //0xfc, 0xc1, 0xb7, 0xdc
    public static final String testnetGenesisHash = "000000ffbb50fc9898cdd36ec163e6ba23230164c0052a28876255b7dcf2cd36";
    static public long testnetGenesisBlockDifficultyTarget = (0x1e00ffffL);         //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockTime = 1440000002L;                       //main.cpp: LoadBlockIndex
    static public long testnetGenesisBlockNonce = (6556309);                         //main.cpp: LoadBlockIndex

    //main.cpp GetBlockValue(height, fee)
    public static final Coin GetBlockReward(int height)
    {
        int COIN = 1;
        Coin nSubsidy = Coin.valueOf(15, 0);
        return nSubsidy.shiftRight(height / subsidyDecreaseBlockCount);
    }
    static final Coin nGenesisBlockRewardCoin = Coin.valueOf(1,0);
    static final Coin minimumSubsidy = Coin.valueOf(5,0);
    static final Coin nPremine = Coin.valueOf(240640,0);

    public static Coin GetBlockSubsidy(int nHeight){

        if (nHeight == 0)
        {
            return nGenesisBlockRewardCoin;
        }

        if (nHeight == 1)
        {
            return nPremine;
        }

        Coin nSubsidy = Coin.valueOf(512);

        // Subsidy is reduced by 6% every 10080 blocks, which will occur approximately every 1 week
        int exponent=(nHeight / 10080);
        for(int i=0;i<exponent;i++){
            nSubsidy=nSubsidy.multiply(47);
            nSubsidy=nSubsidy.divide(50);
        }
        if(nSubsidy.compareTo(minimumSubsidy) < 0) {nSubsidy=minimumSubsidy;}
        return nSubsidy;
    }
    Coin GetBlockSubsidy120000(int nHeight)
    {
        // Subsidy is reduced by 10% every day (1440 blocks)
        Coin nSubsidy = Coin.valueOf(250,0);
        int exponent = ((nHeight - 120000) / 1440);
        for(int i=0; i<exponent; i++)
            nSubsidy = nSubsidy.multiply(45).divide(50);

        return nSubsidy;
    }

    Coin GetBlockSubsidy150000(int nHeight)
    {
        // Subsidy is reduced by 1% every week (10080 blocks)
        Coin nSubsidy = Coin.valueOf(25,0);
        int exponent = ((nHeight - 150000) / 10080);
        for(int i=0; i<exponent; i++)
            nSubsidy = (nSubsidy.multiply(99).divide(100));

        return nSubsidy.compareTo(minimumSubsidy) < 0 ? minimumSubsidy : nSubsidy;
    }

    public Coin GetBlockValue(int nHeight)
    {
        if(nHeight >= 150000)
            return GetBlockSubsidy150000(nHeight);

        if(nHeight >= 120000)
            return GetBlockSubsidy120000(nHeight);

        return GetBlockSubsidy(nHeight);
    }

    public static int subsidyDecreaseBlockCount = 4730400;     //main.cpp GetBlockValue(height, fee)

    public static BigInteger proofOfWorkLimit = Utils.decodeCompactBits(0x1e0fffffL);  //main.cpp bnProofOfWorkLimit (~uint256(0) >> 20); // groestlcoin: starting difficulty is 1 / 2^12

    static public String[] testnetDnsSeeds = new String[] {
            "testnet-seed1.groestlcoin.org",
            "testnet-seed2.groestlcoin.org",
    };

    /** The string returned by getId() for the main, production network where people trade things. */
    public static final String ID_MAINNET = "org.groestlcoin.production";
    /** The string returned by getId() for the testnet. */
    public static final String ID_TESTNET = "org.groestlcoin.test";
    /** Unit test network. */
    public static final String ID_UNITTESTNET = "com.google.groestlcoin.unittest";

    //checkpoints.cpp Checkpoints::mapCheckpoints
    public static void initCheckpoints(Map<Integer, Sha256Hash> checkpoints)
    {
        checkpoints.put( 0, Sha256Hash.wrap(CoinDefinition.genesisHash));
        checkpoints.put( 28888, Sha256Hash.wrap("00000000000228ce19f55cf0c45e04c7aa5a6a873ed23902b3654c3c49884502"));
        checkpoints.put( 58888, Sha256Hash.wrap("0000000000dd85f4d5471febeb174a3f3f1598ab0af6616e9f266b56272274ef"));
        checkpoints.put(111111, Sha256Hash.wrap("00000000013de206275ee83f93bee57622335e422acbf126a37020484c6e113c"));
        checkpoints.put(1000000, Sha256Hash.wrap("000000000df8560f2612d5f28b52ed1cf81b0f87ac0c9c7242cbcf721ca6854a"));
        checkpoints.put(2000000, Sha256Hash.wrap("00000000000434d5b8d1c3308df7b6e3fd773657dfb28f5dd2f70854ef94cc66"));
        checkpoints.put(2372000, Sha256Hash.wrap("000000000000117a4710e01e4f86d883ca491b96efa0b4f2139c4d49a9437f10"));
        checkpoints.put(2785000, Sha256Hash.wrap("00000000000013811b5078b06f3b98aaad29b94f09d047144e473de35f481474"));
    }

    //Unit Test Information
    public static final String UNITTEST_ADDRESS = "FmpNNw388tMqsDkKW6KfyksRkCVWqjBSCe";
    public static final String UNITTEST_ADDRESS_PRIVATE_KEY = "QU1rjHbrdJonVUgjT7Mncw7PEyPv3fMPvaGXp9EHDs1uzdJ98hUZ";

}
