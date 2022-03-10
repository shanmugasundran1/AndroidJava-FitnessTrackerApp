package com.easyfitness.DAO.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.easyfitness.DAO.DAOMachine;
import com.easyfitness.DAO.Machine;
import com.easyfitness.DAO.Profile;
import com.easyfitness.DAO.Weight;
import com.easyfitness.enums.DistanceUnit;
import com.easyfitness.enums.ExerciseType;
import com.easyfitness.enums.RecordType;
import com.easyfitness.enums.WeightUnit;

import com.easyfitness.utils.DateConverter;
import com.easyfitness.enums.ProgramRecordStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAOFonte extends DAORecord {

    public static final int SUM_FCT = 0;
    public static final int MAX1_FCT = 1;
    public static final int MAX5_FCT = 2;
    public static final int NBSERIE_FCT = 3;
    public static final int TOTAL_REP_FCT = 4;
    public static final int MAX_REP_FCT = 5;
    public static final int ONEREPMAX_FCT = 6;

    private static final String TABLE_ARCHI = KEY + "," + DATE + "," + EXERCISE + "," + SETS + "," + REPS + "," + WEIGHT + "," + WEIGHT_UNIT + "," + PROFILE_KEY + "," + NOTES + "," + EXERCISE_KEY + "," + TIME;

    public DAOFonte(Context context) {
        super(context);
    }

    /**
     * @param pDate       Date
     * @param pExercise   Machine name
     * @param pWeightUnit
     * @param pProfileId
     */
    public long addBodyBuildingRecord(Date pDate, String pExercise, int pSets, int pReps, float pWeight, WeightUnit pWeightUnit, String pNote, long pProfileId, long pTemplateRecordId) {
        return addRecord(pDate, pExercise, ExerciseType.STRENGTH, pSets, pReps, pWeight, pWeightUnit, 0, 0, DistanceUnit.KM, 0, pNote, pProfileId, pTemplateRecordId, RecordType.FREE_RECORD_TYPE);
    }

    public long addWeightRecordToProgramTemplate(long pTemplateId, long pTemplateSessionId, Date pDate, String pExerciseName, int pSets, int pReps, float pWeight, WeightUnit pWeightUnit, int restTime) {
        return addRecord(pDate, pExerciseName, ExerciseType.STRENGTH, pSets, pReps, pWeight,
                pWeightUnit, "", 0, DistanceUnit.KM, 0, 0, -1,
                RecordType.TEMPLATE_TYPE, -1, pTemplateId, pTemplateSessionId,
                restTime, ProgramRecordStatus.NONE);
    }

    /**
     * @param fonteList List of Fonte records
     */
    public void addBodyBuildingList(List<Record> fonteList) {
        addList(fonteList);
    }


    /**
     * @return the number of series for this machine for this day
     */
    public int getNbSeries(Date pDate, String pMachine, Profile pProfile) {

        if (pProfile == null) return 0;
        int lReturn = 0;

        //Test is Machine exists. If not create it.
        DAOMachine lDAOMachine = new DAOMachine(mContext);
        long machine_key = lDAOMachine.getMachine(pMachine).getId();

        String lDate = DateConverter.dateToDBDateStr(pDate);

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;

        // Select All Machines
        String selectQuery = "SELECT SUM(" + SETS + ") FROM " + TABLE_NAME
                + " WHERE " + LOCAL_DATE + "=\"" + lDate + "\" AND " + EXERCISE_KEY + "=" + machine_key
                + " AND " + PROFILE_KEY + "=" + pProfile.getId()
                + " AND " + TEMPLATE_RECORD_STATUS + "!=" + ProgramRecordStatus.PENDING.ordinal()
                + " AND " + RECORD_TYPE + "!=" + RecordType.TEMPLATE_TYPE.ordinal();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        mCursor.moveToFirst();
        try {
            lReturn = mCursor.getInt(0);
        } catch (NumberFormatException e) {
            //Date date = new Date();
            lReturn = 0; // Return une valeur
        }

        close();

        // return value
        return lReturn;
    }

    /**
     * @return the total weight for this machine for this day
     */
    public float getTotalWeightMachine(Date pDate, String pMachine, Profile pProfile) {
        if (pProfile == null) return 0;
        float lReturn = 0;

        //Test is Machine exists. If not create it.
        DAOMachine lDAOMachine = new DAOMachine(mContext);
        long machine_key = lDAOMachine.getMachine(pMachine).getId();

        String lDate = DateConverter.dateToDBDateStr(pDate);

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        // Select All Machines
        String selectQuery = "SELECT " + SETS + ", " + WEIGHT + ", " + REPS + " FROM " + TABLE_NAME
                + " WHERE " + LOCAL_DATE + "=\"" + lDate + "\" AND " + EXERCISE_KEY + "=" + machine_key
                + " AND " + PROFILE_KEY + "=" + pProfile.getId()
                + " AND " + TEMPLATE_RECORD_STATUS + "!=" + ProgramRecordStatus.PENDING.ordinal()
                + " AND " + RECORD_TYPE + "!=" + RecordType.TEMPLATE_TYPE.ordinal();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                float value = mCursor.getInt(0) * mCursor.getFloat(1) * mCursor.getInt(2);
                lReturn += value;
                i++;
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return lReturn;
    }


    /**
     * @return the total weight for this day
     */
    public float getTotalWeightSession(Date pDate, Profile pProfile) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        float lReturn = 0;

        String lDate = DateConverter.dateToDBDateStr(pDate);

        // Select All Machines
        String selectQuery = "SELECT " + SETS + ", " + WEIGHT + ", " + REPS + " FROM " + TABLE_NAME
                + " WHERE " + LOCAL_DATE + "=\"" + lDate + "\""
                + " AND " + PROFILE_KEY + "=" + pProfile.getId()
                + " AND " + TEMPLATE_RECORD_STATUS + "<" + ProgramRecordStatus.PENDING.ordinal()
                + " AND " + RECORD_TYPE + "!=" + RecordType.TEMPLATE_TYPE.ordinal();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            int i = 0;
            do {
                float value = mCursor.getInt(0) * mCursor.getFloat(1) * mCursor.getInt(2);
                lReturn += value;
                i++;
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return lReturn;
    }

    /**
     * @return Max weight for a profile p and a machine m
     */
    public Weight getMax(Profile p, Machine m) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        Weight w = null;

        // Select All Machines
        String selectQuery = "SELECT MAX(" + WEIGHT + "), " + WEIGHT_UNIT + " FROM " + TABLE_NAME
                + " WHERE " + PROFILE_KEY + "=" + p.getId() + " AND " + EXERCISE_KEY + "=" + m.getId()
                + " AND " + TEMPLATE_RECORD_STATUS + "<" + ProgramRecordStatus.PENDING.ordinal()
                + " AND " + RECORD_TYPE + "!=" + RecordType.TEMPLATE_TYPE.ordinal();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                w = new Weight(mCursor.getFloat(0), WeightUnit.fromInteger(mCursor.getInt(1)));
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return w;
    }

    /**
     * @return Min weight for a profile p and a machine m
     */
    public Weight getMin(Profile p, Machine m) {

        SQLiteDatabase db = this.getReadableDatabase();
        mCursor = null;
        Weight w = null;

        // Select All Machines
        String selectQuery = "SELECT MIN(" + WEIGHT + "), " + WEIGHT_UNIT + " FROM " + TABLE_NAME
                + " WHERE " + PROFILE_KEY + "=" + p.getId() + " AND " + EXERCISE_KEY + "=" + m.getId()
                + " AND " + TEMPLATE_RECORD_STATUS + "<" + ProgramRecordStatus.PENDING.ordinal()
                + " AND " + RECORD_TYPE + "!=" + RecordType.TEMPLATE_TYPE.ordinal();
        mCursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (mCursor.moveToFirst()) {
            do {
                w = new Weight(mCursor.getFloat(0), WeightUnit.fromInteger(mCursor.getInt(1)));
            } while (mCursor.moveToNext());
        }
        close();

        // return value
        return w;
    }

    public void populate() {
        // DBORecord(long id, Date pDate, String pMachine, int pSerie, int
        // pRepetition, int pPoids)
        Date date = DateConverter.timeToDate(12, 34, 56);
        int poids = 10;

        for (int i = 1; i <= 5; i++) {
            String machine = "Biceps";
            date.setDate(date.getDay() + i * 10);
            addBodyBuildingRecord(date, machine, i * 2, 10 + i, poids * i, WeightUnit.KG, "", mProfile.getId(), -1);
        }

        date = DateConverter.timeToDate(12, 34, 56);
        poids = 12;

        for (int i = 1; i <= 5; i++) {
            String machine = "Dev Couche";
            date.setDate(date.getDay() + i * 10);
            addBodyBuildingRecord(date, machine, i * 2, 10 + i, poids * i, WeightUnit.KG, "", mProfile.getId(), -1);
        }
    }

}
