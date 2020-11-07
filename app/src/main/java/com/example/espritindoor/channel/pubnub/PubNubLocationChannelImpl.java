/*
 *
 */
package com.example.espritindoor.channel.pubnub;

import android.text.TextUtils;
import android.util.Log;


import com.example.espritindoor.channel.ConversionUtils;
import com.example.espritindoor.channel.LocationChannel;
import com.example.espritindoor.channel.LocationChannelException;
import com.example.espritindoor.channel.LocationChannelListener;
import com.example.espritindoor.channel.LocationEvent;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.espritindoor.technique.SharingUtils.TAG;




public class PubNubLocationChannelImpl implements LocationChannel {

    private Pubnub mPubNub;

    private String mCurrentChannel;

    private LocationChannelListener mListener;

    public PubNubLocationChannelImpl(String publishKey, String subscribeKey) {

        if (TextUtils.isEmpty(publishKey)
                || TextUtils.isEmpty(subscribeKey)) {
            throw new IllegalArgumentException("all arguments must be non null");
        }
        mPubNub = new Pubnub(publishKey, subscribeKey);

    }

    @Override
    public void subscribe(String channelName, LocationChannelListener listener)
            throws LocationChannelException {

        if (TextUtils.isEmpty(channelName)) {
            throw new IllegalArgumentException("channelName must be non empty");
        }
        if (listener == null) {
            throw new IllegalArgumentException("listener must be non null");
        }

        unsubscribe();

        mListener = listener;
        mCurrentChannel = channelName;
        try {
            mPubNub.subscribe(mCurrentChannel, mCallback);
        } catch (PubnubException e) {
            throw new LocationChannelException("subscribe failed", e);
        }
    }

    @Override
    public void unsubscribe() {
        if (mCurrentChannel != null) {
            mPubNub.unsubscribe(mCurrentChannel);
            mCurrentChannel = null;
        }
    }

    @Override
    public void publish(LocationEvent event) {
        try {
            if(mCurrentChannel != null && event != null) {
                mPubNub.publish(mCurrentChannel, ConversionUtils.toJSON(event), false, mCallback);
            }
        } catch (JSONException e) {
            throw new IllegalStateException("conversion failed", e);
        }
    }

    @Override
    public void disconnect() {
        mPubNub.unsubscribeAll();
        mPubNub.shutdown();
    }


    private Callback mCallback = new Callback() {
        @Override
        public void successCallback(String channel, Object message) {
            if (message instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) message;
                LocationEvent event = ConversionUtils.parseJSONOpt(jsonObject);
                if (event != null) {
                    mListener.onLocation(event);
                }
            }
        }

        @Override
        public void connectCallback(String channel, Object message) {
            Log.w(TAG, "connected, channel: " + channel + ", message: " + message);
        }

        @Override
        public void disconnectCallback(String channel, Object message) {
            Log.w(TAG, "disconnect, channel: " + channel + ", message: " + message);
        }

        @Override
        public void reconnectCallback(String channel, Object message) {
            Log.d(TAG, "reconnect, channel: " + channel + ", message: " + message);
        }

        @Override
        public void errorCallback(String channel, PubnubError error) {
            Log.e(TAG, "error, channel: " + channel + ", error: " + error);
        }
    };
}
