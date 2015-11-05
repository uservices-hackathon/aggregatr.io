package pl.uservices.aggregatr.service.dto;

import java.util.List;


public class OrderRequest
{

	private final List<Ingredient> ingredients;

	public OrderRequest(final List<Ingredient> ingredients)
	{
		this.ingredients = ingredients;
	}

	public List<Ingredient> getIngredients()
	{
		return ingredients;
	}
}
