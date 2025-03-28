package com.becker.freelance.capital.trades;

import com.becker.freelance.commons.position.Position;
import com.becker.freelance.commons.signal.EntrySignal;
import com.becker.freelance.commons.signal.ExitSignal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public class TradeController {

    private final TradeApiClient apiClient;

    public TradeController() {
        apiClient = new TradeApiClient();
    }

    public List<Position> allPositions() {
        try {
            AllPositionsResponse allPositionsResponse = apiClient.allPositions();
        } catch (URISyntaxException e) {

        } catch (IOException e) {

        } catch (InterruptedException e) {

        }

        return List.of();
    }

    public void createPosition(EntrySignal entrySignal) {

    }

    public void closePosition(ExitSignal exitSignal) {

    }
}
