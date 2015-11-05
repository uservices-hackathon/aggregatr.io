package pl.uservices.aggregatr.service.external.impl;

import org.springframework.stereotype.Service;
import pl.uservices.aggregatr.service.external.HopService;

import java.util.Random;

@Service
public class DefaultHopService implements HopService
{

	private Random rnd = new Random();

	@Override
	public int orderHop()
	{
		return rnd.nextInt(1001);
	}

}
