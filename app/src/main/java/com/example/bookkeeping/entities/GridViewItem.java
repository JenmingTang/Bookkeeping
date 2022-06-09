package com.example.bookkeeping.entities;

public class GridViewItem {
    private int img;
    private String text;

    public GridViewItem(int img, String text) {
        this.img = img;
        this.text = text;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "GridViewItem{" +
                "img=" + img +
                ", text='" + text + '\'' +
                '}';
    }
}
