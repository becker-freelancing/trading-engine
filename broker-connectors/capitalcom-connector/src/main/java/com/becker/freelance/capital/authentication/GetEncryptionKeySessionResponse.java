package com.becker.freelance.capital.authentication;

public record GetEncryptionKeySessionResponse(
        String encryptionKey,
        Long timeStamp) {
}
