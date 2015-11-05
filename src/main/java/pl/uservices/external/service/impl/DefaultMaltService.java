package pl.uservices.external.service.impl;

import java.util.Random;

import pl.uservices.external.service.MaltService;


public class DefaultMaltService implements MaltService
{

	private Random rnd = new Random();

	@Override
	public int orderMalt()
	{
		return rnd.nextInt(1001);
	}

}
