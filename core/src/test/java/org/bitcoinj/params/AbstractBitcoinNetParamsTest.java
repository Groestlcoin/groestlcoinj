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

package org.bitcoinj.params;

import org.bitcoinj.core.Block;
import org.bitcoinj.core.Coin;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractBitcoinNetParamsTest {
    private final AbstractBitcoinNetParams BITCOIN_PARAMS = new AbstractBitcoinNetParams() {
        @Override
        public Block getGenesisBlock() {
            return null;
        }

        @Override
        public String getPaymentProtocolId() {
            return null;
        }
    };

    @Test
    public void isDifficultyTransitionPoint() {
        assertFalse(BITCOIN_PARAMS.isDifficultyTransitionPoint(1438));
        assertTrue(BITCOIN_PARAMS.isDifficultyTransitionPoint(1439));
        assertFalse(BITCOIN_PARAMS.isDifficultyTransitionPoint(1440));
    }

    @Test
    public void isRewardHalvingPoint() {
        assertTrue(BITCOIN_PARAMS.isRewardHalvingPoint(10079));

        assertTrue(BITCOIN_PARAMS.isRewardHalvingPoint(100799));

        assertFalse(BITCOIN_PARAMS.isRewardHalvingPoint(100800));
        assertTrue(BITCOIN_PARAMS.isRewardHalvingPoint(201599));
        assertFalse(BITCOIN_PARAMS.isRewardHalvingPoint(201601));

        assertTrue(BITCOIN_PARAMS.isRewardHalvingPoint(1007999));
    }

    @Test
    public void getBlockInflation() {
        assertEquals(Coin.COIN, BITCOIN_PARAMS.getBlockInflation(0));
        assertEquals(Coin.PREMINE_COINS, BITCOIN_PARAMS.getBlockInflation(1));
        assertEquals(Coin.MAX_REWARD_COINS, BITCOIN_PARAMS.getBlockInflation(2));

        assertEquals(Coin.MAX_REWARD_COINS, BITCOIN_PARAMS.getBlockInflation(10078));
        assertEquals(Coin.MAX_REWARD_COINS, BITCOIN_PARAMS.getBlockInflation(10079));
        assertEquals(Coin.valueOf(48128000000L), BITCOIN_PARAMS.getBlockInflation(10081));
        assertEquals(Coin.valueOf(48128000000L), BITCOIN_PARAMS.getBlockInflation(10082));

        assertEquals(Coin.valueOf(29337333871L), BITCOIN_PARAMS.getBlockInflation(100798));
        assertEquals(Coin.valueOf(29337333871L), BITCOIN_PARAMS.getBlockInflation(100799));
        assertEquals(Coin.valueOf(27577093838L), BITCOIN_PARAMS.getBlockInflation(100801));
        assertEquals(Coin.valueOf(27577093838L), BITCOIN_PARAMS.getBlockInflation(100802));
    }
}
