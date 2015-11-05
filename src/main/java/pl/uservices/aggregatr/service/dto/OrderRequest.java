package pl.uservices.aggregatr.service.dto;

import java.util.List;


public class OrderRequest
{

	private List<Ingredient> ingredients;


	public List<Ingredient> getIngredients()
	{
		return ingredients;
	}

	public void setIngredients(final List<Ingredient> ingredients)
	{
		this.ingredients = ingredients;
	}

}
