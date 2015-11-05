package pl.uservices.aggregatr.service.aggregation
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc
import spock.lang.Specification

abstract class BaseMockMvcSpec extends Specification {

    def setup() {
        setupMocks()
        RestAssuredMockMvc.standaloneSetup()
    }

    void setupMocks() {

    }

}
