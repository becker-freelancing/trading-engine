package com.becker.freelance.capital.ws.dto.market;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MarketDataSubscribe {
    private List<String> epics;
    private List<String> resolutions;

    public MarketDataSubscribe(List<String> epics, List<String> resolutions) {
        this.epics = epics;
        this.resolutions = resolutions;
    }

    public List<String> getEpics() {
        return epics;
    }

    public void setEpics(List<String> epics) {
        this.epics = epics;
    }

    public List<String> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<String> resolutions) {
        this.resolutions = resolutions;
    }
}
