package model;

import java.util.ArrayList;

public record ListGamesResponse (ArrayList<JsonFriendlyGameData> games, String message) {}
