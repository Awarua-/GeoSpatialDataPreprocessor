package controller;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;


public class BitMapSerializer implements JsonSerializer<OutputBitMap> {
        @Override
        public JsonElement serialize(OutputBitMap map, Type type, JsonSerializationContext jsonSerializationContext) {
            JsonObject jsonQuadrants = new JsonObject();
            DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;
            OffsetDateTime startDate = map.getStartDate();
            OffsetDateTime endDate = map.getEndDate();
            if (startDate != null) {
                jsonQuadrants.addProperty("startDate", startDate.format(formatter));
            }
            if (endDate != null) {
                jsonQuadrants.addProperty("endDate", endDate.format(formatter));
            }
            JsonArray jsonArray = new JsonArray();
            for (BitMap point : map.getPoints()) {
                if (point != null) {
                    final JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("latitude", point.getCoordinate().getLatitude());
                    jsonObject.addProperty("longitude", point.getCoordinate().getLongitude());
                    jsonObject.addProperty("radius", point.getRadius());
                    jsonObject.addProperty("numberOfPoints", point.getCount());

                    jsonArray.add(jsonObject);
                }
            }
            jsonQuadrants.add("points", jsonArray);
            return jsonQuadrants;
        }
}
