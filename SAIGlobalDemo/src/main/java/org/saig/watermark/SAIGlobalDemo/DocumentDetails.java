package org.saig.watermark.SAIGlobalDemo;

public class DocumentDetails {
    public String staffAccountName;
    public String date;

    public DocumentDetails(String staffAccountName, String date) {
        super();
        this.staffAccountName = staffAccountName;
        this.date = date;
    }

    public String getStaffAccountName() {
        return staffAccountName;
    }

    public void setStaffAccountName(String staffAccountName) {
        this.staffAccountName = staffAccountName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}
