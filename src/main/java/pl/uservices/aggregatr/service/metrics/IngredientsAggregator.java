package pl.uservices.aggregatr.service.metrics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pl.uservices.aggregatr.service.dto.Ingredient;
import pl.uservices.aggregatr.service.repository.StockRepository;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;


@Component
public class IngredientsAggregator
{

	final StockRepository stockRepository;


	@Autowired
	public IngredientsAggregator(final MetricRegistry metricRegistry, final StockRepository stockRepository)
	{
		this.stockRepository = stockRepository;
		setupMeters(metricRegistry);
	}

	private void setupMeters(final MetricRegistry metricRegistry)
	{
		metricRegistry.register("Hop", (Gauge<Integer>) () -> stockRepository.getAmount(Ingredient.HOP));
		metricRegistry.register("Water", (Gauge<Integer>) () -> stockRepository.getAmount(Ingredient.WATER));
		metricRegistry.register("Yeast", (Gauge<Integer>) () -> stockRepository.getAmount(Ingredient.YEAST));
		metricRegistry.register("Malt", (Gauge<Integer>) () -> stockRepository.getAmount(Ingredient.MALT));
	}

}
