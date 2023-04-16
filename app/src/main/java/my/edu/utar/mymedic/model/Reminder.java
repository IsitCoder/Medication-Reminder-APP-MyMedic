package my.edu.utar.mymedic.model;

public class Reminder {
    int id;
    int medicineId;
    String medicineName;
    String startDate;
    String endDate;
    String alarmTime;

    public Reminder(int id, int medicineId, String medicineName, String startDate, String endDate, String alarmTime) {
        this.id = id;
        this.medicineId = medicineId;
        this.medicineName = medicineName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.alarmTime = alarmTime;
    }
}
