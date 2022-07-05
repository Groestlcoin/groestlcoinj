// Copyright (c) 2009-2010 Satoshi Nakamoto
// Copyright (c) 2009-2012 The Bitcoin developers
// Distributed under the MIT/X11 software license, see the accompanying
// file COPYING or http://www.opensource.org/licenses/mit-license.php.
#ifndef BITCOIN_HASH_H
#define BITCOIN_HASH_H

#include "sph_groestl.h"
#include <vector>

#define HASH512_SIZE 64
#define HASH256_SIZE 32

void trim256(const unsigned char * pn, unsigned char * ret)
{
    for (unsigned int i = 0; i < HASH256_SIZE; i++){
        ret[i] = pn[i];
    }
}

inline bool HashGroestl(const unsigned char * pbegin, const unsigned char * pend, unsigned char * pResult)
{
    sph_groestl512_context  ctx_gr[2];
    static unsigned char pblank[1];
    unsigned char hash[2][HASH512_SIZE];
	
    sph_groestl512_init(&ctx_gr[0]);
    sph_groestl512 (&ctx_gr[0], (pbegin == pend ? pblank : static_cast<const void*>(&pbegin[0])), (pend - pbegin) * sizeof(pbegin[0]));
    sph_groestl512_close(&ctx_gr[0], static_cast<void*>(&hash[0]));
	
	sph_groestl512_init(&ctx_gr[1]);
	sph_groestl512(&ctx_gr[1],static_cast<const void*>(&hash[0]),64);
	sph_groestl512_close(&ctx_gr[1],static_cast<void*>(&hash[1]));
	
    trim256(hash[1], pResult);
    return true;
}

#endif
