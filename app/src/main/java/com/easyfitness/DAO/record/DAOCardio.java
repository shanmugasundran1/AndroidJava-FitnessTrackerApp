package com.easyfitness.DAO.record;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.easyfitness.DAO.Profile;
import com.easyfitness.R;
import com.easyfitness.enums.DistanceUnit;
import com.easyfitness.enums.ExerciseType;
import com.easyfitness.enums.ProgramRecordStatus;
import com.easyfitness.enums.RecordType;
import com.easyfitness.enums.WeightUnit;

import com.easyfitness.utils.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DAOCardio extends DAORecord {

    public static final int DISTANCE_FCT = 0;
    public static final int DURATION_FCT = 1;
    public static final int SPEED_FCT = 2;
    public static final int MAXDURATION_FCT = 3;
    public static final int MAXDISTANCE_FCT = 4;
    public static final int NBSERIE_FCT = 5;

    private static final String OLD_TABLE_NAME = "EFcardio";

    private static final String TABLE_ARCHI = KEY + "," + DATE + "," + EXERCISE + "," + DISTANCE + "," + DURATION + "," + PROFILE_KEY + "," + TIME + "," + DISTANCE_UNIT;

    public DAOCardio(Context context) {
        super(context);
        mContext = context;
    }

    /**
     * @param pDate
     * @param pMachine
     * @param pDistance
     * @param pDuration
     * @param pProfileId
     * @return
     */
    public long addCardioRecord(Date pDate, String pMachine, float pDistance, long pDuration, long pProfileId, DistanceUnit pDistanceUnit, long pTemplateRecordId) {
        return addRecord(pDate, pMachine, ExerciseType.CARDIO, 0, 0, 0, WeightUnit.KG, 0, pDistance, pDistanceUnit, pDuration, "", pProfileId, pTemplateRecordId, RecordType.FREE_RECORD_TYPE);
    }

    public long addCardioRecordToProgramTemplate(long pTemplateId, long pTemplateSessionId, Date pDate, String pExerciseName, float pDistance, DistanceUnit pDistanceUnit, long pDuration, int restTime) {
        return addRecord(pDate, pExerciseName, ExerciseType.CARDIO, 0, 0, 0,
                WeightUnit.KG, "", pDistance, pDistanceUnit, pDuration, 0, -1,
                RecordType.TEMPLATE_TYPE, -1, pTemplateId, pTemplateSessionId,
                restTime, ProgramRecordStatus.NONE);
    }

}
