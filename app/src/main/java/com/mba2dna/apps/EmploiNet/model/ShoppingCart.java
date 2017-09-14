package com.mba2dna.apps.EmploiNet.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCart {
	
	private Map<String, List<ShoppingItem>> cart;

	public Map<String, List<ShoppingItem>> getCart() {
		if (cart == null) {
			cart = new HashMap<String, List<ShoppingItem>>();
		}
		return cart;
	}
	
	public void addItemsToCart(String recipeId, List<ShoppingItem> shoppingItems) {
		getCart().put(recipeId, shoppingItems);
	}
	
	public void removeAllItemsFromCart(String recipeId) {
		getCart().put(recipeId, new ArrayList<ShoppingItem>());
	}
	
	public List<ShoppingItem> getShoppingList(String recipeId) {
		List<ShoppingItem> result = getCart().get(recipeId);
		if (result == null) {
			result = new ArrayList<ShoppingItem>();
		}
		return result;
	}
	
	public void addItemToCart(String recipeId, ShoppingItem item) {
		removeItemFromCart(recipeId, item);
		List<ShoppingItem> shoppingItems = getShoppingList(recipeId);
		if (!shoppingItems.contains(item)) {
			shoppingItems.add(item);
			addItemsToCart(recipeId, shoppingItems);
		}
	}
	
	public void removeItemFromCart(String recipeId, ShoppingItem item) {
		List<ShoppingItem> shoppingItems = getShoppingList(recipeId);
		ShoppingItem toBeRemoved = null;
		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getItem().equals(item.getItem())) {
				toBeRemoved = shoppingItem;
			}
		}
		if (toBeRemoved != null) {
			shoppingItems.remove(toBeRemoved);
			addItemsToCart(recipeId, shoppingItems);
		}
	}
	
	public boolean containsItem(String recipeId, String itemName) {
		boolean result = false;
		List<ShoppingItem> shoppingItems = getShoppingList(recipeId);
		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getItem().equals(itemName)) {
				result = true;
			}
		}
		return result;
	}
	
	public ShoppingItem getItem(String recipeId, String itemName) {
		ShoppingItem result = null;
		List<ShoppingItem> shoppingItems = getShoppingList(recipeId);
		for (ShoppingItem shoppingItem : shoppingItems) {
			if (shoppingItem.getItem().equals(itemName)) {
				result = shoppingItem;
			}
		}
		return result;
	}
	
	public void checkItemInCart(String recipeId, String itemName) {
		ShoppingItem item = getItem(recipeId, itemName);
		if (item != null) {
			item.setChecked(true);
			item.setCleared(false);
		}
		
	}
	
	public void clearItemInCart(String recipeId, String itemName) {
		ShoppingItem item = getItem(recipeId, itemName);
		if (item != null) {
			item.setCleared(true);
			item.setChecked(false);
		}
	}
	
	public void restoreItemInCart(String recipeId, String itemName) {
		ShoppingItem item = getItem(recipeId, itemName);
		if (item != null) {
			item.setChecked(false);
			item.setCleared(false);
		}
	}
	
}
