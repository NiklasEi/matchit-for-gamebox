package me.nikl.matchit;

import me.nikl.gamebox.module.GameBoxModule;

public class MatchitModule extends GameBoxModule {
    public final static String MATCHIT = "matchit";

    @Override
    public void onEnable() {
        registerGame(MATCHIT, MatchIt.class, "mi");
    }

    @Override
    public void onDisable() {

    }
}
