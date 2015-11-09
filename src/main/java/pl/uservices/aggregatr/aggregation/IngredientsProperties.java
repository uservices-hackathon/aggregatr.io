package pl.uservices.aggregatr.aggregation;

import com.google.common.collect.ImmutableMap;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import pl.uservices.aggregatr.aggregation.model.IngredientType;
import pl.uservices.aggregatr.aggregation.model.Order;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ConfigurationProperties("ingredients")
@Data
public class IngredientsProperties {

    private Map<IngredientType, String> serviceNames = ImmutableMap.<IngredientType, String>builder()
            .put(IngredientType.WATER, "wodeo")
            .put(IngredientType.MALT, "slodeo")
            .put(IngredientType.HOP, "chmieleo")
            .put(IngredientType.YEAST, "drozdzeo")
            .build();
    private String rootUrl = "http://localhost:8030";
    private Integer threshold = 1000;

    public List<String> getListOfServiceNames(Order order) {
        return serviceNames.entrySet()
                .stream()
                .filter((entry -> order.getItems().contains(entry.getKey())))
                .map((Map.Entry::getValue))
                .collect(Collectors.toList());
    }
}
