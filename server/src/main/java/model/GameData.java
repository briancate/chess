package model;

import chess.ChessGame;
import com.google.gson.Gson;

import java.util.Map;

public record GameData (int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(Map.of("gameID", gameID, "whiteUsername", whiteUsername, "blackUsername", blackUsername, "gameName", gameName));
    }
}


