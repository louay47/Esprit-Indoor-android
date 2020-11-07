/*
 *
 */
package com.example.espritindoor.channel;

/**
 * Listener for channel events.
 */
public interface LocationChannelListener {

    /***
     * Callback triggered when new location event is seen on channel.
     */
    void onLocation(LocationEvent event);
}
