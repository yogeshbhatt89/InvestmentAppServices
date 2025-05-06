package com.investmentapp.investment_app.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class CompanyNewsDTO {
  private String category;
  private Long datetime;
  private String headline;
  private String id;
  private String image;

  @JsonProperty("related")
  @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
  private List<String> related;

  private String source;
  private String summary;
  private String url;

  public CompanyNewsDTO() {}

  public CompanyNewsDTO(
      String category,
      Long datetime,
      String headline,
      String id,
      String image,
      List<String> related,
      String source,
      String summary,
      String url) {
    this.category = category;
    this.datetime = datetime;
    this.headline = headline;
    this.id = id;
    this.image = image;
    this.related = related;
    this.source = source;
    this.summary = summary;
    this.url = url;
  }

  // Getters and Setters
  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public Long getDatetime() {
    return datetime;
  }

  public void setDatetime(Long datetime) {
    this.datetime = datetime;
  }

  public String getHeadline() {
    return headline;
  }

  public void setHeadline(String headline) {
    this.headline = headline;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

  public List<String> getRelated() {
    return related;
  }

  public void setRelated(List<String> related) {
    this.related = related;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public String getSummary() {
    return summary;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
