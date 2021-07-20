package com.dacodes.bepensa.entities;

/**
 * created by Carlos Chin Ku
 * email:efrainck94@gmail.com
 */
public class RecoverPassword {
    private int division;
    private String employe_number;
    private String birthday;
    private String newpassword;

    public int getDivision() {
        return division;
    }

    public void setDivision(int division) {
        this.division = division;
    }

    public String getEmploye_number() {
        return employe_number;
    }

    public void setEmploye_number(String employe_number) {
        this.employe_number = employe_number;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getNewpassword() {
        return newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }
}
