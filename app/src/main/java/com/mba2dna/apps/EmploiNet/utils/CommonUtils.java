package com.mba2dna.apps.EmploiNet.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.widget.TextView;
/*
import com.mba2dna.recipeapp.ApplicationContext;
import com.mba2dna.recipeapp.MainActivity;
import com.mba2dna.recipeapp.models.Category;
import com.mba2dna.recipeapp.models.Recipe;
import com.mba2dna.recipeapp.settings.AppConstants;
*/
/**
 * Utility class for common operations
 * 
 * @author mba2dna Team
 * 
 */
public class CommonUtils {
	/**
	 * Converts dp to px
	 * 
	 * @param context
	 * @param dips
	 * @return
	 */
	public static int dipsToPixels(Context context, float dips) {
		if (context != null) {
			return Math.round(TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, dips, context.getResources()
							.getDisplayMetrics()));
		} else {
			return Math.round(dips);
		}
	}

	/**
	 * Returns the resource id
	 * 
	 * @param c
	 * @param pVariableName
	 * @param pResourcename
	 * @param pPackageName
	 * @return
	 */
	public static int getResourceId(Context c, String pVariableName,
			String pResourcename, String pPackageName) {
		try {
			return c.getResources().getIdentifier(pVariableName, pResourcename,
					pPackageName);
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Returns photo by given title
	 * 
	 * @param context
	 * @param name
	 * @return
	 */
	public static Drawable getImage(Context context, String name) {
		return context.getResources().getDrawable(
				context.getResources().getIdentifier(name, "drawable",
						context.getPackageName()));
	}

	/**
	 * Sets Roboto Thin font to given textview
	 * 
	 * @param c
	 * @param textView
	 */
	public static void setRobotoThinFont(Context c, TextView textView) {
		Typeface typeface = Typeface.createFromAsset(c.getAssets(),
				"nexalight.ttf");//Roboto_Thin.ttf
		textView.setTypeface(typeface);
	}
	
	/**
	 * Sets Roboto Thin font to given textview
	 * 
	 * @param c
	 * @param textView
	 */
	public static void setRobotoBoldFont(Context c, TextView textView) {
		Typeface typeface = Typeface.createFromAsset(c.getAssets(),
				"nexabold.ttf");//Roboto_Bold.ttf
		textView.setTypeface(typeface);
	}

	/**
	 * Searches the recipes for given text
	 * 
	 * @param appContext
	 * @param searchFor
	 * @return
	 */
/*	public static List<Recipe> performSearchFor(ApplicationContext appContext,
			String searchFor) {
		List<Category> allCategoriest = appContext
				.getParsedApplicationSettings().getCategories();

		List<Recipe> resultRecipes = new ArrayList<Recipe>();

		for (int i = 0; i < allCategoriest.size(); i++) {
			for (int j = 0; j < allCategoriest.get(i).getRecipes().size(); j++) {
				Recipe currentRecipeToCheck = allCategoriest.get(i)
						.getRecipes().get(j);

				if (currentRecipeToCheck.contains(searchFor)) {
					resultRecipes.add(currentRecipeToCheck);
				}
			}
		}

		return resultRecipes;
	}*/

	/**
	 * Returns the Favorites type_activite
	 * 
	 * @param mainActivity
	 * @return
	 */
	/*public static Category getFavoriteCategory(MainActivity mainActivity) {

		Category favCat = new Category(AppConstants.CATEGORY_FAVORITE, "fav",
				CommonUtils.getAllFavoritesRecipes(mainActivity));

		return favCat;
	}*/

	/**
	 * Returns a list with all favourite recipes
	 * 
	 * @param mainActivity
	 * @return
	 */
	/*private static List<Recipe> getAllFavoritesRecipes(MainActivity mainActivity) {

		List<Recipe> allFavRecipes = new ArrayList<Recipe>();

		List<Category> allCategoriest = ((ApplicationContext) mainActivity
				.getApplicationContext()).getParsedApplicationSettings()
				.getCategories();

		for (int i = 0; i < allCategoriest.size(); i++) {
			for (int j = 0; j < allCategoriest.get(i).getRecipes().size(); j++) {
				Recipe currentRecipe = allCategoriest.get(i).getRecipes()
						.get(j);

				if (mainActivity.isRecipeIdInFavSharedPrefs(currentRecipe
						.getId())) {
					allFavRecipes.add(currentRecipe);
				}
			}
		}

		return allFavRecipes;

	}*/

	/**
	 * Searches the recipes in a given type_activite for given text
	 * 
	 * @param activity
	 * @param categoryName
	 * @param searchFor
	 * @return
	 */
	/*public static List<Recipe> performCategorySearchFor(
			MainActivity activity, String categoryName, String searchFor) {
		ApplicationContext appContext = (ApplicationContext) activity.getApplicationContext();
		List<Category> allCategoriest = appContext
				.getParsedApplicationSettings().getCategories();
		List<Recipe> resultRecipes = new ArrayList<Recipe>();

		if (categoryName.equalsIgnoreCase("Home")) {
			for (int i = 0; i < allCategoriest.size(); i++) {
					for (int j = 0; j < allCategoriest.get(i).getRecipes().size(); j++) {
						Recipe currentRecipeToCheck = allCategoriest.get(i)
								.getRecipes().get(j);

						if (currentRecipeToCheck.getRecipeSummary()
								.getRecipeSummaryOrigin().equals(searchFor)) {
							resultRecipes.add(currentRecipeToCheck);
						}
					}
			}
		} else if (categoryName.equalsIgnoreCase(AppConstants.CATEGORY_FAVORITE)) {
			List<Recipe> favouriteRecipes = getAllFavoritesRecipes(activity);
			for (int i = 0; i < favouriteRecipes.size(); i++) {
				Recipe currentRecipeToCheck = favouriteRecipes.get(i);

				if (currentRecipeToCheck.getRecipeSummary()
						.getRecipeSummaryOrigin().equals(searchFor)) {
					resultRecipes.add(currentRecipeToCheck);
				}
			}
		} else {
			for (int i = 0; i < allCategoriest.size(); i++) {
				if (allCategoriest.get(i).getName().equalsIgnoreCase(categoryName)) {
					for (int j = 0; j < allCategoriest.get(i).getRecipes().size(); j++) {
						Recipe currentRecipeToCheck = allCategoriest.get(i)
								.getRecipes().get(j);

						if (currentRecipeToCheck.getRecipeSummary()
								.getRecipeSummaryOrigin().equals(searchFor)) {
							resultRecipes.add(currentRecipeToCheck);
						}
					}
					break;
				}
			}
		}
		

		return resultRecipes;
	}*/

	/**
	 * Returns the origins from all recipes
	 * 
	 * @param appContext
	 * @return
	 */
	/*public static List<String> getOrigins(ApplicationContext appContext) {
		List<Category> allCategoriest = appContext
				.getParsedApplicationSettings().getCategories();

		List<String> resultOrigins = new ArrayList<String>();

		for (int i = 0; i < allCategoriest.size(); i++) {
			for (int j = 0; j < allCategoriest.get(i).getRecipes().size(); j++) {
				Recipe currentRecipeToCheck = allCategoriest.get(i)
						.getRecipes().get(j);

				if (!resultOrigins.contains(currentRecipeToCheck
						.getRecipeSummary().getRecipeSummaryOrigin())) {
					resultOrigins.add(currentRecipeToCheck.getRecipeSummary()
							.getRecipeSummaryOrigin());
				}
			}
		}
		Collections.sort(resultOrigins);
		return resultOrigins;
	}
	
	public static boolean isContentLocal() {
		boolean result = false;
		if (RecipesXMLTagConstants.TAG_URL_SETTINGS == null
				|| RecipesXMLTagConstants.TAG_URL_SETTINGS.length() == 0) {
			result = true;
		}
		return result;
	}*/
}
