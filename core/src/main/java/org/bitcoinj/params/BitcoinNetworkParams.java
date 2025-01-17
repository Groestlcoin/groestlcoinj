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

import org.bitcoinj.base.BitcoinNetwork;
import org.bitcoinj.base.internal.Stopwatch;
import org.bitcoinj.base.internal.TimeUtils;
import org.bitcoinj.base.internal.ByteUtils;
import org.bitcoinj.core.BitcoinSerializer;
import org.bitcoinj.core.Block;
import org.bitcoinj.base.Coin;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.core.ProtocolVersion;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.core.VerificationException;
import org.bitcoinj.protocols.payments.PaymentProtocol;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.base.utils.MonetaryFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;

import static org.bitcoinj.base.internal.Preconditions.checkState;

/**
 * Parameters for Bitcoin-like networks.
 */
public abstract class BitcoinNetworkParams extends NetworkParameters {

    /**
     * Scheme part for Bitcoin URIs.
     * @deprecated Use {@link BitcoinNetwork#BITCOIN_SCHEME}
     */
    @Deprecated
    public static final String BITCOIN_SCHEME = BitcoinNetwork.BITCOIN_SCHEME;

    /**
     * Block reward halving interval (number of blocks)
     */
    public static final int REWARD_HALVING_INTERVAL = 10080;

    private static final Logger log = LoggerFactory.getLogger(BitcoinNetworkParams.class);

    /** lazy-initialized by the first call to {@link NetworkParameters#getGenesisBlock()} */
    protected Block genesisBlock;

    /**
     * No-args constructor
     */
    public BitcoinNetworkParams(BitcoinNetwork network) {
        super(network);
        interval = INTERVAL;
        subsidyDecreaseBlockCount = REWARD_HALVING_INTERVAL;
    }

    /**
     * Return network parameters for a network id
     * @param id the network id
     * @return the network parameters for the given string ID or NULL if not recognized
     */
    @Nullable
    public static BitcoinNetworkParams fromID(String id) {
        if (id.equals(BitcoinNetwork.ID_MAINNET)) {
            return MainNetParams.get();
        } else if (id.equals(BitcoinNetwork.ID_TESTNET)) {
            return TestNet3Params.get();
        } else if (id.equals(BitcoinNetwork.ID_SIGNET)) {
            return SigNetParams.get();
        } else if (id.equals(BitcoinNetwork.ID_REGTEST)) {
            return RegTestParams.get();
        } else {
            return null;
        }
    }

    /**
     * Return network parameters for a {@link BitcoinNetwork} enum
     * @param network the network
     * @return the network parameters for the given string ID
     * @throws IllegalArgumentException if unknown network
     */
    public static BitcoinNetworkParams of(BitcoinNetwork network) {
        switch (network) {
            case MAINNET:
                return MainNetParams.get();
            case TESTNET:
                return TestNet3Params.get();
            case SIGNET:
                return SigNetParams.get();
            case REGTEST:
                return RegTestParams.get();
            default:
                throw new IllegalArgumentException("Unknown network");
        }
    }

    /**
     * @return the payment protocol network id string
     * @deprecated Use {@link PaymentProtocol#protocolIdFromParams(NetworkParameters)}
     */
    @Deprecated
    public String getPaymentProtocolId() {
        return PaymentProtocol.protocolIdFromParams(this);
    }

    /**
     * Checks if we are at a reward halving point.
     * @param previousHeight The height of the previous stored block
     * @return If this is a reward halving point
     */
    public final boolean isRewardHalvingPoint(final int previousHeight) {
        return ((previousHeight + 1) % REWARD_HALVING_INTERVAL) == 0;
    }

    static private final Coin genesisBlockRewardCoin = Coin.valueOf(1,0);
    static private final Coin minimumSubsidy = Coin.valueOf(5,0);
    static private final Coin premineSubsidy = Coin.valueOf(240640,0);

    /**
     * <p>A utility method that calculates how much new Bitcoin would be created by the block at the given height.
     * The inflation of Bitcoin is predictable and drops roughly every 4 years (210,000 blocks). At the dawn of
     * the system it was 50 coins per block, in late 2012 it went to 25 coins per block, and so on. The size of
     * a coinbase transaction is inflation plus fees.</p>
     *
     * <p>The half-life is controlled by {@link NetworkParameters#getSubsidyDecreaseBlockCount()}.</p>
     *
     * @param height the height of the block to calculate inflation for
     * @return block reward (inflation) for specified block
     */
    public static Coin getBlockInflation(int height) {
        if (height == 0)
        {
            return genesisBlockRewardCoin;
        }

        if (height == 1)
        {
            return premineSubsidy;
		/*
		optimized standalone cpu miner 	60*512=30720
		standalone gpu miner 			120*512=61440
		first pool			 			70*512 =35840
		block-explorer		 			60*512 =30720
		mac wallet binary    			30*512 =15360
		linux wallet binary  			30*512 =15360
		web-site						100*512	=51200
		total									=240640
		*/
        }

        Coin nSubsidy = Coin.valueOf(512, 0);

        // Subsidy is reduced by 6% every 10080 blocks, which will occur approximately every 1 week
        int exponent=(height / 10080);
        for(int i=0;i<exponent;i++){
            nSubsidy=nSubsidy.multiply(47);
            nSubsidy=nSubsidy.divide(50);
        }
        if(nSubsidy.compareTo(minimumSubsidy) < 0) {nSubsidy=minimumSubsidy;}
        return nSubsidy;
    }

