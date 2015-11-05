package pl.uservices.aggregatr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.uservices.aggregatr.service.dto.Ingredient;
import pl.uservices.aggregatr.service.dto.OrderRequest;
import pl.uservices.aggregatr.service.dto.OrderResponse;
import pl.uservices.aggregatr.service.external.HopService;
import pl.uservices.aggregatr.service.external.MaltService;
import pl.uservices.aggregatr.service.external.WaterService;
import pl.uservices.aggregatr.service.external.YeastService;
import pl.uservices.aggregatr.service.repository.StockRepository;


@RestController
public class AggregatrController
{

	private static final Integer THRESHOLD_VALUE = 1000;

	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private HopService hopService;
	@Autowired
	private MaltService maltService;
	@Autowired
	private WaterService waterService;
	@Autowired
	private YeastService yeastService;


	@RequestMapping(value = "/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrderResponse order(final @RequestBody OrderRequest orderRequest)
	{
		final Map<Ingredient, Integer> stock = handleIngredientsOrder(orderRequest.getIngredients());
		if (thresholdsExceeded(stock))
		{

		}
		return new OrderResponse(stock);
	}

	private Map<Ingredient, Integer> handleIngredientsOrder(final List<Ingredient> ingredients)
	{
		final Map<Ingredient, Integer> stock = new HashMap<>();
		for (final Ingredient ingredient : Ingredient.values())
		{
			if (ingredients.contains(ingredient))
			{
				int orderedAmount = orderExternal(ingredient);
				stock.put(ingredient, stockRepository.modifyAmount(ingredient, orderedAmount));
			}
			else
			{
				stock.put(ingredient, stockRepository.getAmount(ingredient));
			}
		}
		return stock;
	}

	private int orderExternal(final Ingredient ingredient)
	{
		switch (ingredient)
		{
			case HOP:
				return hopService.orderHop();
			case WATER:
				return waterService.orderWater();
			case YEAST:
				return yeastService.orderYeast();
			case MALT:
				return maltService.orderMalt();
		}
       return 0;
	}

	private boolean thresholdsExceeded(final Map<Ingredient, Integer> stock)
	{
		for (final Integer stockValue : stock.values())
		{
			if (stockValue < THRESHOLD_VALUE)
			{
				return false;
			}
		}
		return true;
	}

}
