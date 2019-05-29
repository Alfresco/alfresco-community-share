package org.alfresco.po.share.user.profile;

public class ContactInformation
{
    private String telephone;
    private String mobile;
    private String email;
    private String skype;
    private String im;
    private String googleUserName;

    public ContactInformation(String telephone, String mobile, String email, String skype,
                              String im, String googleUserName)
    {
        this.setTelephone(telephone);
        this.setMobile(mobile);
        this.setEmail(email);
        this.setSkype(skype);
        this.setIm(im);
        this.setGoogleUserName(googleUserName);
    }

    public String getTelephone()
    {
        return telephone;
    }

    public void setTelephone(String telephone)
    {
        this.telephone = telephone;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getSkype()
    {
        return skype;
    }

    public void setSkype(String skype)
    {
        this.skype = skype;
    }

    public String getIm()
    {
        return im;
    }

    public void setIm(String im)
    {
        this.im = im;
    }

    public String getGoogleUserName()
    {
        return googleUserName;
    }

    public void setGoogleUserName(String googleUserName)
    {
        this.googleUserName = googleUserName;
    }

}
