package in.ureport.flowrunner.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by johncordeiro on 13/10/15.
 */
public class FlowRun implements Parcelable {

    private Flow flow;

    private Contact contact;

    private Boolean responded;

    @SerializedName("created_on")
    private Date createdOn;

    @SerializedName("modified_on")
    private Date modifiedOn;

    @SerializedName("expires_on")
    private Date expiresOn;

    @SerializedName("expired_on")
    private Date expiredOn;

    public Boolean getResponded() {
        return responded;
    }

    public void setResponded(Boolean responded) {
        this.responded = responded;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Date createdOn) {
        this.createdOn = createdOn;
    }

    public Date getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(Date modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public Date getExpiresOn() {
        return expiresOn;
    }

    public void setExpiresOn(Date expiresOn) {
        this.expiresOn = expiresOn;
    }

    public Date getExpiredOn() {
        return expiredOn;
    }

    public void setExpiredOn(Date expiredOn) {
        this.expiredOn = expiredOn;
    }

    public Flow getFlow() {
        return flow;
    }

    public FlowRun setFlow(Flow flow) {
        this.flow = flow;
        return this;
    }

    public Contact getContact() {
        return contact;
    }

    public FlowRun setContact(Contact contact) {
        this.contact = contact;
        return this;
    }

    @Override
    public String toString() {
        return "FlowRun{" +
                ", flow=" + flow +
                ", contact='" + contact + '\'' +
                ", responded=" + responded +
                ", createdOn=" + createdOn +
                ", modifiedOn=" + modifiedOn +
                ", expiresOn=" + expiresOn +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.flow, flags);
        dest.writeParcelable(this.contact, flags);
        dest.writeValue(this.responded);
        dest.writeLong(this.createdOn != null ? this.createdOn.getTime() : -1);
        dest.writeLong(this.modifiedOn != null ? this.modifiedOn.getTime() : -1);
        dest.writeLong(this.expiresOn != null ? this.expiresOn.getTime() : -1);
        dest.writeLong(this.expiredOn != null ? this.expiredOn.getTime() : -1);
    }

    public FlowRun() {
    }

    protected FlowRun(Parcel in) {
        this.flow = in.readParcelable(Flow.class.getClassLoader());
        this.contact = in.readParcelable(Contact.class.getClassLoader());
        this.responded = (Boolean) in.readValue(Boolean.class.getClassLoader());
        long tmpCreatedOn = in.readLong();
        this.createdOn = tmpCreatedOn == -1 ? null : new Date(tmpCreatedOn);
        long tmpModifiedOn = in.readLong();
        this.modifiedOn = tmpModifiedOn == -1 ? null : new Date(tmpModifiedOn);
        long tmpExpiresOn = in.readLong();
        this.expiresOn = tmpExpiresOn == -1 ? null : new Date(tmpExpiresOn);
        long tmpExpiredOn = in.readLong();
        this.expiredOn = tmpExpiredOn == -1 ? null : new Date(tmpExpiredOn);
    }

    public static final Creator<FlowRun> CREATOR = new Creator<FlowRun>() {
        @Override
        public FlowRun createFromParcel(Parcel source) {
            return new FlowRun(source);
        }

        @Override
        public FlowRun[] newArray(int size) {
            return new FlowRun[size];
        }
    };
}
