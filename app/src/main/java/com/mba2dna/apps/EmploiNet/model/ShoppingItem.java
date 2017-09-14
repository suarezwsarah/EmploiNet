package com.mba2dna.apps.EmploiNet.model;

public class ShoppingItem {
	
	private String item;
	private boolean isChecked;
	private boolean isCleared;
	
	public ShoppingItem(String item, boolean isChecked, boolean isCleared) {
		super();
		this.item = item;
		this.isChecked = isChecked;
		this.isCleared = isCleared;
	}
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public boolean isCleared() {
		return isCleared;
	}
	public void setCleared(boolean isCleared) {
		this.isCleared = isCleared;
	}
	
}
