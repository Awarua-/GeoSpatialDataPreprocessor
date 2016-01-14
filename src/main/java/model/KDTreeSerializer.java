package model;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


public class KDTreeSerializer implements JsonSerializer<KDTree> {
    @Override
    public JsonElement serialize(KDTree kdTree, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonQuadrants = new JsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        jsonQuadrants.addProperty("maxDepth", kdTree.getTreeDepth());
        OffsetDateTime startDate = kdTree.getStartDate();
        if (startDate != null) {
            jsonQuadrants.addProperty("startDate", startDate.format(formatter));
        }
        jsonQuadrants.addProperty("endDate", kdTree.getEndDate().format(formatter));
        for (Quadrant quadrant : kdTree.getQuadrants()) {
            final JsonObject jsonObject = new JsonObject();
            JsonObject coordinate = new JsonObject();
            coordinate.addProperty("latitude", quadrant.getOrigin().getLatitude());
            coordinate.addProperty("longitude", quadrant.getOrigin().getLongitude());
            jsonObject.add("origin", coordinate);
            jsonObject.addProperty("radius", quadrant.getRadius());
            jsonObject.addProperty("numberOfPoints", quadrant.getPoints().size());

//            JsonObject points = new JsonObject();
//            for (Point point : quadrant.getPoints()) {
//                JsonObject jsonPoint = new JsonObject();
//                jsonPoint.addProperty("latitude", point.getLatitude());
//                jsonPoint.addProperty("longitude", point.getLongitude());
//                jsonPoint.addProperty("date", point.getDate().toString());
//                jsonPoint.addProperty("user_id", point.getUserId());
//                points.add(String.valueOf(point.getId()), jsonPoint);
//            }
//            jsonObject.add("points", points);
            jsonQuadrants.add("q" + quadrant.getId().toString(), jsonObject);
        }
        return jsonQuadrants;
    }
}
