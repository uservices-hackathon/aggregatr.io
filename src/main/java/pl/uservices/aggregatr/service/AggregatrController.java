package pl.uservices.aggregatr.service;

import java.util.HashMap;
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

		final Map<Ingredient, Integer> stock = new HashMap<>();

		for (final Ingredient ingredient : orderRequest.getIngredients())
		{
			switch (ingredient)
			{

				case HOP:
					int orderHop = hopService.orderHop();
					stock.put(Ingredient.HOP, orderHop);
					break;
				case WATER:
					int orderWater = waterService.orderWater();
					stock.put(Ingredient.WATER, orderWater);
					break;
				case YEAST:
					int orderYeast = yeastService.orderYeast();
					stock.put(Ingredient.YEAST, orderYeast);
					break;
				case MALT:
					int orderMalt = maltService.orderMalt();
					stock.put(Ingredient.MALT, orderMalt);
					break;
			}
		}


		return new OrderResponse(stock);
	}

}
