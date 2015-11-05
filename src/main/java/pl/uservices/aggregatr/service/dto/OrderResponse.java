package pl.uservices.aggregatr.service.dto;

import java.util.Map;


public class OrderResponse
{

	private Map<Ingredient, Integer> stock;

	public OrderResponse(final Map<Ingredient, Integer> stock)
	{
		this.stock = stock;
	}

	public Map<Ingredient, Integer> getStock()
	{
		return stock;
	}
}
