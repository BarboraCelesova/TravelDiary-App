package com.fiit.traveldiary.app.db.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;
import com.fiit.traveldiary.app.db.TravelDiaryContract;
import com.fiit.traveldiary.app.db.provider.SQLiteProvider;
import com.fiit.traveldiary.app.exceptions.RecordNotFoundException;
import com.fiit.traveldiary.app.models.Location;
import com.fiit.traveldiary.app.models.Photo;
import com.fiit.traveldiary.app.models.Record;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub Dubec on 17/04/16.
 */
public abstract class RecordHelper {

	public static long persist(Record model) {
		SQLiteDatabase db = SQLiteProvider.getInstance().getWritableDatabase();

		boolean exists = true;

		try {
			Record originalModel = getOne(String.format("WHERE %s = '%s'", TravelDiaryContract.RecordEntry.COLUMN_UUID, model.getUuid()));
			model.setId(originalModel.getId());
			model.setIdTrip(originalModel.getIdTrip());
		}
		catch (RecordNotFoundException e) {
			exists = false;
		}

		ContentValues contentValues = new ContentValues();

		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_ID_TRIP, model.getTrip().getId());
		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD_TYPE, model.getRecordType().getId());
		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_UUID, model.getUuid());
		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_DESCRIPTION, model.getDescription());

		if (model.getUser() != null)
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_ID_USER, model.getUser().getId());

		if (model.getLocation() != null) {
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_LATITUDE, model.getLocation().getLatitude());
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_LONGITUDE, model.getLocation().getLongitude());
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_ALTITUDE, model.getLocation().getAltitude());
		}

		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_DAY, model.getDayAsString("yyyy-MM-dd'T'HH:mm:ssZ"));

		if (model.getCreatedAt() != null)
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_UPDATED_AT, model.getUpdatedAtAsString("yyyy-MM-dd'T'HH:mm:ssZ"));

		if (model.getUpdatedAt() != null)
			contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_CREATED_AT, model.getCreatedAtAsString("yyyy-MM-dd'T'HH:mm:ssZ"));

		contentValues.put(TravelDiaryContract.RecordEntry.COLUMN_SYNC, model.getSyncStatus().toString());

		if (exists) {
			String selection = TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD + " LIKE ?";
			String[] selectionArgs = { String.valueOf(model.getId()) };
			db.update(TravelDiaryContract.RecordEntry.TABLE_NAME, contentValues, selection, selectionArgs);
		}
		else {
			long primaryKey;
			primaryKey = db.insert(TravelDiaryContract.RecordEntry.TABLE_NAME, null, contentValues);
			model.setId(primaryKey);
		}

		for (Photo photo : model.getPhotos()) {
			PhotoHelper.persist(photo);
		}

		return model.getId();
	}

	public static boolean remove(Record model) {
		SQLiteDatabase db = SQLiteProvider.getInstance().getWritableDatabase();

		String selection = TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD + " LIKE ?";
		String[] selectionArgs = { String.valueOf(model.getId()) };

		return db.delete(TravelDiaryContract.RecordEntry.TABLE_NAME, selection, selectionArgs) != 0;
	}

	public static List<Record> getAll(String filter) {
		List<Record> records = new ArrayList<Record>();
		SQLiteDatabase db = SQLiteProvider.getInstance().getReadableDatabase();

		String sql = String.format("SELECT * FROM %s %s;", TravelDiaryContract.RecordEntry.TABLE_NAME, filter);

		Cursor c = db.rawQuery(sql, null);

		if (c.moveToFirst()) {
			do {
				Record record = new Record();
				record.setId(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD)));
				record.setIdRecordType(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD_TYPE)));
				record.setIdUser(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_USER)));
				record.setIdTrip(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_TRIP)));
				record.setDayFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_DAY)), "yyyy-MM-dd'T'HH:mm:ssZ");
				record.setUuid(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_UUID)));

				record.setLocation(new Location(
						c.getDouble(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_LATITUDE)),
						c.getDouble(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_LONGITUDE)),
						c.getInt(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ALTITUDE))
				));

				record.setUpdatedAtFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_UPDATED_AT)), "yyyy-MM-dd'T'HH:mm:ss'Z'");
				record.setCreatedAtFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_CREATED_AT)), "yyyy-MM-dd'T'HH:mm:ss'Z'");

				records.add(record);
			} while (c.moveToNext());
		}

		c.close();
		return records;
	}

	@NonNull
	public static Record getOne(String filter) throws RecordNotFoundException {

		SQLiteDatabase db = SQLiteProvider.getInstance().getReadableDatabase();

		String sql = String.format("SELECT * FROM %s %s LIMIT 1;", TravelDiaryContract.RecordEntry.TABLE_NAME, filter);

		Cursor c = db.rawQuery(sql, null);

		if (!c.moveToFirst()) {
			c.close();
			throw new RecordNotFoundException();
		}

		Record record = new Record();

		record.setId(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD)));
		record.setIdRecordType(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_RECORD_TYPE)));
		record.setIdUser(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_USER)));
		record.setIdTrip(c.getLong(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ID_TRIP)));
		record.setDayFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_DAY)), "yyyy-MM-dd'T'HH:mm:ssZ");
		record.setUuid(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_UUID)));

		record.setLocation(new Location(
				c.getDouble(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_LATITUDE)),
				c.getDouble(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_LONGITUDE)),
				c.getInt(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_ALTITUDE))
		));

		record.setUpdatedAtFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_UPDATED_AT)), "yyyy-MM-dd'T'HH:mm:ss'Z'");
		record.setCreatedAtFromString(c.getString(c.getColumnIndex(TravelDiaryContract.RecordEntry.COLUMN_CREATED_AT)), "yyyy-MM-dd'T'HH:mm:ss'Z'");

		c.close();

		return record;

	}
}
