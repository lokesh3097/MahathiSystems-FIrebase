package msipl.com.mahathisystems;

import java.io.Serializable;

public class Analyser implements Serializable
{
    public String serialNumber, invoiceNumber, customerName, customerContact, emailId, date;

    public int day, month, year;

    public Analyser()
    {

    }

    public Analyser(String serialNumber, int day, int month, int year, String invoiceNumber, String customerName, String customerContact, String emailId)
    {
        this.serialNumber = serialNumber;
        this.day = day;
        this.month = month;
        this.year = year;
        this.invoiceNumber = invoiceNumber;
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.emailId = emailId;
        this.date = day+"-"+month+"-"+year;
    }
}
