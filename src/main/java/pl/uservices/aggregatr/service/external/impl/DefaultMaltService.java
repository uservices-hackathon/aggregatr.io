package pl.uservices.aggregatr.service.external.impl;

import java.util.Random;

import org.springframework.stereotype.Service;
import pl.uservices.aggregatr.service.external.MaltService;

@Service
public class DefaultMaltService implements MaltService
{

	private Random rnd = new Random();

	@Override
	public int orderMalt()
	{
		return rnd.nextInt(1001);
	}

}
