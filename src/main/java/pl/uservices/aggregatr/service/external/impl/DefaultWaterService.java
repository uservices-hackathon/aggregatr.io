package pl.uservices.aggregatr.service.external.impl;

import java.util.Random;

import org.springframework.stereotype.Service;
import pl.uservices.aggregatr.service.external.WaterService;


@Service
public class DefaultWaterService implements WaterService
{

	private Random rnd = new Random();

	@Override
	public int orderWater()
	{
		return rnd.nextInt(1001);
	}

}
