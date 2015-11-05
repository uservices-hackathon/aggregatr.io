package pl.uservices.external.service.impl;

import java.util.Random;

import pl.uservices.external.service.YeastService;


public class DefaultYeastService implements YeastService
{

	private Random rnd = new Random();

	@Override
	public int orderYeast()
	{
		return rnd.nextInt(1001);
	}

}
