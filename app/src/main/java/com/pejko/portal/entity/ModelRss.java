package com.pejko.portal.entity;

import com.einmalfel.earl.Item;

import java.util.Date;

public class ModelRss
{

    public String link;
    public Date publicationDate;
    public String title;
    public String description;
    public String imageLink;
    public String author;

    public ModelRss(Item item) {
        this.link = item.getLink();
        this.publicationDate = item.getPublicationDate();
        this.title = item.getTitle();
        this.description = item.getDescription();
        this.imageLink = item.getImageLink();
        this.author = item.getAuthor();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPublicationDate() {
        return publicationDate;
    }

    public void setPublicationDate(Date publicationDate) {
        this.publicationDate = publicationDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String toString() {
        return "ModelRss{" +
                "link='" + link + '\'' +
                ", publicationDate='" + publicationDate.toString() + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}

// eof
