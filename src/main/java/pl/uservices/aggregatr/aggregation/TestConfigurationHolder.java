package pl.uservices.aggregatr.aggregation;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TestConfigurationHolder {
	public static final String TEST_COMMUNICATION_TYPE_HEADER_NAME = "TEST-COMMUNICATION-TYPE";
	public static final ThreadLocal<TestConfigurationHolder> CURRENT_HOLDER = new ThreadLocal<>();

	private TestCommunicationType testCommunicationType;

	public enum TestCommunicationType {
		FEIGN, REST_TEMPLATE
	}
}
