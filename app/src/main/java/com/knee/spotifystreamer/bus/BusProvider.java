package com.knee.spotifystreamer.bus;

import com.squareup.otto.Bus;

/**
 * Created by c_cknee on 7/29/2015.
 */
public final class BusProvider {
    private static final Bus BUS = new Bus();
    private BusProvider(){}
    public static Bus getInstance(){
        return BUS;
    }
}