    /**
     * Checks if we are at a difficulty transition point.
     * @param previousHeight The height of the previous stored block
     * @return If this is a difficulty transition point
     */
    public final boolean isDifficultyTransitionPoint(final int previousHeight) {
        return ((previousHeight + 1) % this.getInterval()) == 0;
    }

    public void checkDifficultyTransitions_btc(final StoredBlock storedPrev, final Block nextBlock,
        final BlockStore blockStore) throws VerificationException, BlockStoreException {
        final Block prev = storedPrev.getHeader();

        // Is this supposed to be a difficulty transition point?
        if (!isDifficultyTransitionPoint(storedPrev.getHeight())) {

            // No ... so check the difficulty didn't actually change.
            if (nextBlock.getDifficultyTarget() != prev.getDifficultyTarget())
                throw new VerificationException("Unexpected change in difficulty at height " + storedPrev.getHeight() +
                        ": " + Long.toHexString(nextBlock.getDifficultyTarget()) + " vs " +
                        Long.toHexString(prev.getDifficultyTarget()));
            return;
        }

        // We need to find a block far back in the chain. It's OK that this is expensive because it only occurs every
        // two weeks after the initial block chain download.
        Stopwatch watch = Stopwatch.start();
        Sha256Hash hash = prev.getHash();
        StoredBlock cursor = null;
        final int interval = this.getInterval();
        for (int i = 0; i < interval; i++) {
            cursor = blockStore.get(hash);
            if (cursor == null) {
                // This should never happen. If it does, it means we are following an incorrect or busted chain.
                throw new VerificationException(
                        "Difficulty transition point but we did not find a way back to the last transition point. Not found: " + hash);
            }
            hash = cursor.getHeader().getPrevBlockHash();
        }
        checkState(cursor != null && isDifficultyTransitionPoint(cursor.getHeight() - 1), () ->
                "didn't arrive at a transition point");
        watch.stop();
        if (watch.elapsed().toMillis() > 50)
            log.info("Difficulty transition traversal took {}", watch);

        Block blockIntervalAgo = cursor.getHeader();
        int timespan = (int) (prev.getTimeSeconds() - blockIntervalAgo.getTimeSeconds());
        // Limit the adjustment step.
        final int targetTimespan = this.getTargetTimespan();
        if (timespan < targetTimespan / 4)
            timespan = targetTimespan / 4;
        if (timespan > targetTimespan * 4)
            timespan = targetTimespan * 4;

        BigInteger newTarget = ByteUtils.decodeCompactBits(prev.getDifficultyTarget());
        newTarget = newTarget.multiply(BigInteger.valueOf(timespan));
        newTarget = newTarget.divide(BigInteger.valueOf(targetTimespan));

        BigInteger maxTarget = this.getMaxTarget();
        if (newTarget.compareTo(maxTarget) > 0) {
            log.info("Difficulty hit proof of work limit: {} vs {}",
                    Long.toHexString(ByteUtils.encodeCompactBits(newTarget)),
                    Long.toHexString(ByteUtils.encodeCompactBits(maxTarget)));
            newTarget = maxTarget;
        }

        int accuracyBytes = (int) (nextBlock.getDifficultyTarget() >>> 24) - 3;
        long receivedTargetCompact = nextBlock.getDifficultyTarget();

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        newTarget = newTarget.and(mask);
        long newTargetCompact = ByteUtils.encodeCompactBits(newTarget);

        if (newTargetCompact != receivedTargetCompact)
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    Long.toHexString(newTargetCompact) + " vs " + Long.toHexString(receivedTargetCompact));
    }

    @Override
    @Deprecated
    public Coin getMaxMoney() {
        return BitcoinNetwork.MAX_MONEY;
    }

    /**
     * @deprecated Get one another way or construct your own {@link MonetaryFormat} as needed.
     */
    @Override
    @Deprecated
    public MonetaryFormat getMonetaryFormat() {
        return new MonetaryFormat();
    }

    @Override
    public BitcoinSerializer getSerializer() {
        return new BitcoinSerializer(this);
    }

    @Override
    @Deprecated
    public String getUriScheme() {
        return BitcoinNetwork.BITCOIN_SCHEME;
    }

    @Override
    @Deprecated
    public boolean hasMaxMoney() {
        return network().hasMaxMoney();
    }

    protected void verifyDifficulty(BigInteger calcDiff, StoredBlock storedPrev, Block nextBlock)
    {
        if (calcDiff.compareTo(maxTarget) > 0) {
            //log.info("Difficulty hit proof of work limit: {}", calcDiff.toString(16));
            calcDiff = maxTarget;
        }
        int accuracyBytes = (int) (nextBlock.getDifficultyTarget() >>> 24) - 3;
        BigInteger receivedDifficulty = nextBlock.getDifficultyTargetAsInteger();

        // The calculated difficulty is to a higher precision than received, so reduce here.
        BigInteger mask = BigInteger.valueOf(0xFFFFFFL).shiftLeft(accuracyBytes * 8);
        calcDiff = calcDiff.and(mask);

        if (calcDiff.compareTo(receivedDifficulty) != 0)
            throw new VerificationException("Network provided difficulty bits do not match what was calculated: " +
                    receivedDifficulty.toString(16) + " vs " + calcDiff.toString(16));
    }

    protected void DarkGravityWave3(final StoredBlock storedPrev, final Block nextBlock,
                                  final BlockStore blockStore) {
        /* current difficulty formula, darkcoin - DarkGravity v3, written by Evan Duffield - evan@darkcoin.io */
        StoredBlock BlockLastSolved = storedPrev;
        StoredBlock BlockReading = storedPrev;
        Block BlockCreating = nextBlock;
        BlockCreating = BlockCreating;
        long nActualTimespan = 0;
        long LastBlockTime = 0;
        long PastBlocksMin = 24;
        long PastBlocksMax = 24;
        long CountBlocks = 0;
        BigInteger PastDifficultyAverage = BigInteger.ZERO;
        BigInteger PastDifficultyAveragePrev = BigInteger.ZERO;

        if (BlockLastSolved == null || BlockLastSolved.getHeight() == 0 || BlockLastSolved.getHeight() < PastBlocksMin) {
            verifyDifficulty(this.getMaxTarget(), storedPrev, nextBlock);
            return;
        }

        for (int i = 1; BlockReading != null && BlockReading.getHeight() > 0; i++) {
            if (PastBlocksMax > 0 && i > PastBlocksMax) { break; }
            CountBlocks++;

            if(CountBlocks <= PastBlocksMin) {
                if (CountBlocks == 1) { PastDifficultyAverage = BlockReading.getHeader().getDifficultyTargetAsInteger(); }
                else { PastDifficultyAverage = ((PastDifficultyAveragePrev.multiply(BigInteger.valueOf(CountBlocks)).add(BlockReading.getHeader().getDifficultyTargetAsInteger()).divide(BigInteger.valueOf(CountBlocks + 1)))); }
                PastDifficultyAveragePrev = PastDifficultyAverage;
            }

            if(LastBlockTime > 0){
                long Diff = (LastBlockTime - BlockReading.getHeader().getTimeSeconds());
                nActualTimespan += Diff;
            }
            LastBlockTime = BlockReading.getHeader().getTimeSeconds();

            try {
                StoredBlock BlockReadingPrev = blockStore.get(BlockReading.getHeader().getPrevBlockHash());
                if (BlockReadingPrev == null)
                {
                    //assert(BlockReading); break;
                    return;
                }
                BlockReading = BlockReadingPrev;
            }
            catch(BlockStoreException x)
            {
                return;
            }
        }

        BigInteger bnNew= PastDifficultyAverage;

        long nTargetTimespan = CountBlocks*this.TARGET_SPACING;//nTargetSpacing;

        if (nActualTimespan < nTargetTimespan/3)
            nActualTimespan = nTargetTimespan/3;
        if (nActualTimespan > nTargetTimespan*3)
            nActualTimespan = nTargetTimespan*3;

        // Retarget
        bnNew = bnNew.multiply(BigInteger.valueOf(nActualTimespan));
        bnNew = bnNew.divide(BigInteger.valueOf(nTargetTimespan));
        verifyDifficulty(bnNew, storedPrev, nextBlock);

    }
    protected void DarkGravityWave(final StoredBlock storedPrev, final Block nextBlock,
                                 final BlockStore blockStore) {
    /* current difficulty formula, limecoin - DarkGravity, written by Evan Duffield - evan@limecoin.io */
        StoredBlock BlockLastSolved = storedPrev;
        StoredBlock BlockReading = storedPrev;
        Block BlockCreating = nextBlock;
        //BlockCreating = BlockCreating;
        long nBlockTimeAverage = 0;
        long nBlockTimeAveragePrev = 0;
        long nBlockTimeCount = 0;
        long nBlockTimeSum2 = 0;
        long nBlockTimeCount2 = 0;
        long LastBlockTime = 0;
        long PastBlocksMin = 12;
        long PastBlocksMax = 120;
        long CountBlocks = 0;
        BigInteger PastDifficultyAverage = BigInteger.valueOf(0);
        BigInteger PastDifficultyAveragePrev = BigInteger.valueOf(0);

        //if (BlockLastSolved == NULL || BlockLastSolved->nHeight == 0 || BlockLastSolved->nHeight < PastBlocksMin) { return bnProofOfWorkLimit.GetCompact(); }
        if (BlockLastSolved == null || BlockLastSolved.getHeight() == 0 || (long)BlockLastSolved.getHeight() < PastBlocksMin)
        { verifyDifficulty(this.getMaxTarget(), storedPrev, nextBlock); return;}

        for (int i = 1; BlockReading != null && BlockReading.getHeight() > 0; i++) {
            if (PastBlocksMax > 0 && i > PastBlocksMax)
            {
                break;
            }
            CountBlocks++;

            if(CountBlocks <= PastBlocksMin) {
                if (CountBlocks == 1) { PastDifficultyAverage = BlockReading.getHeader().getDifficultyTargetAsInteger(); }
                else
                {
                    //PastDifficultyAverage = ((CBigNum().SetCompact(BlockReading->nBits) - PastDifficultyAveragePrev) / CountBlocks) + PastDifficultyAveragePrev;
                    PastDifficultyAverage = BlockReading.getHeader().getDifficultyTargetAsInteger().subtract(PastDifficultyAveragePrev).divide(BigInteger.valueOf(CountBlocks)).add(PastDifficultyAveragePrev);

                }
                PastDifficultyAveragePrev = PastDifficultyAverage;
            }

            if(LastBlockTime > 0){
                long Diff = (LastBlockTime - BlockReading.getHeader().getTimeSeconds());
                if(Diff < 0)
                    Diff = 0;
                if(nBlockTimeCount <= PastBlocksMin) {
                    nBlockTimeCount++;

                    if (nBlockTimeCount == 1) { nBlockTimeAverage = Diff; }
                    else { nBlockTimeAverage = ((Diff - nBlockTimeAveragePrev) / nBlockTimeCount) + nBlockTimeAveragePrev; }
                    nBlockTimeAveragePrev = nBlockTimeAverage;
                }
                nBlockTimeCount2++;
                nBlockTimeSum2 += Diff;
            }
            LastBlockTime = BlockReading.getHeader().getTimeSeconds();

            //if (BlockReading->pprev == NULL)
            try {
                StoredBlock BlockReadingPrev = blockStore.get(BlockReading.getHeader().getPrevBlockHash());
                if (BlockReadingPrev == null)
                {
                    //assert(BlockReading); break;
                    return;
                }
                BlockReading = BlockReadingPrev;
            }
            catch(BlockStoreException x)
            {
                return;
            }
        }

        BigInteger bnNew = PastDifficultyAverage;
        if (nBlockTimeCount != 0 && nBlockTimeCount2 != 0) {
            double SmartAverage = ((((double)nBlockTimeAverage)*0.7)+((nBlockTimeSum2 / nBlockTimeCount2)*0.3));
            if(SmartAverage < 1) SmartAverage = 1;
            double Shift = TARGET_SPACING/SmartAverage;

            double fActualTimespan = (((double)CountBlocks*(double)TARGET_SPACING)/Shift);
            double fTargetTimespan = ((double)CountBlocks*TARGET_SPACING);
            if (fActualTimespan < fTargetTimespan/3)
                fActualTimespan = fTargetTimespan/3;
            if (fActualTimespan > fTargetTimespan*3)
                fActualTimespan = fTargetTimespan*3;

            long nActualTimespan = (long)fActualTimespan;
            long nTargetTimespan = (long)fTargetTimespan;

            // Retarget
            bnNew = bnNew.multiply(BigInteger.valueOf(nActualTimespan));
            bnNew = bnNew.divide(BigInteger.valueOf(nTargetTimespan));
        }
        verifyDifficulty(bnNew, storedPrev, nextBlock);

        /*if (bnNew > bnProofOfWorkLimit){
            bnNew = bnProofOfWorkLimit;
        }

        return bnNew.GetCompact();*/
    }
    @Override
    public void checkDifficultyTransitions(final StoredBlock storedPrev, final Block nextBlock,
                                           final BlockStore blockStore) throws VerificationException, BlockStoreException {

        if (storedPrev.getHeight() >= (100000 - 1))
        {
                DarkGravityWave3(storedPrev, nextBlock, blockStore);
                return;
        }

        DarkGravityWave(storedPrev, nextBlock, blockStore);
        return;
    }
}
