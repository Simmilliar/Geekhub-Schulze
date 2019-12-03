package com.geekhubjava.schulze.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;

@Service
public class SecureRandomStringService {

    private SecureRandom secureRandom = new SecureRandom();

    public String getString() {
        return new BigInteger(128, secureRandom).toString(32);
    }
}
