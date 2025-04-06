package com.becker.freelance.data.csv;

import java.util.Optional;

public class RowMappingInfo {

    private final boolean containsHeader;
    private final int closeTimeIdx;
    private final int openBidIdx;
    private final int openAskIdx;
    private final int highBidIdx;
    private final int highAskIdx;
    private final int lowBidIdx;
    private final int lowAskIdx;
    private final int closeBidIdx;
    private final int closeAskIdx;
    private final int volumeIdx;
    private final int tradesIdx;


    public RowMappingInfo(boolean containsHeader, int closeTimeIdx, int openBidIdx, int openAskIdx, int highBidIdx, int highAskIdx, int lowBidIdx, int lowAskIdx, int closeBidIdx, int closeAskIdx, int volumeIdx, int tradesIdx) {
        this.containsHeader = containsHeader;
        this.closeTimeIdx = closeTimeIdx;
        this.openBidIdx = openBidIdx;
        this.openAskIdx = openAskIdx;
        this.highBidIdx = highBidIdx;
        this.highAskIdx = highAskIdx;
        this.lowBidIdx = lowBidIdx;
        this.lowAskIdx = lowAskIdx;
        this.closeBidIdx = closeBidIdx;
        this.closeAskIdx = closeAskIdx;
        this.volumeIdx = volumeIdx;
        this.tradesIdx = tradesIdx;
    }

    public RowMappingInfo(boolean containsHeader, int closeTimeIdx, int openBidIdx, int openAskIdx, int highBidIdx, int highAskIdx, int lowBidIdx, int lowAskIdx, int closeBidIdx, int closeAskIdx) {
        this(containsHeader,
                closeTimeIdx,
                openBidIdx,
                openAskIdx,
                highBidIdx,
                highAskIdx,
                lowBidIdx,
                lowAskIdx,
                closeBidIdx,
                closeAskIdx,
                -1,
                -1);
    }

    public boolean containsHeader() {
        return containsHeader;
    }

    public int getCloseTimeIdx() {
        return closeTimeIdx;
    }

    public int getOpenBidIdx() {
        return openBidIdx;
    }

    public int getOpenAskIdx() {
        return openAskIdx;
    }

    public int getHighBidIdx() {
        return highBidIdx;
    }

    public int getHighAskIdx() {
        return highAskIdx;
    }

    public int getLowBidIdx() {
        return lowBidIdx;
    }

    public int getLowAskIdx() {
        return lowAskIdx;
    }

    public int getCloseBidIdx() {
        return closeBidIdx;
    }

    public int getCloseAskIdx() {
        return closeAskIdx;
    }

    public Optional<Integer> getVolumeIdx() {
        return volumeIdx == -1 ? Optional.empty() : Optional.of(volumeIdx);
    }

    public Optional<Integer> getTradesIdx() {
        return tradesIdx == -1 ? Optional.empty() : Optional.of(tradesIdx);
    }
}
