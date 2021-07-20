package com.dacodes.bepensa.entities;

/**
 * Created by marioguillermo on 6/25/17.
 */

public class MenuEntity {

    private int typeOfCell;
    private String cellTitle;
    private int cellIcon;
    private boolean bold;
    private String Image;
    private String textField1;
    private String textField2;
    private int integer1;
    private int progress;
    private boolean selected;
    private int id;
    private int textSize;
    private boolean integer;
    private int imageId;

    private  String available;

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    private String points;

    public int getTypeOfCell() {
        return typeOfCell;
    }

    public void setTypeOfCell(int typeOfCell) {
        this.typeOfCell = typeOfCell;
    }

    public String getCellTitle() {
        return cellTitle;
    }

    public void setCellTitle(String cellTitle) {
        this.cellTitle = cellTitle;
    }

    public int getCellIcon() {
        return cellIcon;
    }

    public void setCellIcon(int cellIcon) {
        this.cellIcon = cellIcon;
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getTextField1() {
        return textField1;
    }

    public void setTextField1(String textField1) {
        this.textField1 = textField1;
    }

    public String getTextField2() {
        return textField2;
    }

    public void setTextField2(String textField2) {
        this.textField2 = textField2;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public boolean isInteger() {
        return integer;
    }

    public void setInteger(boolean integer) {
        this.integer = integer;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getInteger1() {
        return integer1;
    }

    public void setInteger1(int integer1) {
        this.integer1 = integer1;
    }
}
