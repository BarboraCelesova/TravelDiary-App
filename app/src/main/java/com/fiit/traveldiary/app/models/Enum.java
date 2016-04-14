package com.fiit.traveldiary.app.models;

import org.json.JSONObject;

/**
 * Created by jdubec on 13/04/16.
 */
public class Enum extends Model {

	private long id;
	private String code;
	private String description;

	public Enum() {
	}

	public Enum(long id, String code, String description) {
		this.id = id;
		this.code = code;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public String getCode() {
		return code;
	}

	public String getDescription() {
		return description;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public JSONObject toJSON() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean parseJSON(JSONObject jsonObject) {
		return false;
	}

	public String toString() {
		return this.description;
	}

}
