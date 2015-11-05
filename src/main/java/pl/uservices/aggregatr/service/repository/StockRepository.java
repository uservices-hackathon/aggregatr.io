package pl.uservices.aggregatr.service.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import pl.uservices.aggregatr.service.dto.Ingredient;


@Repository
public class StockRepository
{

	private final Map<Ingredient, Integer> stock = new ConcurrentHashMap<>();


	public StockRepository()
	{
		for (final Ingredient ingredient : Ingredient.values())
		{
			stock.put(ingredient, 0);
		}
	}

	public int getAmount(final Ingredient ingredient)
	{
		return stock.get(ingredient);
	}

	public int modifyAmount(final Ingredient ingredient, final int delta)
	{
		return stock.merge(ingredient, delta, (currentVal, newVal) -> currentVal + newVal);
	}

}
