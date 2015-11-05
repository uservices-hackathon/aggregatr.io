package pl.uservices.aggregatr.service.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;


@Repository
public class StockRepository
{

	private enum Ingredient
	{
		HOP, WATER, YEAST, MALT;
	}


	private final Map<Ingredient, Integer> stock = new ConcurrentHashMap<>();

	public StockRepository()
	{
		stock.put(Ingredient.HOP, 0);
		stock.put(Ingredient.WATER, 0);
		stock.put(Ingredient.YEAST, 0);
		stock.put(Ingredient.MALT, 0);
	}

	public int getHopAmount()
	{
		return stock.get(Ingredient.HOP);
	}

	public int getMaltAmount()
	{
		return stock.get(Ingredient.MALT);
	}

	public int getWaterAmount()
	{
		return stock.get(Ingredient.WATER);
	}

	public int getYeastAmount()
	{
		return stock.get(Ingredient.YEAST);
	}

	public int modifyHopAmount(final int delta)
	{
		return stock.merge(Ingredient.HOP, delta, (currentVal, newVal) -> currentVal + newVal);
	}

	public int modifyMaltAmount(final int delta)
	{
		return stock.merge(Ingredient.MALT, delta, (currentVal, newVal) -> currentVal + newVal);
	}

	public int modifyWaterAmount(final int delta)
	{
		return stock.merge(Ingredient.WATER, delta, (currentVal, newVal) -> currentVal + newVal);
	}

	public int modifyYeastAmount(final int delta)
	{
		return stock.merge(Ingredient.YEAST, delta, (currentVal, newVal) -> currentVal + newVal);
	}

}
