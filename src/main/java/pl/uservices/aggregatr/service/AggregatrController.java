package pl.uservices.aggregatr.service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pl.uservices.aggregatr.service.dto.OrderRequest;
import pl.uservices.aggregatr.service.dto.OrderResponse;


@RestController
public class AggregatrController
{

	@RequestMapping(value = "/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public OrderResponse order(final @RequestBody OrderRequest orderRequest)
	{
		OrderResponse response = null;

		return response;
	}
}
