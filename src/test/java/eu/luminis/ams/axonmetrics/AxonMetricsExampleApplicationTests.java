package eu.luminis.ams.axonmetrics;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "enableFlightGenerator=false")
public class AxonMetricsExampleApplicationTests {

	@Test
	public void contextLoads() {
	}

}
