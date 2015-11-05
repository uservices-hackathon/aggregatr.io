package pl.uservices.external.service.impl;

import java.util.Random;

import pl.uservices.external.service.WaterService;


public class DefaultWaterService implements WaterService
{

	private Random rnd = new Random();

	@Override
	public int orderWater()
	{
		return rnd.nextInt(1001);
	}

}
