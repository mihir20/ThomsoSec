package in.thomso.thomsosec;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("contact")
    @Expose
    private String contact;

    @SerializedName("organization")
    @Expose
    private String organization;

    @SerializedName("qr")
    @Expose
    private String qr;

    @SerializedName("format")
    @Expose
    private String format;

    public Data(String image,String name, String email, String contact, String organization, String qr, String format) {
        this.image = image;
        this.email = email;
        this.contact = contact;
        this.organization = organization;
        this.qr = qr;
        this.format = format;
        this.name = name;
    }

    public Data() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
