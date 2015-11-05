package pl.uservices.external.service.impl;

import pl.uservices.external.service.HopService;

import java.util.Random;


public class DefaultHopService implements HopService
{

	private Random rnd = new Random();

	@Override
	public int orderHop()
	{
		return rnd.nextInt(1001);
	}

}
