package com.yichen.job.entity;

public class ExampleBook {
    public String title;
    public String author;
    public int pages;
    public String date;
    public String currency;
    public double price;
    public String language;
    public String series;
    public String isbn;

    public ExampleBook(String title,
                       String author,
                       int pages,
                       String date,
                       String currency,
                       double price,
                       String language,
                       String series,
                       String isbn) {
        this.title = title;
        this.author = author;
        this.pages = pages;
        this.date = date;
        this.currency = currency;
        this.price = price;
        this.language = language;
        this.series = series;
        this.isbn = isbn;
    }
}
