package model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class QuadTreeSerializer implements JsonSerializer<QuadTree> {
    @Override
    public JsonElement serialize(QuadTree kdTree, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject jsonQuadrants = new JsonObject();
        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
        jsonQuadrants.addProperty("maxDepth", kdTree.getTreeDepth());
        OffsetDateTime startDate = kdTree.getStartDate();
        OffsetDateTime endDate = kdTree.getEndDate();
        if (startDate != null) {
            jsonQuadrants.addProperty("startDate", startDate.format(formatter));
        }
        if (endDate != null) {
            jsonQuadrants.addProperty("endDate", endDate.format(formatter));
        }
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
