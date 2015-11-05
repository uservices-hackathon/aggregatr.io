package pl.uservices.aggregatr.service.dto;

public class WortRequest
{

	private final Integer quantity;


	public WortRequest(final Integer quantity)
	{
		this.quantity = quantity;
	}

	public Integer getQuantity()
	{
		return quantity;
	}

}
