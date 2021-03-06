package com.fiit.traveldiary.app.db.helpers;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.fiit.traveldiary.app.db.TravelDiaryContract;
import com.fiit.traveldiary.app.db.provider.SQLiteProvider;
import com.fiit.traveldiary.app.exceptions.RecordNotFoundException;
import com.fiit.traveldiary.app.models.Status;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jakub Dubec on 17/04/16.
 */
public abstract class StatusHelper {

	public static long save(Status status) {

		SQLiteDatabase db = SQLiteProvider.getInstance().getWritableDatabase();

		ContentValues contentValues = new ContentValues();

		contentValues.put(TravelDiaryContract.StatusEntry.COLUMN_CODE, status.getCode());
		contentValues.put(TravelDiaryContract.StatusEntry.COLUMN_DESCRIPTION, status.getDescription());

		status.setId(db.insert(TravelDiaryContract.StatusEntry.TABLE_NAME, null, contentValues)); //insert a rovno nasetuj ID

		return status.getId();

	}

	public static List<Status> getAll() {

		SQLiteDatabase db = SQLiteProvider.getInstance().getReadableDatabase();

		String sql = String.format("SELECT * FROM %s", TravelDiaryContract.StatusEntry.TABLE_NAME);

		Cursor c = db.rawQuery(sql, null);

		List<Status> statusList = new ArrayList<Status>();

		if (c.moveToFirst()) {

			do {
				Status status = new Status(
						c.getLong(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_ID_STATUS)),
						c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_CODE)),
						c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_DESCRIPTION))
				);

				statusList.add(status);
			} while (c.moveToNext());
		}

		c.close();

		return statusList;

	}

	public static Status get(String code) throws RecordNotFoundException {

		SQLiteDatabase db = SQLiteProvider.getInstance().getReadableDatabase();

		String sql = String.format("SELECT * FROM %s WHERE %s = '%s' LIMIT 1;", TravelDiaryContract.StatusEntry.TABLE_NAME, TravelDiaryContract.StatusEntry.COLUMN_CODE, code);

		Cursor c = db.rawQuery(sql, null);

		if (!c.moveToFirst())
			throw new RecordNotFoundException();

		Status status = new Status(
				c.getLong(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_ID_STATUS)),
				c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_CODE)),
				c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_DESCRIPTION))
		);

		c.close();

		return status;
	}

	public static Status get(long id) throws RecordNotFoundException {

		SQLiteDatabase db = SQLiteProvider.getInstance().getReadableDatabase();

		String sql = String.format("SELECT * FROM %s WHERE %s = %d LIMIT 1;", TravelDiaryContract.StatusEntry.TABLE_NAME, TravelDiaryContract.StatusEntry.COLUMN_ID_STATUS, id);
		Cursor c = db.rawQuery(sql, null);

		if (!c.moveToFirst())
			throw new RecordNotFoundException();

		Status status = new Status(
				c.getLong(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_ID_STATUS)),
				c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_CODE)),
				c.getString(c.getColumnIndex(TravelDiaryContract.StatusEntry.COLUMN_DESCRIPTION))
		);

		c.close();

		return status;
	}

	public static boolean removeAll() {
		SQLiteDatabase db = SQLiteProvider.getInstance().getWritableDatabase();
		return db.delete(TravelDiaryContract.StatusEntry.TABLE_NAME, null, null) != 0;
	}

}
