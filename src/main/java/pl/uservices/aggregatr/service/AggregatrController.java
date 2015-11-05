package pl.uservices.aggregatr.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.uservices.aggregatr.service.dto.Ingredient;
import pl.uservices.aggregatr.service.dto.OrderRequest;
import pl.uservices.aggregatr.service.dto.OrderResponse;
import pl.uservices.aggregatr.service.dto.WortRequest;
import pl.uservices.aggregatr.service.external.HopService;
import pl.uservices.aggregatr.service.external.MaltService;
import pl.uservices.aggregatr.service.external.WaterService;
import pl.uservices.aggregatr.service.external.YeastService;
import pl.uservices.aggregatr.service.repository.StockRepository;

import com.ofg.infrastructure.discovery.ServiceAlias;
import com.ofg.infrastructure.web.resttemplate.fluent.ServiceRestClient;


@RestController
public class AggregatrController
{

	private static final Integer THRESHOLD_VALUE = 1000;
	private static final Integer DECREASE_AMOUNT = -1000;

	private static final String MATURATOR_SERVICE_NAME = "dojrzewatr";
	private static final String WORT_ENDPOINT_URL = "/wort";

	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private ServiceRestClient serviceRestClient;
	@Autowired
	private HopService hopService;
	@Autowired
	private MaltService maltService;
	@Autowired
	private WaterService waterService;
	@Autowired
	private YeastService yeastService;

	private static final Logger LOG = LoggerFactory.getLogger(AggregatrController.class);


	@RequestMapping(value = "/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrderResponse order(final @RequestBody OrderRequest orderRequest)
	{
		LOG.info("Received order request for " + orderRequest.toString());

		Map<Ingredient, Integer> stock = handleIngredientsOrder(orderRequest.getIngredients());
		if (thresholdsExceeded(stock))
		{
			LOG.info("Thresholds exceeded, sending wort!");
			sendToMaturator();
			stock = decreaseStock();
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
				LOG.info("Ordered " + orderedAmount + " of " + ingredient);
				stock.put(ingredient, stockRepository.modifyAmount(ingredient, orderedAmount));
			}
			else
			{
				stock.put(ingredient, stockRepository.getAmount(ingredient));
			}
		}
		return stock;
	}

	private void sendToMaturator()
	{
		ResponseEntity response = serviceRestClient.forService(new ServiceAlias(MATURATOR_SERVICE_NAME)).post()
				.onUrl(WORT_ENDPOINT_URL).body(new WortRequest(1)).withHeaders().contentTypeJson().andExecuteFor().aResponseEntity()
				.ofType(ResponseEntity.class);
		LOG.info("Sent to MATURATOR \\../, got response code " + response.getStatusCode());
	}

	private Map<Ingredient, Integer> decreaseStock()
	{
		final Map<Ingredient, Integer> result = new HashMap<>();
		for (final Ingredient ingredient : Ingredient.values())
		{
			result.put(ingredient, stockRepository.modifyAmount(ingredient, DECREASE_AMOUNT));
		}
		return result;
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
