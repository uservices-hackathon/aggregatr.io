package pl.uservices.aggregatr.service.external.impl;

import java.util.Random;

import org.springframework.stereotype.Service;
import pl.uservices.aggregatr.service.external.YeastService;

@Service
public class DefaultYeastService implements YeastService
{

	private Random rnd = new Random();

	@Override
	public int orderYeast()
	{
		return rnd.nextInt(1001);
	}

}
