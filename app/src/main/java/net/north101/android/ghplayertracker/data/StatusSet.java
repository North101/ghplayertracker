package net.north101.android.ghplayertracker.data;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class StatusSet {
    public boolean disarm;
    public boolean stun;
    public boolean immobilize;
    public boolean poison;
    public boolean wound;
    public boolean muddle;
    public boolean invisible;
    public boolean strengthen;

    @ParcelConstructor
    public StatusSet(boolean disarm, boolean stun, boolean immobilize, boolean poison, boolean wound, boolean muddle, boolean invisible, boolean strengthen) {
        this.disarm = disarm;
        this.stun = stun;
        this.immobilize = immobilize;
        this.poison = poison;
        this.wound = wound;
        this.muddle = muddle;
        this.invisible = invisible;
        this.strengthen = strengthen;
    }

    public StatusSet() {}
}
