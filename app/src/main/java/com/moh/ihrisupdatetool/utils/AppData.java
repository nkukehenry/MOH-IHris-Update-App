package com.moh.ihrisupdatetool.utils;

import com.moh.ihrisupdatetool.db.entities.CommunityWorkerEntity;
import com.moh.ihrisupdatetool.db.entities.DistrictEntity;
import com.moh.ihrisupdatetool.db.entities.FormEntity;
import com.moh.ihrisupdatetool.db.entities.MinistryWorkerEntity;

import java.util.ArrayList;
import java.util.List;

public class AppData {

    public static List<FormEntity> allForms = new ArrayList<>();
    public static FormEntity selectedForm;
    public static DistrictEntity selectedDistrict;
    public static CommunityWorkerEntity selectedCommunityWorker = null;
    public static MinistryWorkerEntity selectedMinistryWorker = null;
    public static Boolean isCommunityWorker = false;
    public static int userId;
}
