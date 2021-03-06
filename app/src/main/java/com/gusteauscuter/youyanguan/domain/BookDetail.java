package com.gusteauscuter.youyanguan.domain;

import java.util.List;

public class BookDetail extends BookBase {

	private List<LocationInfo> locationInfo;
	private String authorIntro = "";
	private String summary= "";
	private String catalog= "";
	private String pages= "";
	private String price= "";
	private String pictureUrl= "";
	private boolean isDoubanExist;

	public BookDetail() {

	}

	public String getAuthorIntro() {
		return authorIntro;
	}

	public String getSummary() {
		return summary;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getPages() {
		return pages;
	}

	public String getPrice() {
		return price;
	}

	public List<LocationInfo> getLocationInfo() {
		return locationInfo;
	}

	public String getPictureUrl() {
		return pictureUrl;
	}


	public boolean isDoubanExist() {
		return isDoubanExist;
	}


	public void setDoubanExist(boolean isDoubanExist) {
		this.isDoubanExist = isDoubanExist;
	}

	public void setLocationInfo(List<LocationInfo> locationInfo) {
		this.locationInfo = locationInfo;
	}

	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}


	public void setSummary(String summary) {
		this.summary = summary;
	}


	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}


	public void setPages(String pages) {
		this.pages = pages;
	}


	public void setPrice(String price) {
		this.price = price;
	}


	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}


	public void setIsDoubanExist(boolean isDoubanExist) {
		this.isDoubanExist = isDoubanExist;
	}
}
