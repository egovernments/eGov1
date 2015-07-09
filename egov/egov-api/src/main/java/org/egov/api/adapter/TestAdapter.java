package org.egov.api.adapter;

import java.lang.reflect.Type;

import org.egov.pgr.entity.Complaint;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

public class TestAdapter extends DataAdapter<Complaint> {

	@Override
	public JsonElement serialize(Complaint complaint, Type typeOfSrc, JsonSerializationContext context) {
		JsonObject jo = new JsonObject();
		jo.addProperty("detail", complaint.getDetails());
		jo.addProperty("crn", complaint.getCrn());
		jo.addProperty("status", complaint.getStatus().getName());
		jo.addProperty("statusDetail", complaint.getStateDetails());
		return jo;
	}
}