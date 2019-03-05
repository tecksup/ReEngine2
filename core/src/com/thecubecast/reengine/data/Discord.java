package com.thecubecast.reengine.data;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord {

    private Thread t;
    private DiscordEventHandlers handlers;
    private DiscordRichPresence presence;

    public Discord(String ApplicationId, String SteamId) {
        Init(ApplicationId, SteamId);
    }

    public Discord(String ApplicationId) {
        Init(ApplicationId, "");
    }

    public void Init(String ApplicationId, String SteamId) {
        handlers = new DiscordEventHandlers();
        presence = new DiscordRichPresence();
        DiscordRPC.INSTANCE.Discord_Initialize(ApplicationId, handlers, true, SteamId);
        presence.details = "Multiplayer Demo";
        presence.state = "Initializing";
        presence.largeImageKey = "clouds";
        presence.smallImageKey = "clouds";
        presence.smallImageText = "The Character";
        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
        t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordRPC.INSTANCE.Discord_RunCallbacks();
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    DiscordRPC.INSTANCE.Discord_Shutdown();
                    break;
                }
            }
        }, "RPC-Callback-Handler");
        t.start();
    }

    public void setPresenceDetails(String detail) {
        presence.details = detail;
    }

    public void setPresenceState(String state) {
        presence.state = state;
    }

    public DiscordRichPresence getPresence() {
        return presence;
    }

    public DiscordEventHandlers gethandlers() {
        return handlers;
    }

    public void UpdatePresence() {
        DiscordRPC.INSTANCE.Discord_UpdatePresence(presence);
    }

    public void dispose() {
        t.interrupt();
    }
}
